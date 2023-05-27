package servent.message.update;

import app.AppConfig;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.Message;
import servent.message.MessageType;

public class UpdateSystemMessage extends BasicMessage {

    // informacije o tome sta saljemo kroz ovu poruku
    // sta sve treba da se apdejtuje?

    public UpdateSystemMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.UPDATE_SYSTEM, originalSenderInfo, receiverInfo);
    }

    public UpdateSystemMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, int messageId) {
        super(MessageType.UPDATE_SYSTEM, originalSenderInfo, receiverInfo, "", messageId);
    }

    @Override
    public Message changeReceiver(Integer newReceiverId) {
        if (AppConfig.myServentInfo.getNeighbors().contains(newReceiverId)) {
            ServentInfo newReceiverInfo = AppConfig.getInfoById(newReceiverId);

            return new UpdateSystemMessage(getOriginalSenderInfo(),
                    newReceiverInfo, getMessageId());
        } else {
            AppConfig.timestampedErrorPrint("Trying to make a message for " + newReceiverId + " who is not a neighbor.");

            return null;
        }

    }
}
