package servent.message.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;

/**
 * This worker sends a message asynchronously. Doing this in a separate thread
 * has the added benefit of being able to delay without blocking main or somesuch.
 * 
 * @author bmilojkovic
 *
 */
public class DelayedMessageSender implements Runnable {

	private final Message messageToSend;
	
	public DelayedMessageSender(Message messageToSend) {
		this.messageToSend = messageToSend;
	}
	
	public void run() {

		try {
			Thread.sleep((long)(Math.random() * 1000) + 500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		ServentInfo receiverInfo = messageToSend.getReceiverInfo();

		try {

			if(messageToSend.getReceiverInfo().getId() == -1 || messageToSend.getOriginalSenderInfo().getId() == -1) {

				AppConfig.timestampedStandardPrint("Saljem poruku " + messageToSend + " van arhitekture");
				Socket sendSocket = new Socket(receiverInfo.getIpAddress(), receiverInfo.getListenerPort());

				ObjectOutputStream oos = new ObjectOutputStream(sendSocket.getOutputStream());
				oos.writeObject(messageToSend);
				oos.flush();

				sendSocket.close();

			} else if(AppConfig.myServentInfo.getNeighbors().contains(receiverInfo.getId())) {

				AppConfig.timestampedStandardPrint("Saljem poruku " + messageToSend + " ka komsiji " + receiverInfo);
				Socket sendSocket = new Socket(receiverInfo.getIpAddress(), receiverInfo.getListenerPort());

				ObjectOutputStream oos = new ObjectOutputStream(sendSocket.getOutputStream());
				oos.writeObject(messageToSend);
				oos.flush();

				sendSocket.close();

			} else {

				for(Integer neighbor : AppConfig.myServentInfo.getNeighbors()) {

					AppConfig.timestampedStandardPrint("Saljem poruku " + messageToSend + " ka " + neighbor + ", jer mi " + messageToSend.getReceiverInfo().getId() + " nije komsija");
					Socket sendSocket = new Socket(AppConfig.getInfoById(neighbor).getIpAddress(), AppConfig.getInfoById(neighbor).getListenerPort());

					ObjectOutputStream oos = new ObjectOutputStream(sendSocket.getOutputStream());
					oos.writeObject(messageToSend);
					oos.flush();

					sendSocket.close();

				}
			}
		} catch (IOException e) {
			AppConfig.timestampedErrorPrint("Couldn't send message: " + messageToSend);
			AppConfig.timestampedErrorPrint(e.getMessage());
		}
	}
}
