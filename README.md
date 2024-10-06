# Desafio Backend: API de Restaurantes e Produtos 🍔

![Java](https://img.shields.io/badge/java-FF5722.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-0B3D30?style=for-the-badge&logo=mongodb&logoColor=white)
![Mongo Express](https://img.shields.io/badge/Mongo%20Express-285C35?style=for-the-badge&logo=mongodb&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![OAuth2](https://img.shields.io/badge/OAuth2-F80000?style=for-the-badge&logo=openid&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-181717?style=for-the-badge&logo=github&logoColor=white)

## O que é o desafio? 🤔

O desafio, encontrado no GitHub da Goomer, é de uma API de Restaurantes, que permite listar, cadastrar, alterar e atualizar restaurantes e seus respectivos produtos.

Como um diferencial para este desafio, eu utilizei o OAuth2 e JWT para a Autenticação, com um Authorization Server próprio, utilizando o Password Grant Type.
Além disso, inspirado neste [desafio](https://github.com/ab-inbev-ze-company/ze-code-challenges/blob/master/backend_pt.md) da Zé Delivery (que eu também já resolvi, e o repositório está [aqui](https://github.com/FernandoCanabarroAhnert/ze-delivery-desafio-backend)), eu decidi implementar a funcionalidade de realizar consultas geográficas, utilizando as GeoSpatial/GeoJson Queries do banco de dados MongoDB. Dada uma coordenada(longitude e latitude), o banco de dados procura registros próximos deste ponto, sendo possível específicar a distância mínima e/ou máxima em metros.

E em consequência disto, eu também decidi utilizar APIs externas relacionadas a dados geográficos. Eu utilizei a API da ViaCep para converter um dado CEP para um endereço, e uma API da OpenStreetView, o Nominatim, para converter um endereço em um ponto geográfico. O objetivo disto é para que um usuário, ao se cadastrar, não precise digitar todo o seu endereço, somento o CEP e o seu número, e quando ele for consultar restaurantes geograficamente próximos, ele não precise especificar as suas próprias coordenadas(longitude e latitude).

Outra funcionalidade implementada é o GitHub Actions, para realizar o CI/CD desta aplicação. A integração contínua (CI) garante que o código seja testado automaticamente a cada nova alteração, detectando erros rapidamente. A entrega e o deploy contínuos (CD) permitem que essas alterações sejam entregues automaticamente para produção ou ambientes de testes, garantindo entregas rápidas e frequentes. Neste caso, eu fiz com que este projeto fosse publicado no Docker Hub a cada novo commit.

O desafio pode ser encontrado aqui: <https://github.com/goomerdev/job-dev-backend-interview>

<p align="left" width="100%">
    <img width="25%" src="https://github.com/user-attachments/assets/bf812f68-445b-40d2-a96e-bb5eae767b7c"> 
</p>

## Requisitos da Aplicação ✅

Esses foram os requisitos definidos no enunciado original:

A sua API deverá ser capaz de:

- Listar todos os restaurantes
- Cadastrar novos restaurantes
- Listar os dados de um restaurante
- Alterar os dados um restaurante
- Excluir um restaurante
- Listar todos os produtos de um restautante
- Criar um produto de um restaurante
- Alterar um produto de um restaurante
- Excluir um produto de um restaurante

O cadastro do restaurante precisa ter os seguintes campos:

- Foto do restaurante
- Nome do restaurante
- Endereço do restaurante
- Horários de funcionamento do restaurante (ex.: De Segunda à Sexta das 09h as 18h e de Sabado à Domingo das 11h as 20h).

O cadastro de produtos do restaurante precisa ter os seguintes campos:

- Foto do produto
- Nome do produto
- Preço do produto
- Categoria do produto (ex.: Doce, Salgados, Sucos...)

Quando o Produto for colocado em promoção, precisa ter os seguintes campos:

- Descrição para a promoção do produto (ex.: Chopp pela metade do preço)
- Preço promocional
- Dias da semana e o horário em que o produto deve estar em promoção

### Serviço RESTful 🚀

* Desenvolvimento de um serviço RESTful para toda a aplicação.

## Tecnologias 💻
 
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [MongoDB](https://www.mongodb.com/)
- [Mongo Express](https://alphasec.io/mongo-express-mongodb-management-made-easy/)
- [ViaCep API](https://viacep.com.br/)
- [Nominatim API](https://nominatim.org/)
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT](https://jwt.io/)
- [OAuth2](https://oauth.net/2/)
- [SpringDoc OpenAPI 3](https://springdoc.org/v2/#spring-webflux-support)
- [Docker](https://www.docker.com/)
- [JUnit5](https://junit.org/junit5/)
- [Mockito](https://site.mockito.org/)
- [Jacoco](https://www.eclemma.org/jacoco/)
- [Bean Validation](https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html)
- [HATEOAS](https://spring.io/projects/spring-hateoas)

## Práticas adotadas ✨

- SOLID, DRY, YAGNI, KISS
- API REST
- TDD
- Consultas com Spring Data JPA
- Injeção de Dependências
- Testes Automatizados
- Geração automática do Swagger com a OpenAPI 3
- Autenticação e Autorização com OAuth2 e JWT

## Diferenciais 🔥

Alguns diferenciais que não foram solicitados no desafio:

* Utilização de Docker
* TDD - Test Driven Development
* Tratamento de exceções
* Validações com Constraints Customizados
* Testes Unitários e de Integração
* Cobertura de Testes com Jacoco
* Documentação Swagger
* Consumo da API da ViaCep com o RestTemplate, para consultar o CEP do Usuário
* Consumo da API Nominatim, para obter as Coordenadas de um usuário a partir do CEP
* Implementação de HATEOAS

## Como executar 🎉

1.Clonar repositório git:

```text
git clone https://github.com/FernandoCanabarroAhnert/desafio-goomer.git
```

2.Instalar dependências.

```text
mvn clean install
```

3.Executar a aplicação Spring Boot.

4.Testar endpoints através do Postman ou da url
<http://localhost:8080/swagger-ui/index.html#/>

### Usando Docker 🐳

- Clonar repositório git
- Construir o projeto:
```
./mvnw clean package
```
- Construir a imagem:
```
./mvnw spring-boot:build-image
```
- Executar o container:
```
docker run --name desafio-goomer -p 8080:8080  -d desafio-goomer:0.0.1-SNAPSHOT
```
## API Endpoints 📚

Para fazer as requisições HTTP abaixo, foi utilizada a ferramenta [Postman](https://www.postman.com/):
- Collection do Postman completa: [Postman-Collection](https://github.com/user-attachments/files/17267724/Goomer.Dev.postman_collection.json)
- Environment do Postman: [Postman Environment](https://github.com/user-attachments/files/17267750/Goomer.Dev.Env.postman_environment.json)


- Consultar Restaurantes próximos das coordenadas do Usuário logado
```
$ http GET http://localhost:8080/restaurants/geo/nearMe?maxDistance=8000

[
    {
        "id": "67015fcaf90ce176dccb9761",
        "name": "Hamburgueria 1903",
        "imageUrl": "https://exemplo.com/hamburgueria-1903.jpg",
        "address": {
            "logradouro": "Av. Carlos Gomes",
            "numero": "100",
            "complemento": "Loja 2",
            "bairro": "Três Figueiras",
            "cep": "90480-003",
            "cidade": "Porto Alegre",
            "estado": "RS"
        },
        "point": {
            "longitude": -51.171539,
            "latitude": -30.031834
        },
        "openingHours": {
            "Segunda-Sexta": "11h-23h",
            "Sábado-Domingo": "12h-22h"
        },
        "tags": [
            "Hamburguer",
            "Restaurante",
            "Fast Food"
        ],
        "links": [
            {
                "rel": "Consultar Restaurante por Id",
                "href": "http://localhost:8080/restaurants/67015fcaf90ce176dccb9761"
            }
        ]
    },
    {
        "id": "67015f9af90ce176dccb975b",
        "name": "Restaurante Sushi Seninha",
        "imageUrl": "https://exemplo.com/sushi-seninha.jpg",
        "address": {
            "logradouro": "Av. Protásio Alves",
            "numero": "3136",
            "complemento": "",
            "bairro": "Petrópolis",
            "cep": "90410-006",
            "cidade": "Porto Alegre",
            "estado": "RS"
        },
        "point": {
            "longitude": -51.186074,
            "latitude": -30.032024
        },
        "openingHours": {
            "Segunda-Sexta": "12h-23h",
            "Sábado": "12h-00h",
            "Domingo": "12h-22h"
        },
        "tags": [
            "Sushi",
            "Comida Japonesa",
            "Restaurante"
        ],
        "links": [
            {
                "rel": "Consultar Restaurante por Id",
                "href": "http://localhost:8080/restaurants/67015f9af90ce176dccb975b"
            }
        ]
    }
]
```

- Consultar Restaurante por Id
```
$ http GET http://localhost:8080/restaurants/{id}

{
    "id": "67015ecef90ce176dccb9753",
    "name": "Churrascaria Galpão Crioulo",
    "imageUrl": "https://exemplo.com/galpao-crioulo.jpg",
    "address": {
        "logradouro": "Av. Loureiro da Silva",
        "numero": "255",
        "complemento": "Parque Maurício Sirotsky Sobrinho",
        "bairro": "Centro Histórico",
        "cep": "90050-240",
        "cidade": "Porto Alegre",
        "estado": "RS"
    },
    "point": {
        "longitude": -51.225755,
        "latitude": -30.028448
    },
    "openingHours": {
        "Segunda-Sexta": "11h-23h",
        "Sábado-Domingo": "11h-22h"
    },
    "tags": [
        "Churrascaria",
        "Comida Gaúcha",
        "Restaurante"
    ],
    "_links": {
        "Consultar Produtos do Restaurante Churrascaria Galpão Crioulo": {
            "href": "http://localhost:8080/restaurants/67015ecef90ce176dccb9753/products"
        }
    }
}

```
- Consultar Restaurantes por Coordenadas
```
$ http GET http://localhost:8080/restaurants/geo?longitude=-51.225755&latitude=-30.028448&maxDistance=7500

[
    {
        "id": "67015ecef90ce176dccb9753",
        "name": "Churrascaria Galpão Crioulo",
        "imageUrl": "https://exemplo.com/galpao-crioulo.jpg",
        "address": {
            "logradouro": "Av. Loureiro da Silva",
            "numero": "255",
            "complemento": "Parque Maurício Sirotsky Sobrinho",
            "bairro": "Centro Histórico",
            "cep": "90050-240",
            "cidade": "Porto Alegre",
            "estado": "RS"
        },
        "point": {
            "longitude": -51.225755,
            "latitude": -30.028448
        },
        "openingHours": {
            "Segunda-Sexta": "11h-23h",
            "Sábado-Domingo": "11h-22h"
        },
        "tags": [
            "Churrascaria",
            "Comida Gaúcha",
            "Restaurante"
        ],
        "links": [
            {
                "rel": "Consultar Restaurante por Id",
                "href": "http://localhost:8080/restaurants/67015ecef90ce176dccb9753"
            }
        ]
    }
]

```


