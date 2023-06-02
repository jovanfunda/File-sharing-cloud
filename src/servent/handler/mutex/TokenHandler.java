package servent.handler.mutex;

import app.AppConfig;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.mutex.TokenMessage;
import servent.message.util.MessageUtil;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TokenHandler implements MessageHandler {

	private Message clientMessage;
	private final SuzukiMutex mutex;

	public TokenHandler(Message clientMessage, DistributedMutex tokenMutex) {
		this.clientMessage = clientMessage;
		this.mutex = (SuzukiMutex) tokenMutex;
	}
	
	@Override
	public void run() {
		if(clientMessage.getMessageType() == MessageType.TOKEN) {
			if (clientMessage.getReceiverInfo().getId() == AppConfig.myServentInfo.getId()) {
				mutex.setTokenActive(true);
			}
		} else {
			AppConfig.timestampedErrorPrint("Received a message that is not a token.. " + clientMessage);
		}
	}
}
