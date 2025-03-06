package com.github.theprogmatheus.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Data
public class JGRUChecker {

    private static final boolean disabled = System.getProperty("com.github.theprogmatheus.util.JGRUChecker.disabled") != null;
    private static final String apiUrl = "https://api.github.com/repos/%s/%s/releases/latest";

    private String username;
    private String repositoryName;
    private String currentVersion;

    private transient CompletableFuture<GithubRelease> lastReleaseFuture;

    public JGRUChecker(String username, String repositoryName, String currentVersion) {
        this.username = username;
        this.repositoryName = repositoryName;
        this.currentVersion = currentVersion;
    }

    public GithubRelease check() {
        return checkAsync().join();
    }

    public CompletableFuture<GithubRelease> checkAsync() {
        if (this.lastReleaseFuture == null || this.lastReleaseFuture.isCompletedExceptionally()) {
            synchronized (this) {
                if (this.lastReleaseFuture == null || this.lastReleaseFuture.isCompletedExceptionally()) {
                    this.lastReleaseFuture = CompletableFuture.supplyAsync(() -> fetchLastRelease().orElse(null));
                }
            }
        }
        return this.lastReleaseFuture;
    }

    private Optional<GithubRelease> fetchLastRelease() {
        if (disabled) return Optional.empty();

        try {
            URLConnection connection = new URL(String.format(apiUrl, this.username, this.repositoryName)).openConnection();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String bodyContent = bufferedReader.lines().collect(Collectors.joining("\n"));
            bufferedReader.close();

            JSONObject data = (JSONObject) new JSONParser().parse(bodyContent);

            String id = data.get("id").toString();
            String name = data.get("name").toString();
            String version = data.get("tag_name").toString();
            String downloadPage = data.get("html_url").toString();

            return Optional.of(new GithubRelease(id, name, version, downloadPage));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid GitHub repository URL", e);
        } catch (IOException | ParseException e) {
            throw new RuntimeException("Failed to fetch the latest release", e);
        }
    }

    public GithubRelease getLastRelease() {
        return checkAsync().join();
    }

    public boolean isUpdateAvailable() {
        GithubRelease lastRelease = getLastRelease();
        return lastRelease != null && !lastRelease.getVersion().equals(this.currentVersion);
    }

    @Data
    @AllArgsConstructor
    @ToString
    public static class GithubRelease {
        private String id;
        private String name;
        private String version;
        private String downloadPage;
    }

}
