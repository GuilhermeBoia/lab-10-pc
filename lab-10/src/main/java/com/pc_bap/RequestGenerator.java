package com.pc_bap;

import java.net.http.*;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;

public class RequestGenerator {

    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Random random = new Random();

    public static void main(String[] args) {
        try {
            System.out.println("Aguardando servidor iniciar...");
            Thread.sleep(3000); // espera o Spring Boot subir
    
            popularProdutosIniciais(); // popula os produtos
    
            int numThreads = 20;
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    
            for (int i = 0; i < numThreads; i++) {
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < 10; j++) {
                            executarRequisicaoAleatoria();
                            Thread.sleep(random.nextInt(500));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
    
            executor.shutdown();
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private static void popularProdutosIniciais() throws Exception {
        System.out.println("Populando produtos iniciais...");
    
        List<String> produtos = List.of(
            """
            {
                "id": 1234,
                "name": "Notebook Gamer",
                "price": 4500.0,
                "quantityStock": 10
            }
            """,
            """
            {
                "id": 5678,
                "name": "Mouse Sem Fio",
                "price": 120.0,
                "quantityStock": 30
            }
            """,
            """
            {
                "id": 9012,
                "name": "Teclado Mecânico",
                "price": 350.0,
                "quantityStock": 15
            }
            """
        );
    
        for (String body : produtos) {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/products"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    
            enviar(request, "Cadastro de produto");
        }
    
        System.out.println("Produtos cadastrados.");
    }
    

    private static void executarRequisicaoAleatoria() throws Exception {
        int opcao = random.nextInt(4);

        switch (opcao) {
            case 0 -> consultarProdutos();
            case 1 -> comprarProduto();
            case 2 -> atualizarEstoque();
            case 3 -> gerarRelatorio();
        }
    }

    private static void consultarProdutos() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/products"))
            .GET()
            .build();

        enviar(request, "Consulta de produtos");
    }

    private static void comprarProduto() throws Exception {
        String requestBody = """
            {
                "id": "1234",
                "quantity": 1
            }
        """;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/purchase"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        enviar(request, "Compra de produto");
    }

    private static void atualizarEstoque() throws Exception {
        String requestBody = String.format("""
            {
                "quantity": %d
            }
        """, 10 + random.nextInt(20));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/products/1234/stock"))
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        enviar(request, "Atualização de estoque");
    }

    private static void gerarRelatorio() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/sales/report"))
            .GET()
            .build();

        enviar(request, "Relatório de vendas");
    }

    private static void enviar(HttpRequest request, String tipo) throws Exception {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.printf("[%s] Status: %d - Resposta: %s%n", tipo, response.statusCode(), response.body());
    }
}
