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
             * CASOS DEL PUNTO 1
             */
            // a) El usuario digita CONV-DEC-BIN seguido de los dos parámetros necesarios para realizar la conversión.
            case "CONV-DEC-BIN":
                String decToBin = Functions.convertDecToBin(Integer.parseInt(partes[1]), Integer.parseInt(partes[2]));
                arraylistAux.add(decToBin);
                return arraylistAux;
            // b) El usuario digita CONV-DEC-HEX seguido de los dos parámetros necesarios para realizar la conversión.
            case "CONV-DEC-HEX":
                String decToHex = Functions.convertDecToHex(Integer.parseInt(partes[1]), Integer.parseInt(partes[2]));
                arraylistAux.add(decToHex);
                return arraylistAux;
            // c) El usuario digita CONV-BIN-HEXA
            case "CONV-BIN-HEXA":
                String binToHex = Functions.convertBinToHex(partes[1]);
                arraylistAux.add(binToHex);
                return arraylistAux;
            /*
             * CASOS DEL PUNTO 2
             */

            case "GEN-CAD": // Este caso incluye la cantidad de carácteres de la cadena. Estará en partes[1].

                // a) El usuario digita GEN-CAD seguido de la cantidad de caracteres que requiere generar.
                if(partes.length == 2) {
                    try {
                        arraylistAux.add(Functions.generarCadena(Integer.parseInt(partes[1])));
                    } catch (NumberFormatException e) {
                        arraylistAux.add(e.getMessage());
                    }
                // b) el usuario digita GEN-CAD seguido de los dos parámetros necesarios para realizar la operación (cantidad de caracteres y la cantidad de caracteres para cada uno de los segmentos).
                } else if (partes.length == 3) {
                    try {
                        int longitudCadenaGenerar = Integer.parseInt(partes[1]);
                        int longitudSegmentosCadenaGenerada = Integer.parseInt(partes[2]);
                        String[] auxArr = Functions.dividirCadenaEnParteIguales(Functions.generarCadena(longitudCadenaGenerar), longitudSegmentosCadenaGenerada);
                        arraylistAux.add(String.join(", ", auxArr));
                    } catch (NumberFormatException e) {
                        arraylistAux.add(e.getMessage());
                    }
                } else { 
                    String falloParam = "Número incorrecto de parámetros";
                    arraylistAux.add(falloParam);
                } 
                return arraylistAux;
            // c) El usuario digita GEN-CAD-PAR seguido de los dos parámetros necesarios para realizar la operación (cantidad de caracteres y la cantidad de caracteres para cada uno de los segmentos).
            case "GEN-CAD-PAR":
                int longitudCadenaGenerar1 = Integer.parseInt(partes[1]);
                int longitudParticiones = Integer.parseInt(partes[2]);

                // Se verifica si la longitud de la cadena es múltiplo de 16
                if (longitudCadenaGenerar1%16 != 0) {
                    String errLongCadena = "La longitud de la cadena no es múltiplo de 16";
                    arraylistAux.add(errLongCadena);
                } else {
                    String cadenaParaPartesIguales = Functions.generarCadena(longitudCadenaGenerar1);
                    arraylistAux = new ArrayList<>(Arrays.asList(
                        Functions.dividirCadenaEnParteIguales(cadenaParaPartesIguales, longitudParticiones)
                    ));
                }
                return arraylistAux;

            /*
             * CASOS DEL PUNTO 3
             */
            // a) El usuario digita CAD-SEG seguido de los parámetros necesarios para realizar la operación (cantidad de caracteres y los tamaños de las partes para separar la cadena).
            case "CAD-SEG":
                String cadenaParaPartesDesiguales = Functions.generarCadena(Integer.parseInt(partes[1]));
                int[] volumenesA = new int[partes.length - 2];
                for(int i=0; i <  volumenesA.length - 1; i++){ 
                    volumenesA[i] = Integer.parseInt((partes[i + 2]));
                }
                String[] cadenaPartesDesiguales = Functions.dividirCadenaPorVolumenesIndicados(cadenaParaPartesDesiguales,volumenesA);
                StringBuilder auxB = new StringBuilder();
                for(int i=0; i<cadenaPartesDesiguales.length; i++){
                    auxB.append(cadenaPartesDesiguales[i]);
                    if(i < cadenaPartesDesiguales.length - 2) auxB.append(", ");
                }
                arraylistAux.add(auxB.toString());
                return arraylistAux;

            case "CAD-SEG-PAR":
                String cadenaParaPartesDesigualesPartes = Functions.generarCadena(Integer.parseInt(partes[1]));
                int[] volumenesB = new int[partes.length - 2];
                for(int i=0; i <=  volumenesB.length - 1; i++){ 
                    volumenesB[i] = Integer.parseInt((partes[i + 2]));
                }
                System.out.println(volumenesB);
                String[] cadenasPartesDesigualesPorPartes = Functions.dividirCadenaPorVolumenesIndicados(cadenaParaPartesDesigualesPartes,volumenesB);
                arraylistAux = new ArrayList<>(Arrays.asList(cadenasPartesDesigualesPorPartes));
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
                String errComandoDesconocido = "El comando que envío es desconocido.";
                arraylistAux.add(errComandoDesconocido)
                return arraylistAux;
        }


    }


}
