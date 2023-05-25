package servent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.AppConfig;
import app.Cancellable;
import mutex.DistributedMutex;
import servent.handler.*;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class SimpleServentListener implements Runnable, Cancellable {

	private volatile boolean working = true;
	
	private DistributedMutex mutex;
	private int portNumber;

	public SimpleServentListener(int portNumber) {
		this.mutex = mutex;
		this.portNumber = portNumber;
	}

	private final ExecutorService threadPool = Executors.newWorkStealingPool();
	
	@Override
	public void run() {
		ServerSocket listenerSocket = null;
		try {
			listenerSocket = new ServerSocket(portNumber, 100);
			/*
			 * If there is no connection after 1s, wake up and see if we should terminate.
			 */
			listenerSocket.setSoTimeout(1000);
		} catch (IOException e) {
			AppConfig.timestampedErrorPrint("Couldn't open listener socket on: " + portNumber);
			System.exit(0);
		}
		
		while (working) {
			try {
				/*
				 * This blocks for up to 1s, after which SocketTimeoutException is thrown.
				 */
				Socket clientSocket = listenerSocket.accept();
				
				//GOT A MESSAGE! <3
				Message clientMessage = MessageUtil.readMessage(clientSocket);
				
				MessageHandler messageHandler = new NullHandler(clientMessage);

				switch (clientMessage.getMessageType()) {
					case HELLO_FROM_BOOTSTRAP:
						messageHandler = new HelloFromBootstrapHandler(clientMessage);
						break;
					case HELLO_TO_NODE:
						messageHandler = new HelloToNodeHandler(clientMessage);
						break;
					case HELLO_FROM_NODE:
						messageHandler = new HelloFromNodeHandler(clientMessage);
						break;
//				case TOKEN:
//					messageHandler = new TokenHandler(clientMessage, mutex);
//					break;
//					case LAMPORT_REQUEST:
//						messageHandler = new LamportRequestHandler(clientMessage,mutex);
//						break;
//					case LAMPORT_RELEASE:
//						messageHandler = new LamportReleaseHandler(clientMessage, mutex);
//						break;
//					case LAMPORT_REPLY:
//						messageHandler = new LamportReplyHandler(clientMessage, mutex);
//						break;
//				case POISON:
//					break;
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

	@Override
	public void stop() {
		this.working = false;
	}

}
