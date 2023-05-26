package servent.message.update;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class SystemUpdatedMessage extends BasicMessage {

    public SystemUpdatedMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.SYSTEM_UPDATED, originalSenderInfo, receiverInfo);
    }
}
