package servent.message.update;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class UpdateSystemMessage extends BasicMessage {

    // informacije o tome sta saljemo kroz ovu poruku
    // sta sve treba da se apdejtuje?

    public UpdateSystemMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.UPDATE_SYSTEM, originalSenderInfo, receiverInfo);
    }
}
