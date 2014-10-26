package uk.org.harden;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

@SuppressWarnings("UnnecessarySemicolon")
class ReportServer {

    private static final Logger LOGGER = Logger.getLogger(ReportServer.class.getName());

    public static void main(String[] args) {

        try (
                ServerSocket MyService = new ServerSocket(TimerConstants.TCPPortNumber);
        ) {
            int counter = 0;
            Map<String, ArrayList<String>> clientObject;
            String clientSentence;

            //noinspection InfiniteLoopStatement
            while (true) {
                try (
                        Socket clientSocket = MyService.accept();
                        ObjectInputStream objectInput = new ObjectInputStream(clientSocket.getInputStream());
                        DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
                ) {
                    counter++;
                    int rows = 0;
                    int cols = 0;

                    try {
                        clientObject = (Map<String, ArrayList<String>>) objectInput.readObject();
                        rows = clientObject.size();
                        cols = clientObject.get("Header").size();
                        LOGGER.info("(" + counter + ") " + clientObject.toString());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    clientSentence = "Rows=" + rows + ", Cols=" + cols + "\n";
                    outToClient.writeBytes(clientSentence);
                    LOGGER.info("(" + counter + ") Replied");
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
