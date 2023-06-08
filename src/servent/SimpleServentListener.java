package servent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.AppConfig;
import app.Cancellable;
import app.ServentInfo;
import mutex.DistributedMutex;
import servent.handler.*;
import servent.handler.buddySystem.PingHandler;
import servent.handler.buddySystem.PongHandler;
import servent.handler.file.PullFileHandler;
import servent.handler.file.SendFileHandler;
import servent.handler.mutex.FirstRequestTokenHandler;
import servent.handler.mutex.GotRequestHandler;
import servent.handler.mutex.TokenHandler;
import servent.handler.mutex.RequestTokenHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.mutex.RequestTokenMessage;
import servent.message.mutex.TokenMessage;
import servent.message.util.MessageUtil;

public class SimpleServentListener implements Runnable, Cancellable {

	private volatile boolean working = true;
	
	private DistributedMutex mutex;
	private int portNumber;

	public SimpleServentListener(int portNumber, DistributedMutex mutex) {
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

				boolean didPut = AppConfig.receivedMessages.add(clientMessage);

				if(clientMessage.getMessageType() == MessageType.HELLO_TO_NODE || clientMessage.getMessageType() == MessageType.FIRST_REQUEST || (clientMessage.getOriginalSenderInfo().getId() == -1 && clientMessage.getMessageType() == MessageType.UPDATE_SYSTEM)) {
					didPut = true;
				}

				if (didPut) {
					AppConfig.timestampedStandardPrint("Prihvacena poruka " + clientMessage);

					if (clientMessage.getReceiverInfo().getId() != AppConfig.myServentInfo.getId()) {
						MessageUtil.sendMessage(clientMessage);
						continue;
					}

					MessageHandler messageHandler = new NullHandler(clientMessage);

					switch (clientMessage.getMessageType()) {
						case HELLO_FROM_BOOTSTRAP:
							messageHandler = new HelloFromBootstrapHandler(clientMessage, mutex);
							break;
						case HELLO_TO_NODE:
							messageHandler = new HelloToNodeHandler(clientMessage, mutex);
							break;
						case HELLO_FROM_NODE:
							messageHandler = new HelloFromNodeHandler(clientMessage, mutex);
							break;
						case REQUEST_TOKEN:
							messageHandler = new RequestTokenHandler(clientMessage, mutex);
							break;
						case TOKEN:
							messageHandler = new TokenHandler(clientMessage, mutex);
							break;
						case FIRST_REQUEST:
							messageHandler = new FirstRequestTokenHandler(clientMessage, mutex);
							break;
						case GOT_REQUEST:
							messageHandler = new GotRequestHandler(clientMessage, mutex);
							break;
						case UPDATE_SYSTEM:
							messageHandler = new UpdateSystemHandler(clientMessage, mutex);
							break;
						case SYSTEM_UPDATED:
							messageHandler = new SystemUpdatedHandler(clientMessage, mutex);
							break;
						case PULL_FILE:
							messageHandler = new PullFileHandler(clientMessage);
							break;
						case SEND_FILE:
							messageHandler = new SendFileHandler(clientMessage);
							break;
						case PING:
							messageHandler = new PingHandler(clientMessage);
							break;
						case PONG:
							messageHandler = new PongHandler(clientMessage);
							break;
					}

					threadPool.submit(messageHandler);
				} else {
					AppConfig.timestampedStandardPrint("Poruka vec procitana " + clientMessage);
				}
			} catch (SocketTimeoutException timeoutEx) {
				//Uncomment the next line to see that we are waking up every second.
//				AppConfig.timestampedStandardPrint("Waiting...");
			} catch (IOException e) {
				AppConfig.timestampedErrorPrint("greska simple " + e.getMessage());
			}
		}
	}

	@Override
	public void stop() {
		this.working = false;
	}
}
