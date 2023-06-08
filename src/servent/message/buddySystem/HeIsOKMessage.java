package servent.message.buddySystem;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class HeIsOKMessage extends BasicMessage {

    public HeIsOKMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.HE_IS_OK, originalSenderInfo, receiverInfo);
    }
}
