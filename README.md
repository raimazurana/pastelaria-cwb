# Pastelaria CWB – API REST

Trabalho da disciplina **Desenvolvimento Web Back-End** (UNINTER).
API REST simples para controlar clientes, produtos e pedidos de uma loja de pastel.

**Aluna:** Raissa Mazurana – RU 3409138

**Tecnologias:** Java 17, Spring Boot 3, Spring Data JPA, H2 (ou MySQL), Postman, VS Code.

## Estudo de caso

A Pastelaria CWB cadastrou a cliente **RaissaMazurana3409138** e o produto
**Pastel de Frango Catupiry** (R$ 9,99 a unidade, em estoque). A cliente fez um
pedido de **5 pastéis**, totalizando R$ 49,95.

## Estrutura (padrão MVC)

```
src/main/java/com/pastelariacwb
├── PastelariaCwbApplication.java
├── model/        Cliente, Produto, Pedido (entidades JPA)
├── repository/   ClienteRepository, ProdutoRepository, PedidoRepository
├── controller/   ClienteController, ProdutoController, PedidoController
└── dto/          PedidoRequest (JSON de entrada do pedido)
```

## Como rodar (VS Code no Windows)

Este foi o caminho usado para executar o projeto. Não é preciso instalar o Maven:
a extensão de Java do VS Code já traz um Maven embutido.

### 1. Pré-requisitos
- **JDK 17** ou mais novo instalado (teste no terminal com `java -version`).
  Se não tiver: `Ctrl + Shift + P` → **Java: Install New JDK** → Eclipse Temurin.
- Extensões do VS Code:
  - **Extension Pack for Java** (Microsoft)
  - **Spring Boot Extension Pack**

### 2. Abrir o projeto
1. **File → Open Folder** e escolha a pasta `pastelaria-cwb` (a que contém o `pom.xml`).
2. Se aparecer **Restricted Mode** na barra inferior, clique nele e escolha **Trust**.
3. Se aparecer **Java: Lightweight Mode**, clique nele e escolha **Switch to Standard Mode**.
4. Aguarde a barra inferior mostrar **Java: Ready** (na primeira vez ele baixa as dependências).

### 3. Banco de dados (já configurado)
O projeto já está configurado para usar o banco **H2 em memória**, então não é preciso
instalar nem alterar nada. A configuração fica no final de
`src/main/resources/application.properties`:

```properties
spring.profiles.active=h2
```

Essa linha ativa o perfil definido em `application-h2.properties`, que cria as tabelas
`cliente`, `produto` e `pedido` automaticamente ao iniciar a aplicação.

> **Atenção:** o H2 apaga os dados sempre que a aplicação é parada. Ao rodar de novo,
> o banco começa vazio e os IDs voltam a começar do 1.

> **Opcional – usar MySQL:** apague a linha `spring.profiles.active=h2`, deixe o MySQL
> rodando em `localhost:3306` e ajuste `spring.datasource.username` e
> `spring.datasource.password` no mesmo arquivo. O banco `pastelaria_cwb` é criado
> automaticamente.

Console do H2 (com a API rodando): http://localhost:8080/h2-console
(JDBC URL `jdbc:h2:mem:pastelaria_cwb`, usuário `sa`, sem senha).

### 4. Executar
1. Abra `src/main/java/com/pastelariacwb/PastelariaCwbApplication.java`.
2. Clique em **Run**, logo acima de `public static void main`.
3. A API está no ar quando o terminal mostrar:
   ```
   Tomcat started on port 8080 (http) with context path '/'
   Started PastelariaCwbApplication in X seconds
   ```

Alternativa pelo terminal, se tiver o Maven instalado: `mvn spring-boot:run`.

## Endpoints

| Método | URL | Descrição |
|---|---|---|
| POST | /clientes | Cria cliente |
| GET | /clientes | Lista todos |
| GET | /clientes/{id} | Consulta por ID |
| PUT | /clientes/{id} | Atualiza (opcional) |
| DELETE | /clientes/{id} | Apaga |
| POST | /produtos | Cria produto |
| GET | /produtos | Lista todos |
| GET | /produtos/{id} | Consulta por ID |
| PUT | /produtos/{id} | Atualiza (opcional) |
| DELETE | /produtos/{id} | Apaga |
| POST | /pedidos | Cria pedido |
| GET | /pedidos | Lista todos |
| GET | /pedidos/{id} | Consulta por ID |
| PUT | /pedidos/{id} | Atualiza (opcional) |
| DELETE | /pedidos/{id} | Apaga |

### Exemplos de JSON

Cliente:
```json
{ "nome": "RaissaMazurana3409138", "clienteDesde": "2026-10-07" }
```
Produto:
```json
{ "nome": "Pastel de Frango Catupiry", "preco": 9.99, "estoque": true }
```
Pedido:
```json
{ "clienteId": 1, "produtoId": 1, "quantidade": 5 }
```

### Códigos de resposta

| Código | Quando acontece |
|---|---|
| `201 Created` | Registro criado (POST) |
| `200 OK` | Consulta ou atualização feita |
| `204 No Content` | Registro apagado (DELETE) |
| `400 Bad Request` | Pedido com cliente/produto inexistente ou quantidade inválida |
| `404 Not Found` | ID não existe |
| `409 Conflict` | Tentativa de apagar cliente/produto que já possui pedidos |

## Testes no Postman

1. Use o **Postman Desktop** (o Postman pela web não acessa `localhost` sem configuração extra).
2. **Import** → `postman/PastelariaCWB.postman_collection.json`.
3. Com a API rodando, execute as requisições **nesta ordem** (os IDs dependem dela):

| # | Requisição | Resultado esperado |
|---|---|---|
| 1 | POST /clientes | 201 – cliente id 1 |
| 2 | POST /produtos | 201 – produto id 1 |
| 3 | POST /pedidos | 201 – pedido id 1 com quantidade 5 |
| 4 | GET /clientes, /produtos, /pedidos | 200 – listagens |
| 5 | GET /clientes/1, /produtos/1, /pedidos/1 | 200 – consulta por ID |
| 6 | POST /clientes com nome "ClienteTeste" | 201 – cliente id 2 |
| 7 | DELETE /clientes/2 | 204 – apagado |
| 8 | GET /clientes/2 | 404 – confirma a exclusão |