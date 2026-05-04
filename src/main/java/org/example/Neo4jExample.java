package org.example;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import static org.neo4j.driver.Values.parameters;

public class Neo4jExample implements AutoCloseable {

    private final Driver driver;

    public Neo4jExample(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void close() {
        driver.close();
    }

    // Criar pessoas e relacionamento
    public void criarRelacoes() {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("""
                    CREATE (a:Pessoa {nome: 'Andre'})
                    CREATE (b:Pessoa {nome: 'Joao'})
                    CREATE (c:Pessoa {nome: 'Maria'})
                    CREATE (a)-[:AMIGO]->(b)
                    CREATE (a)-[:AMIGO]->(c)
                """);
                return null;
            });
        }
    }

    // Consultar conexões
    public void listarAmigos(String nome) {
        try (Session session = driver.session()) {
            Result result = session.run("""
                MATCH (p:Pessoa {nome: $nome})-[:AMIGO]->(amigo)
                RETURN amigo.nome AS nome
            """, parameters("nome", nome));

            System.out.println("Amigos de " + nome + ":");
            while (result.hasNext()) {
                Record record = result.next();
                System.out.println("- " + ((Record) record).get("nome").asString());
            }
        }
    }

    public static void main(String[] args) {
        try (Neo4jExample app = new Neo4jExample(
                "bolt://localhost:7687",
                "neo4j",
                "senha123")) {

           // app.criarRelacoes();
            app.listarAmigos("Andre");
        }
    }
}