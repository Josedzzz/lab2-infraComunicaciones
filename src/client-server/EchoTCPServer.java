import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

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
        System.out.println("[Server] Desde cliente: " + message);

        ArrayList<String> answer = interpretarMensaje(message); // Que redundante
        answer.add("FIN");
        if(answer.size() != 2) {
            for(int i=0; i < answer.size(); i++){
                toNetwork.println(answer.get(i));
            }
        } else {
            toNetwork.println(answer.get(0));
            toNetwork.println(answer.get(1));
        }
    }

    public void createStreams(Socket socket) throws Exception {
        toNetwork = new PrintWriter(socket.getOutputStream(), true);
        fromNetwork = new BufferedReader(new InputStreamReader(serversideSocket.getInputStream()));
    }

    public static void main(String[] args) throws Exception {
        EchoTCPServer server = new EchoTCPServer();
        server.init();
    }

    /*
     FUNCIÓN GENERAL QUE RECIBE LA INSTRUCCIÓN DESDE CLIENTE, PARECE QUE VA A SER GIGANTESCA
     */

    public static ArrayList<String> interpretarMensaje(String mensaje) {

        ArrayList<String> arraylistAux = new ArrayList<>();
        String[] partes = mensaje.split(" "); // Divide el mensaje en un arreglo, no siempre va a ser del mismo tamaño.
        String instruccion = partes[0];

        switch (instruccion) {
            /*
             * CASOS DEL PUNTO 2
             */

            case "GEN-CAD": // Este caso incluye la cantidad de carácteres de la cadena. Estará en partes[1].

                //  a) El usuario digita GEN-CAD seguido de la cantidad de caracteres que requiere generar.
                if(partes.length == 2) {
                    arraylistAux.add(Functions.generarCadena(Integer.parseInt(partes[1])));
                    return arraylistAux;
                // b) el usuario digita GEN-CAD seguido de los dos parámetros necesarios para realizar la operación (cantidad de caracteres y la cantidad de caracteres para cada uno de los segmentos).
                } else { 
                    // No sé si dejarlo así. Espero se entienda jsjs.
                    StringBuilder aux = new StringBuilder();
                    String[] auxArr = Functions.dividirCadenaEnParteIguales(Functions.generarCadena(Integer.parseInt(partes[1])), Integer.parseInt(partes[2]));
                    // Itera sobre el array auxiliar y construye la cadena. 
                    for(int i=0; i<auxArr.length; i++){
                        aux.append(auxArr[i]);
                        if(i < auxArr.length - 1) aux.append(", ");
                    }
                    arraylistAux.add(aux.toString());
                    return arraylistAux;
                }
            // c) el usuario digita GEN-CAD-PAR seguido de los dos parámetros necesarios para realizar la operación (cantidad de caracteres y la cantidad de caracteres para cada uno de los segmentos).
            case "GEN-CAD-PAR":
                String cadena = Functions.generarCadena(Integer.parseInt(partes[1]));
                int longitudParticiones = Integer.parseInt(partes[2]);

                String[] cadenaPartesIguales = Functions.dividirCadenaEnParteIguales(cadena, longitudParticiones);
                arraylistAux = new ArrayList<>(Arrays.asList(cadenaPartesIguales));
                return arraylistAux;

            /*
             * CASOS DEL PUNTO 4
             */

            // El usuario digita UNI-CAD seguido de las partes que conforman una cadena.
            case "UNI-CAD":
                StringBuilder aux = new StringBuilder();
                for(int i=1; i <= partes.length - 1; i++) {
                    aux.append(partes[i]);
                }
                arraylistAux.add(aux.toString());
                return arraylistAux;
            
            default:
                return null;
        }


    }


}
