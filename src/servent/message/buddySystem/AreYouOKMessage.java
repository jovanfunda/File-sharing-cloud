package servent.message.buddySystem;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class AreYouOKMessage extends BasicMessage {

    public AreYouOKMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.ARE_YOU_OK, originalSenderInfo, receiverInfo);
    }
}
