import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoTCPServer {

    public static final int PORT = 3400;

    private ServerSocket listener;
    private Socket serversideSocket;

    private PrintWriter toNetwork;
    private BufferedReader fromNetwork;

    public EchoTCPServer() {
        System.out.println("El servidor con eco TCP está corriendo en el puerto: " + PORT);
    }

    public void init() throws Exception {

        listener = new ServerSocket(PORT);

        while (true) {
            serversideSocket = listener.accept();

            createStreams(serversideSocket);
            protocol(serversideSocket);
        }
    }

    public void protocol(Socket socket) throws Exception {
        String message = fromNetwork.readLine();
        System.out.println("[Server] desde cliente: " + message);
        String answer = message; // Que redundante
        toNetwork.println(answer);
    }

    public void createStreams(Socket socket) throws Exception {
        toNetwork = new PrintWriter(socket.getOutputStream(), true);
        fromNetwork = new BufferedReader(new InputStreamReader(serversideSocket.getInputStream()));
    }

    public static void main(String[] args) throws Exception {
        EchoTCPServer server = new EchoTCPServer();
        server.init();
    }
}
