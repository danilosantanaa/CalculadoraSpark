import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.io.OutputStream;

public class ClientHandler extends Thread {
    final Socket s;
    final InputStream i;
    final OutputStream o;

    public ClientHandler(Socket s, InputStream i, OutputStream o) {
        this.s = s;
        this.i = i;
        this.o = o;
    }

    @Override
    public void run() {
        try {
            Protocolo protocolo;
            do {
                byte[] line = new byte[30000];
                i.read(line);

                protocolo = new Protocolo(line).processarDados();

                // Enviando para o cliente os dados processado pelo protocolo
                System.out.print(protocolo.statusCode() + " ");
                if (protocolo.statusCode() == 200) {

                    // Limpando o buffer
                    String buffer = "";
                    for (int j = 0; j < 60; j++) buffer += " ";

                    System.out.println("Dados processado com sucesso!");
                    o.write((protocolo.jsonFormat().trim()).getBytes(StandardCharsets.UTF_8));
                } else if (protocolo.statusCode() == 404) {
                    System.out.println("Operação solicitada não pode ser processada!");
                    o.write(("{\"erro\":\"" + protocolo.getDados() + " não encontrado!\", \"servidor\":\"servidor-" + (( protocolo.getID() < 5 ? "A" : "B")) + "\"").replace("\n", "").getBytes(StandardCharsets.UTF_8));
                } else if (protocolo.statusCode() == 100) {
                    o.write(String.valueOf(protocolo.statusCode()).getBytes(StandardCharsets.UTF_8));
                } else if (protocolo.statusCode() == 101) {
                    System.out.println("Máquina desligada pelo dispositivo " + s.getRemoteSocketAddress());
                    o.write((protocolo.getResponse()).getBytes(StandardCharsets.UTF_8));
                } else {
                    o.write("Erro de servidor! :(".getBytes(StandardCharsets.UTF_8));
                }

            } while (protocolo == null || protocolo.statusCode() != 100 && protocolo.statusCode() != 101);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
