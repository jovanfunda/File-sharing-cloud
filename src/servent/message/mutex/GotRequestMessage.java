package servent.message.mutex;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class GotRequestMessage extends BasicMessage {

    public GotRequestMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.GOT_REQUEST, originalSenderInfo, receiverInfo);
    }
}
