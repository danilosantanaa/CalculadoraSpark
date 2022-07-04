import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerB {
    public static void main(String[] args) 	{
        System.out.println("SERVIDOR B EXECUTANDO...");
        try {
            ServerSocket s = new ServerSocket(9998);


            while (true) {
                Socket c = s.accept();
                InputStream i = c.getInputStream();
                OutputStream o = c.getOutputStream();

                System.out.println("DISPOSITIVO " + c.getRemoteSocketAddress() + " CONECTADO");

                Thread t = new ClientHandler(c, i, o);

                t.start();
            }
        }
        catch (Exception err){
            System.err.println(err);
        }
    }
}
