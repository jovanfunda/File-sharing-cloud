package app.bootstrap;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import servent.handler.MessageHandler;
import servent.handler.NullHandler;
import servent.handler.bootstrap.HelloToBootstrapHandler;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class BootstrapListener implements Runnable {

    private volatile boolean working = true;

    private final ExecutorService threadPool = Executors.newWorkStealingPool();

    private final int portNumber;

    public BootstrapListener(int portNumber) {
        this.portNumber = portNumber;
    }

    @Override
    public void run() {
        ServerSocket listenerSocket = null;
        try {
            listenerSocket = new ServerSocket(portNumber, 100);

            listenerSocket.setSoTimeout(1000);
        } catch (IOException e) {
            System.err.println("Couldn't open listener socket on: " + portNumber);
            System.exit(0);
        }

        while(working) {
            try {

                Socket clientSocket = listenerSocket.accept();

                //GOT A MESSAGE! <3
                Message clientMessage = MessageUtil.readMessage(clientSocket);

                MessageHandler messageHandler = new NullHandler(clientMessage);

                switch (clientMessage.getMessageType()) {
                    case HELLO_TO_BOOTSTRAP:
                        messageHandler = new HelloToBootstrapHandler(clientMessage);
                        break;
                }

                threadPool.submit(messageHandler);
            } catch (SocketTimeoutException timeoutEx) {
                //Uncomment the next line to see that we are waking up every second.
//				AppConfig.timedStandardPrint("Waiting...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.working = false;
    }
}
