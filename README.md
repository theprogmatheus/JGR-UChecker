# JGR-UChecker 🚀

**JGR-UChecker (Java Github Releases Update Checker)** é uma biblioteca Java simples e eficiente para verificar se há atualizações disponíveis em um repositório do GitHub. Com ela, você pode facilmente comparar a versão atual do seu projeto com a última versão publicada no repositório, ajudando a manter seus projetos sempre atualizados com as versões mais recentes. 🆙

## ✨ Funcionalidades

- Verifique a versão mais recente de um repositório no GitHub.
- Compare a versão do seu projeto com a última versão publicada.
- Determine se uma atualização está disponível, sem precisar abrir o GitHub manualmente.

## ⚡ Como Usar

### 1. Adicionando a dependência no seu projeto

Adicione o seguinte trecho no arquivo `build.gradle` (para Gradle) ou `pom.xml` (para Maven) para incluir a biblioteca `JGR-UChecker` diretamente do GitHub Packages:

#### **Para projetos com Gradle:**

```groovy
repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/theprogmatheus/JGR-UChecker")
        credentials {
            username = project.findProperty("gpr.username") ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.token") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation 'com.github.theprogmatheus:jgr-uchecker:{version}'
}
```

#### **Para projetos com Maven:**

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/theprogmatheus/JGR-UChecker</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.theprogmatheus</groupId>
        <artifactId>jgr-uchecker</artifactId>
        <version>{version}</version>
    </dependency>
</dependencies>

<pluginRepositories>
    <pluginRepository>
        <id>github</id>
        <url>https://maven.pkg.github.com/theprogmatheus/JGR-UChecker</url>
    </pluginRepository>
</pluginRepositories>
```

#### **Autenticação no GitHub Packages:**

GitHub Packages exige autenticação para acessar os pacotes. Para poder usar esta dependência, você precisará adicionar suas credenciais do GitHub (nome de usuário e token) no seu arquivo `~/.m2/settings.xml` (para Maven) ou como propriedades no seu `build.gradle` (para Gradle).

Aqui está um exemplo de como configurar a autenticação no **Maven**:

```xml
<servers>
    <server>
        <id>github</id>
        <username>seu-usuario-github</username>
        <password>seu-token-pessoal-do-github</password>
    </server>
</servers>
```

Ou no **Gradle**:

```groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/theprogmatheus/JGR-UChecker")
        credentials {
            username = project.findProperty("gpr.username") ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.token") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
```

#### **Importante:**
A autenticação no GitHub é **obrigatória** para usar o GitHub Packages. Sem ela, você não conseguirá baixar ou utilizar o projeto. 💡

### 2. Exemplo de Uso

#### **Modo Síncrono:**

Você pode verificar a última versão de um repositório de forma **sincrona**, ou seja, o código aguardará a resposta antes de continuar a execução.

```java
import com.github.theprogmatheus.util.JGRUChecker;
import com.github.theprogmatheus.util.JGRUChecker.GithubRelease;

public class UpdateCheckerExample {
    public static void main(String[] args) {
        // Defina o nome de usuário do GitHub, repositório e a versão atual do seu projeto
        JGRUChecker checker = new JGRUChecker("theprogmatheus", "JGR-UChecker", "1.0.0-alpha");

        // Verifique se uma atualização está disponível
        boolean updateAvailable = checker.isUpdateAvailable();
        System.out.println("Há uma atualização disponível? " + (updateAvailable ? "Sim" : "Não"));

        // Caso haja uma atualização, obtenha os detalhes da última release
        if (updateAvailable) {
            GithubRelease lastRelease = checker.getLastRelease();
            System.out.println("Última versão: " + lastRelease.getVersion());
            System.out.println("Nome da versão: " + lastRelease.getName());
            System.out.println("Link para download: " + lastRelease.getDownloadPage());
        }
    }
}
```

#### **Modo Assíncrono:**

Para evitar bloquear o fluxo do seu código, você pode usar o modo **assíncrono**, que permite verificar as atualizações enquanto o código continua rodando.

```java
import com.github.theprogmatheus.util.JGRUChecker;
import com.github.theprogmatheus.util.JGRUChecker.GithubRelease;

import java.util.concurrent.CompletableFuture;

public class AsyncUpdateCheckerExample {
    public static void main(String[] args) {
        // Defina o nome de usuário do GitHub, repositório e a versão atual do seu projeto
        JGRUChecker checker = new JGRUChecker("theprogmatheus", "JGR-UChecker", "1.0.0-alpha");

        // Verifique se há uma atualização disponível de forma assíncrona
        CompletableFuture<GithubRelease> lastReleaseFuture = checker.checkAsync();
        lastReleaseFuture.thenAccept(lastRelease -> {
            if (lastRelease != null) {
                System.out.println("Última versão: " + lastRelease.getVersion());
                System.out.println("Nome da versão: " + lastRelease.getName());
                System.out.println("Link para download: " + lastRelease.getDownloadPage());
            } else {
                System.out.println("Não foi possível buscar a última versão.");
            }
        });

        // Outros códigos podem continuar a execução enquanto aguardam a resposta
        System.out.println("Verificando se há atualizações...");
    }
}
```

## 🔧 Funcionalidades

- **`check()`**: Verifica a última versão de forma **sincrona**.
- **`checkAsync()`**: Verifica a última versão de forma **assíncrona**, retornando um `CompletableFuture`.
- **`getLastRelease()`**: Retorna a última versão como um objeto `GithubRelease` (sincronamente).
- **`isUpdateAvailable()`**: Verifica se uma atualização está disponível comparando a versão atual com a versão mais recente.

### Detalhes da `GithubRelease`

A classe `GithubRelease` contém os seguintes campos:

- `id`: ID da release no GitHub.
- `name`: Nome da release (geralmente o título da versão).
- `version`: A versão da release.
- `downloadPage`: URL para a página de download da release.

## 🚀 Como Funciona

1. Você fornece o nome de usuário do GitHub, o repositório e a versão atual do seu projeto.
2. A biblioteca consulta a API pública do GitHub para buscar as informações da última release do repositório.
3. Ela compara a versão mais recente com a versão que você está utilizando e determina se há uma atualização disponível.
4. Se houver, você pode obter mais informações sobre a release, como o nome, a versão e o link para o download.

## 📦 Dependências

O `JGR-UChecker` depende de:

- **Lombok**: Para reduzir o código boilerplate, como `@Data`, `@ToString`, entre outros.
- **json-simple**: Para fazer o parse do JSON retornado pela API do GitHub.

## 🔗 Links Úteis

- [GitHub do Projeto](https://github.com/theprogmatheus/JGR-UChecker)
- [GitHub Packages](https://maven.pkg.github.com/theprogmatheus/JGR-UChecker)

## 📝 Licença

Este projeto está licenciado sob a [MIT License](LICENSE).

---

Sinta-se à vontade para contribuir, reportar problemas ou sugerir melhorias! 😄