import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class AplicacaoCambioInternacional {

    // URL base da API de câmbio
    private static final String API_BASE_URL = "https://v6.exchangerate-api.com/v6/1ff96dcf7b301682fb67440c/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Loop principal do menu
        while (true) {
            exibirMenu();

            // Lê a opção escolhida pelo usuário
            int opcao = scanner.nextInt();
            scanner.nextLine();

            // Executa a ação correspondente à opção escolhida
            switch (opcao) {
                case 1:
                    converterMoeda(scanner);
                    break;
                case 2:
                    listarCodigosMoeda();
                    break;
                case 3:
                    System.out.println("Fechando a aplicação...");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Opção inválida, escolha entre as opções a seguir: ");
            }
        }
    }

    // Exibe o menu principal para o usuário
    private static void exibirMenu() {
        System.out.println("|            Realize o seu Câmbio:      |");
        System.out.println("|    1. Conversão das moedas            |");
        System.out.println("|    2. Exibir Lista de moedas          |");
        System.out.println("|    3. Fechar aplicação                |");
        System.out.println("|             Escolha uma opção:        |");
    }

    // Realiza a conversão de moeda
    private static void converterMoeda(Scanner scanner) {
        // Solicita ao usuário a moeda de origem
        System.out.print("Digite a moeda inicial (por exemplo: BRL, EUR, USD...): ");
        String moedaInicial = scanner.nextLine().toUpperCase();

        // Solicita ao usuário a moeda de destino
        System.out.print("Agora, digite a moeda desejada (por exemplo: JPY, AUD, CAD...): ");
        String moedaDesejada = scanner.nextLine().toUpperCase();

        // Solicita ao usuário o valor a ser convertido
        System.out.print("Por fim, especifique o valor a ser convertido: ");
        double valor = scanner.nextDouble();

        try {
            // Monta a URL da API para obter a taxa de câmbio
            String apiUrl = API_BASE_URL + "latest/" + moedaInicial;

            // Cria um cliente HTTP e uma requisição para a API
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Verifica se a resposta da API foi bem-sucedida
            if (response.statusCode() == 200) {
                // Extrai os dados relevantes da resposta JSON
                JsonElement jsonElement = new Gson().fromJson(response.body(), JsonElement.class);
                // Obtém a taxa de câmbio para a moeda de destino
                double taxaCambio = jsonElement.getAsJsonObject().getAsJsonObject("conversion_rates").get(moedaDesejada).getAsDouble();
                // Calcula o valor convertido
                double valorConvertido = valor * taxaCambio;

                // Exibe o resultado da conversão
                System.out.printf("%.2f %s = %.2f %s\n", valor, moedaInicial, valorConvertido, moedaDesejada);
            } else {
                System.out.println("Erro ao fazer a solicitação: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao processar a solicitação. Por favor, tente novamente.");
        }
    }

    // Lista os códigos de moeda disponíveis para conversão
    private static void listarCodigosMoeda() {
        try {
            // Monta a URL da API para obter as taxas de câmbio em relação ao USD
            String apiUrl = API_BASE_URL + "latest/USD";

            // Cria um cliente HTTP e uma requisição para a API
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Verifica se a resposta da API foi bem-sucedida
            if (response.statusCode() == 200) {
                // Extrai as taxas de câmbio do JSON de resposta
                JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonObject conversionRates = jsonResponse.getAsJsonObject("conversion_rates");

                // Exibe os códigos de moeda disponíveis
                System.out.println("Essas são as Moedas Disponíveis para conversão:");
                for (String code : conversionRates.keySet()) {
                    System.out.println(code);
                }
            } else {
                System.out.println("Erro ao fazer a solicitação: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao processar a solicitação. Por favor, tente novamente.");
        }
    }
}