package uk.org.harden;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

@SuppressWarnings("UnnecessarySemicolon")
class TimerTCP {

    private static final Logger LOGGER = Logger.getLogger(TimerTCP.class.getName());

    @SuppressWarnings("UnusedReturnValue")
    public static boolean TimerTCPSend(Map<String, ArrayList<String>> statsTable) {
        boolean result = false;
        int retries = TimerConstants.TCP_RETRIES;
        while (retries-- > 0) {
            try (
                    Socket clientSocket = new Socket(TimerConstants.TCPHostName, TimerConstants.TCPPortNumber);
                    ObjectOutputStream objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ) {
                LOGGER.info(statsTable.toString());

                objectOutput.writeObject(statsTable);

                String serverResponse = inFromServer.readLine();
                LOGGER.info("From Server >> " + serverResponse);

                clientSocket.close();
                result = true;
                break;
            } catch (ConnectException ce) {
                if (retries == 0) {
                    LOGGER.info("Unable to connect to server ... is it running?");
                    //ce.printStackTrace();
                }
            } catch (Exception e) {
                if (retries == 0) {
                    LOGGER.info("Unable to communicate to server.");
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(TimerConstants.TCP_RETRY_DELAY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
