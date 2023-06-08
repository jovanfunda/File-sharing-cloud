package servent.message.buddySystem;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class HeIsNotOKMessage extends BasicMessage {

    public HeIsNotOKMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.NOT_OK, originalSenderInfo, receiverInfo);
    }
}
