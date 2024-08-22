import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class EchoTCPClient
{
    private final static Scanner scanner = new Scanner(System.in);

    public static final String server = "localhost";
    public static final int PORT = 3400;

    private PrintWriter toNetwork;
    private BufferedReader fromNetwork;

    private Socket clientsideSocket;

    public EchoTCPClient() {
        System.out.println("Echo TCP de cliente corriendo...");
    }

    public void init() throws Exception {

        clientsideSocket = new Socket(server, PORT);
        createStream(clientsideSocket);
        protocol(clientsideSocket);

        clientsideSocket.close();
    }

    public void protocol(Socket socket) throws Exception {
        System.out.print("Tipo de conversión: ");
        String input = scanner.nextLine();
        toNetwork.println(input);


        // Leer múltiples líneas del servidor
        String fromServer;
        while ((fromServer = fromNetwork.readLine()) != null) {
            if (fromServer.equals("FIN")) {
                break;
            }
            System.out.println("[Cliente] Desde servidor: " + fromServer);
        }
    }

    private void createStream(Socket socket) throws Exception {
        toNetwork = new PrintWriter(socket.getOutputStream(), true);
        fromNetwork = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static void main(String[] args) throws Exception {
        EchoTCPClient client = new EchoTCPClient();
        client.init();
    }


}
