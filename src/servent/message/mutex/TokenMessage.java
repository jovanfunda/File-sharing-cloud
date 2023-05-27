package servent.message.mutex;

import app.AppConfig;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.Message;
import servent.message.MessageType;

import java.util.Queue;

public class TokenMessage extends BasicMessage {

	private Queue<ServentInfo> serventsWaiting;

	public TokenMessage(ServentInfo sender, ServentInfo receiver) {
		super(MessageType.TOKEN, sender, receiver);
	}

	public TokenMessage(ServentInfo sender, ServentInfo receiver, int messageId) {
		super(MessageType.TOKEN, sender, receiver, "", messageId);
	}

	@Override
	public Message changeReceiver(Integer newReceiverId) {
		if (AppConfig.myServentInfo.getNeighbors().contains(newReceiverId)) {
			ServentInfo newReceiverInfo = AppConfig.getInfoById(newReceiverId);

			Message toReturn = new TokenMessage(getOriginalSenderInfo(),
					newReceiverInfo, getMessageId());

			return toReturn;
		} else {
			AppConfig.timestampedErrorPrint("Trying to make a message for " + newReceiverId + " who is not a neighbor.");

			return null;
		}

	}

}
