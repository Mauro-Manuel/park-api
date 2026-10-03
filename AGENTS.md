# Instruções para agentes

## Comunicação

- Responde sempre em português.
- Mantém código, nomes de classes, métodos, variáveis, endpoints e termos técnicos em inglês quando apropriado.
- Antes de alterar vários ficheiros, explica as alterações planeadas.
- Ao sugerir mensagens de commit, usa Conventional Commits e escreve a mensagem em inglês.

## Tecnologias

- Usa Java 21, Spring Boot 4, Maven e MySQL.
- Não adiciona novas dependências sem necessidade; reutiliza as existentes sempre que possível.
- Não altera configurações da base de dados sem explicar o motivo.

## Arquitetura e estrutura

- Segue a arquitetura em camadas existente: Controller → Service → Repository.
- Mantém os controllers responsáveis pelas requisições e respostas HTTP, os services pelas regras de negócio e transações, e os repositories pelo acesso aos dados.
- Usa DTOs para entrada e saída da API; não expõe entities diretamente nas respostas.
- Usa Bean Validation para validação das requisições.
- Mantém o tratamento de exceções centralizado em `web.exception`.
- Respeita a estrutura de packages sob `com.masprog.park_api`, em `src/main/java/com/masprog/park_api`:
  - Package raiz: arranque da aplicação com `ParkApiApplication`.
  - `config`: configuração da aplicação, OpenAPI e timezone.
  - `entity`: entities JPA e representação dos dados persistidos.
  - `repository`: acesso aos dados com Spring Data JPA.
  - `service`: regras de negócio e transações.
  - `exception`: exceções de negócio.
  - `web.controller`: endpoints REST.
  - `web.dto`: DTOs de entrada e saída.
  - `web.dto.mapper`: conversão entre DTOs e entities.
  - `web.exception`: tratamento centralizado de exceções e respostas de erro.
- Mantém a configuração da aplicação em `src/main/resources` e os testes e respetivos recursos em `src/test/java` e `src/test/resources`.

## Testes

- Usa JUnit 5 para testes.
- Usa Mockito para testes unitários.
- Usa testes de integração para endpoints REST quando apropriado.
- Depois de implementar código, executa os testes relevantes com Maven.
- Se os testes falharem, explica primeiro a causa antes de fazer alterações não relacionadas. Se a causa ainda não estiver identificada, indica essa limitação e investiga-a.

## Segurança

- Nunca exponhas passwords, credenciais, tokens ou secrets em respostas, logs, exemplos ou documentação.
- Nunca incluas secrets em commits.

## Âmbito das alterações e Git

- Prefere alterações pequenas e focadas.
- Preserva o comportamento atual da API, exceto quando a tarefa exigir explicitamente uma alteração.
- Não faças commits ou push automaticamente.
- Não cries nem faças merge de Pull Requests automaticamente.
