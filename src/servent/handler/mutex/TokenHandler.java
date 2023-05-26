package servent.handler.mutex;

import app.AppConfig;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;

public class TokenHandler implements MessageHandler {

	private final Message clientMessage;
	private final SuzukiMutex mutex;
	
	public TokenHandler(Message clientMessage, DistributedMutex tokenMutex) {
		this.clientMessage = clientMessage;
		this.mutex = (SuzukiMutex) tokenMutex;
	}
	
	@Override
	public void run() {
		if(MessageType.TOKEN == clientMessage.getMessageType()) {
			if (clientMessage.getReceiverInfo().getId() == AppConfig.myServentInfo.getId()) {
				mutex.setTokenActive(true);
			}
		} else {
			AppConfig.timestampedErrorPrint("Received a message that is not a token.. " + clientMessage);
		}
	}
}
