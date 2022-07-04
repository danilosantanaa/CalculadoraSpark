import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static spark.Spark.*;

public class Main {

    public static void main(String [] args) {
        port(8080);
        staticFileLocation("/public");

        get("/", (request, response) ->{

            response.header("Content-Type", "text/html; charset=utf-8");
            response.status(200);
            return new View(request).index();
        });

        get("/somar/*", (request, response) -> {
            response.header("Content-Type", "application/json; charset=utf-8");
            response.status(200);
            return ConectarServerInterno("soma " + request.splat()[0]);
        });
        get("/substrair/*", (request, response) ->  {
            response.header("Content-Type", "application/json; charset=utf-8");
            response.status(200);
            return ConectarServerInterno("sub " + request.splat()[0]);
        });
        get("/dividir/*", (request, response) -> {
            response.header("Content-Type", "application/json; charset=utf-8");
            response.status(200);
            return ConectarServerInterno("div " + request.splat()[0]);
        });

        get("/multiplicacao/*", (request, response) -> {
            response.header("Content-Type", "application/json; charset=utf-8");
            response.status(200);
            return ConectarServerInterno("mult " + request.splat()[0]);
        });

        get("/potencia/:num1/:num2", (request, response) -> {
            response.header("Content-Type", "application/json; charset=utf-8");
            response.status(200);
            String num1 = request.params("num1");
            String num2 = request.params("num2");

            return ConectarServerInterno("pot " + num1 + " ^ " + num2 );
        });

        get("/raiz/:num", (request, response) -> {
            response.header("Content-Type", "application/json; charset=utf-8");
            response.status(200);
            String num = request.params("num");

            return ConectarServerInterno("raizQ " + num);
        });

        get("/porcentagem/:num1/:num2", (request, response) -> {
            response.header("Content-Type", "application/json; charset=utf-8");
            response.status(200);
            String num1 = request.params("num1");
            String num2 = request.params("num2");

            return ConectarServerInterno("porc " + num1 + " % " + num2 );
        });

        // Not Found - 404
        get("/*", (request, response) -> "{\n\t\"erro\":\"404 - Not Found\", \n\t\"status\":\"" + request.splat()[0] + "\"\n}");
    }

    public static String ConectarServerInterno(String expresao) {
        System.out.println("CLIENTE EXECUTANDO...");
        Socket s = null;
        InputStream i = null;
        OutputStream o = null;
        String str = "";
        int id = 0;

        try {


            do {
                for(int pos = expresao.length(); pos < 30000; pos++) {
                    expresao += " ";
                }

                byte[] line = expresao.getBytes(StandardCharsets.UTF_8);

                // Mudar a porta do servidor de acordo com o tipo de servidor que ira operar
                Protocolo protocoloClient = new Protocolo(line);
                protocoloClient.processarDados();

                // Se estiver na faixa de 5 até 7 o servidor B realizar a operação de RAIZ, Potencia, e Porcentagem
                int porta = 9999; // Padrão será servidor A
                if(protocoloClient.getID() >= 5 && protocoloClient.getID() <= 7){
                    porta = 9998; // Servidor B
                }

                id = protocoloClient.getID();

                s = new Socket("127.0.0.1", porta);
                i  = s.getInputStream();
                o = s.getOutputStream();

                o.write(line);
                i.read(line);
                str = new String(line);

                if (!str.trim().equals("100"))
                    return str.trim();
                else
                    return "<<< CONEXAO COM O SERVIDOR FINALIZADA >>>";

            } while (!str.trim().equals("100")) ;

        }
        catch (ConnectException e) {
            return "{\n\t\"erro\": \"Por favor, ligue o servidor\", \n\t\"servidor-erro\": \"servidor-" + (( id < 5 ? "A" : "B")) + "\"\n}";
        }
        catch (Exception err) {
            err.printStackTrace();
            return err.getMessage();
        }
    }
}
