package servent.message.update;

import app.AppConfig;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.Message;
import servent.message.MessageType;

public class UpdateSystemMessage extends BasicMessage {

    public int newNodeId;

    public UpdateSystemMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, int newNodeId) {
        super(MessageType.UPDATE_SYSTEM, originalSenderInfo, receiverInfo);
        this.newNodeId = newNodeId;
    }

    public UpdateSystemMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, int newNodeId, int messageId) {
        super(MessageType.UPDATE_SYSTEM, originalSenderInfo, receiverInfo, "", messageId);
        this.newNodeId = newNodeId;
    }

    @Override
    public Message changeReceiver(Integer newReceiverId) {
        if (AppConfig.myServentInfo.getNeighbors().contains(newReceiverId)) {
            ServentInfo newReceiverInfo = AppConfig.getInfoById(newReceiverId);

            return new UpdateSystemMessage(getOriginalSenderInfo(),
                    newReceiverInfo, newNodeId, getMessageId());
        } else {
            AppConfig.timestampedErrorPrint("Trying to make a message for " + newReceiverId + " who is not a neighbor.");

            return null;
        }

    }
}
