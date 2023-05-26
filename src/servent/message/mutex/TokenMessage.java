package servent.message.mutex;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

import java.util.Queue;

public class TokenMessage extends BasicMessage {

	private Queue<ServentInfo> serventsWaiting;

	public TokenMessage(ServentInfo sender, ServentInfo receiver) {
		super(MessageType.TOKEN, sender, receiver);
	}
}
