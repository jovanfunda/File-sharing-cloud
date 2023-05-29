package servent.message.mutex;

import app.AppConfig;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.Message;
import servent.message.MessageType;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

public class TokenMessage extends BasicMessage {

	public Queue<ServentInfo> serventsWaiting;

	public List<Integer> finishedRequests;

	public TokenMessage(ServentInfo sender, ServentInfo receiver, Queue<ServentInfo> serventsWaiting, List<Integer> finishedRequests) {
		super(MessageType.TOKEN, sender, receiver);
		this.serventsWaiting = serventsWaiting;
	}

	public TokenMessage(ServentInfo sender, ServentInfo receiver, Queue<ServentInfo> serventsWaiting, List<Integer> finishedRequests, int messageId) {
		super(MessageType.TOKEN, sender, receiver, "", messageId);
		this.serventsWaiting = serventsWaiting;
	}

	@Override
	public Message changeReceiver(Integer newReceiverId) {
		if (AppConfig.myServentInfo.getNeighbors().contains(newReceiverId)) {
			ServentInfo newReceiverInfo = AppConfig.getInfoById(newReceiverId);

			return new TokenMessage(getOriginalSenderInfo(),
					newReceiverInfo, serventsWaiting, finishedRequests, getMessageId());
		} else {
			AppConfig.timestampedErrorPrint("Trying to make a message for " + newReceiverId + " who is not a neighbor.");

			return null;
		}

	}

}
