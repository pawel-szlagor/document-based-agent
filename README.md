# Web application demonstrating integration of Spring Boot and LangChain4J.

This project covers two main topics:

- Data ingestion - files are uploaded via REST endpoint, parsed by Apache Tika library, split and stored as embedding in
  in-memory vector store
- Retrieval Augmented Generation - retrieves contextual document from a vector store and send the relevant information
  as a context along with original question to LLM

## Requirements

- Java 17+
- OpenAI API key in `OPENAI_API_KEY` environment variable.

## Running the app

The application can be start by running `RAGApplication.java` inside IDE or with the default Maven
command `mvn spring-boot:run`.