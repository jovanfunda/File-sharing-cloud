package servent.message.buddySystem;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class OKMessage extends BasicMessage {

    public OKMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.OK, originalSenderInfo, receiverInfo);
    }
}
