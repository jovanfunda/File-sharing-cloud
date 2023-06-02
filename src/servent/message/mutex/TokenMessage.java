package servent.message.mutex;

import app.AppConfig;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.Message;
import servent.message.MessageType;

import java.util.List;
import java.util.Queue;

public class TokenMessage extends BasicMessage {

	public Queue<ServentInfo> serventsWaiting;

	public List<Integer> finishedRequests;

	public TokenMessage(ServentInfo sender, ServentInfo receiver) {
		super(MessageType.TOKEN, sender, receiver);
	}

	public TokenMessage(ServentInfo sender, ServentInfo receiver, Queue<ServentInfo> serventsWaiting) {
		super(MessageType.TOKEN, sender, receiver);
		this.serventsWaiting = serventsWaiting;
	}

	public TokenMessage(ServentInfo sender, ServentInfo receiver, Queue<ServentInfo> serventsWaiting, int messageId) {
		super(MessageType.TOKEN, sender, receiver, "", messageId);
		this.serventsWaiting = serventsWaiting;
	}

	@Override
	public Message changeReceiver(Integer newReceiverId) {
			ServentInfo newReceiverInfo = AppConfig.getInfoById(newReceiverId);
			TokenMessage message = new TokenMessage(getOriginalSenderInfo(), newReceiverInfo, serventsWaiting, getMessageId());
			message.finishedRequests = finishedRequests;

			return message;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TokenMessage other) {

			boolean messageId = getMessageId() == other.getMessageId();
			boolean id = getOriginalSenderInfo().getId() == other.getOriginalSenderInfo().getId();
			boolean port = getOriginalSenderInfo().getListenerPort() == other.getOriginalSenderInfo().getListenerPort();
			boolean serventValue = getReceiverInfo().equals(other.getReceiverInfo());

			return messageId && id && port && serventValue;
		}

		return false;
	}

}
