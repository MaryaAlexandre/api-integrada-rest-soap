# Gerenciador de Tarefas Simples

---

## Visão Geral do Projeto

Este projeto é um **Gerenciador de Tarefas Simples** que demonstra a integração de diferentes tipos de serviços (**REST e SOAP**) e clientes através de um **API Gateway**. Ele foi construído para ser um **exemplo prático e didático** de microsserviços e comunicação inter-serviços, focando na simplicidade de implementação.

### Objetivos Principais

- Demonstrar a comunicação entre serviços **REST** e **SOAP**.
- Implementar um **API Gateway** para roteamento, agregação e aplicação de princípios **HATEOAS**.
- Fornecer um **cliente web** (frontend simples) para interagir com o sistema.
- Apresentar um **cliente Python** para interagir diretamente com o serviço SOAP.

### Arquitetura Simplificada

O projeto é composto por cinco módulos principais:

1. **`servico-tarefas` (Java/Spring Boot - REST):** Gerencia as operações CRUD (Criar, Ler, Atualizar, Deletar) de tarefas. É um serviço RESTful padrão.
2. **`servico-config-usuario` (Java/Spring Boot - SOAP):** Simula um serviço legado ou especializado que gerencia configurações de usuário (como uma cor preferencial). Este serviço é exposto via SOAP.
3. **`gateway` (Java/Spring Cloud Gateway):** Atua como o ponto de entrada único para o sistema. Ele roteia requisições para os serviços apropriados, injeta links HATEOAS nas respostas do serviço REST de tarefas e funciona como um proxy para o serviço SOAP.
4. **`cliente-web` (HTML/CSS/JavaScript):** Um frontend estático simples que consome as APIs através do Gateway.
5. **`cliente-soap-python` (Python/Zeep):** Um script Python que interage diretamente com o serviço SOAP (`servico-config-usuario`), demonstrando a interoperabilidade.


---

## Tecnologias Utilizadas

- **Backend & Gateway:** Java 17+, Spring Boot 3.2.x, Spring Cloud Gateway, Spring Web Services (JAX-WS).
- **Frontend:** HTML5, CSS3, JavaScript.
- **Cliente SOAP:** Python 3.8+, Zeep.
- **Gerenciamento de Dependências:** Apache Maven.

---

## Pré-requisitos

Para rodar este projeto, você precisará ter instalado em sua máquina:

- **Java Development Kit (JDK) 17 ou superior:**  
  Verifique com `java -version`.

- **Apache Maven 3.8.x ou superior:**  
  Verifique com `mvn -v`.

- **Python 3.8 ou superior:**  
  Verifique com `python3 --version` ou `python --version`.

- **pip (gerenciador de pacotes Python):**  
  Verifique com `pip3 --version` ou `pip --version`.

---

## Como Rodar o Projeto

Siga estes passos cuidadosamente para iniciar e testar todas as partes do sistema. Você precisará de **quatro terminais** abertos simultaneamente.

### 1. Clonar o Repositório (se ainda não o fez)

```bash
git clone <URL_DO_SEU_REPOSITORIO>
cd gerenciador-tarefas-simples
--

### 2. Iniciar os Serviços Java (REST, SOAP e Gateway)
Iniciar os Serviços Java (REST, SOAP e Gateway)
cd servico-tarefas
mvn clean install      # Opcional: para garantir que dependências estão atualizadas
mvn spring-boot:run

Aguarde até receber: Started TarefasApplication in X.XXX seconds

--> Servico-config-usuario9serviço SOAP- 8081)
cd servico-config-usuario
mvn clean install
mvn spring-boot:run

--> Iniciar o gateway- porta 8080
cd gateway
mvn clean install
mvn spring-boot:run

--> Executar o cliente web
cd cliente-web
Se estiver rodando localmente: Basta abrir o arquivo index.html com um clique duplo, ou arraste-o para o navegador.

--> Executar o client SOAP Python- Porta 8081
cd cliente-soap-python
pip install zeep         # Instala a biblioteca necessária, se ainda não tiver
python3 client.py        # ou python client.py dependendo do ambiente

