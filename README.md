# Notificação

Microsserviço Spring Boot responsável por enviar e-mails de notificação de tarefas. Recebe os dados de uma tarefa via API REST, monta um template HTML com Thymeleaf e envia o e-mail ao usuário.

## Tecnologias

- Java 17
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Mail (SMTP)
- Thymeleaf
- Lombok
- Gradle

## Estrutura do projeto

```
notificacao/
├── src/main/java/com/javanauta/notificacao/
│   ├── controller/EmailController.java
│   ├── business/EmailService.java
│   ├── business/dto/TarefasDTO.java
│   ├── business/enums/StatusNotificacao.java
│   └── business/exception/EmailException.java
└── src/main/resources/
    ├── application.yaml
    └── templates/notificacao.html
```

## Pré-requisitos

- JDK 17+
- Conta Gmail com **Senha de app** configurada (o Gmail não aceita mais a senha normal de login para SMTP)

### Como gerar a Senha de app do Gmail

1. Ative a verificação em duas etapas em [Conta Google > Segurança](https://myaccount.google.com/security)
2. Gere uma senha de app em [Senhas de app](https://myaccount.google.com/apppasswords)
3. Copie a senha de 16 caracteres (sem espaços) e configure no `application.yaml`

## Configuração

Edite o arquivo `notificacao/src/main/resources/application.yaml`:

```yaml
spring:
  mail:
    username: seuemail@gmail.com
    password: sua-senha-de-app

envio:
  email:
    remetente: seuemail@gmail.com
    nomeRemetente: Seu Nome

server:
  port: 8082
```

> **Importante:** `spring.mail.username` e `envio.email.remetente` devem ser o mesmo e-mail Gmail usado para gerar a senha de app. Nunca commite senhas reais no repositório.

## Como executar

Entre na pasta do módulo Gradle:

```bash
cd notificacao
```

**Windows:**

```bash
gradlew.bat bootRun
```

**Linux / macOS:**

```bash
./gradlew bootRun
```

A aplicação sobe em `http://localhost:8082`.

## API

### Enviar e-mail de notificação

| Método | Endpoint        | Descrição                    |
|--------|-----------------|------------------------------|
| POST   | `/email`        | Envia e-mail de notificação  |

**Headers:**

```
Content-Type: application/json
```

**Body (JSON):**

```json
{
  "id": "1",
  "nomeTarefa": "Reunião com o cliente",
  "descricao": "Apresentar o projeto final e alinhar os próximos passos.",
  "dataCriacao": "31-08-2026 14:00:00",
  "dataEvento": "31-08-2026 16:30:00",
  "emailUsuario": "destinatario@gmail.com",
  "dataAlteracao": "31-08-2026 14:00:00",
  "statusNotificacao": "PENDENTE"
}
```

**Campos utilizados no e-mail:**

| Campo          | Descrição                          |
|----------------|------------------------------------|
| `nomeTarefa`   | Nome da tarefa exibido no template |
| `descricao`    | Descrição da tarefa                |
| `dataEvento`   | Data/hora do evento                |
| `emailUsuario` | E-mail do destinatário             |

**Formato de datas:** `dd-MM-yyyy HH:mm:ss`

**Valores de `statusNotificacao`:** `PENDENTE`, `NOTIFICADO`, `CANCELADO`

**Respostas:**

| Status | Descrição              |
|--------|------------------------|
| 200    | E-mail enviado         |
| 500    | Erro ao enviar e-mail  |

## Testando com Postman

1. Método: **POST**
2. URL: `http://localhost:8082/email`
3. Body: **raw** → **JSON**
4. Cole o JSON de exemplo acima e altere `emailUsuario` para um e-mail válido

## Build e testes

```bash
cd notificacao
./gradlew build
./gradlew test
```

## CI

O projeto possui workflow GitHub Actions em `.github/workflows/gradle.yml` que executa build e testes em pull requests para a branch `master`.

## Licença

Projeto educacional — Javanauta.
