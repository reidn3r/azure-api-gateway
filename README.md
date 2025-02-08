# URL Shortener Backend - Java + Azure + Spring Cloud Gateway

## Visão Geral
Este projeto consiste em um encurtador de URLs baseado em Java, utilizando tecnologias escaláveis. O sistema é composto por:

1. **API Gateway**: Implementado com **Spring Cloud Gateway**, rodando em um contêiner Docker.
2. **Função Serverless (Encurtamento de URL)**: Implementada como uma **Azure Function** em Java, recebe URLs de entrada e retorna uma URL encurtada com tempo de expiração.
3. **Função Serverless (Redirecionamento)**: Outra **Azure Function** em Java, que recebe uma URL encurtada e realiza o redirecionamento para a URL original, se ainda estiver válida.
4. **Azure Blob Storage**: Cada URL encurtada gera um **blob** no Azure Storage, armazenando os metadados da URL em formato JSON.

---

## Arquitetura
<p align="center">
    <img src="assets/azure-url-shortener.drawio.png" alt="Software Architecture">
</p>

- Gateway: Conteiner Docker (ACR)
- Encurtamento de Redirecionamento: Funções Serverless

---

## Tecnologias Utilizadas
- **Java 17**
- **Docker**
- **Spring Cloud Gateway**
- **Azure Container Registry (ACR)**
- **Azure Functions**
- **Azure Blob Storage**

---

## Endpoint do API Gateway
Todas as requisições podem ser enviadas para o seguinte endpoint (atenção ao protocolo **http**):

```
http://scg-encurtador.brazilsouth.azurecontainer.io
```

---

## Criando uma URL Encurtada
### **Endpoint:**
```
POST /create
```

### **Body da Requisição:**
```json
{
  "url": "https://exemplo.com",
  "expiresIn": 3600
}
```
- **url**: URL de origem que será encurtada.
- **expiresIn**: Tempo de expiração da URL encurtada em **segundos**.

### **Resposta Esperada:**
```json
{
  "sourceURL": "https://example.com",
  "destinyUrl": "WXYZ",
  "expiresDate": "2025-02-08T00:00:00.0000"
}
```

---

## Redirecionamento
### **Endpoint:**
```
GET http://scg-encurtador.brazilsouth.azurecontainer.io/{destinyUrl}
```

- Exemplo:
  - `GET http://scg-encurtador.brazilsouth.azurecontainer.io/abc123`
  - Se a URL ainda for válida, o usuário será redirecionado para a **URL original**.
  - Caso tenha expirado, retornará um erro informando a expiração do link.

---

## Armazenamento no Azure Blob Storage
Cada URL encurtada gera um **blob JSON** no Azure Blob Storage, contendo as seguintes informações:

```java
{
    sourceURL
    destinyUrl
    expiresDate
}
```

---

## Validação da Requisição
A requisição para encurtar URLs utiliza a seguinte classe DTO para validação:

```java
@Getter
@Setter
public class RequestDTO {
    @NotNull
    @NotBlank
    @URL
    private String url;

    @NotNull
    @Positive
    private int expiresIn;
}
```

---

## Implantação do API Gateway no Azure Container Registry
Dockerfile disponível no diretório `api-gateway`. Para criar a imagem e fazer o push para o **Azure Container Registry (ACR)**, prossegui os passos abaixo:

### **1. Construção da Imagem Docker**
No diretório `api-gateway`, execute:
```
docker build -t Nome_Imagem .
```

### **2. Tag da Imagem**
```
docker tag Nome_Imagem:latest <NOME_DO_ACR>.azurecr.io/Nome_Imagem:latest
```

### **3. Envio da Imagem para o ACR**
```
docker push <NOME_DO_ACR>.azurecr.io/Nome_Imagem:latest
```

### **4. Executando o Contêiner no Azure**
Depois de enviar a imagem, execute o contêiner no **Azure Container Instances (ACI)**:
```
az container create \
    --resource-group <NOME_DO_GRUPO> \
    --name Nome_Imagem \
    --image <NOME_DO_ACR>.azurecr.io/Nome_Imagem:latest \
    --cpu 1 --memory 1 \
    --registry-login-server <NOME_DO_ACR>.azurecr.io \
    --dns-name-label scg-encurtador \
    --os-type Linux
    --ports 80
```
