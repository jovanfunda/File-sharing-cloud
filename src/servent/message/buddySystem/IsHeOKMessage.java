package servent.message.buddySystem;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class IsHeOKMessage extends BasicMessage {

    public IsHeOKMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.IS_HE_OK, originalSenderInfo, receiverInfo);
    }
}
