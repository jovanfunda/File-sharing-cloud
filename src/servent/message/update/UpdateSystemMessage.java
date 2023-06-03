package servent.message.update;

import app.AppConfig;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.Message;
import servent.message.MessageType;

import java.util.ArrayList;
import java.util.List;

public class UpdateSystemMessage extends BasicMessage {

    public int newNodeId = -1;

    public List<String> newFiles = new ArrayList<>();

    public List<String> removedFiles = new ArrayList<>();

    public UpdateSystemMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.UPDATE_SYSTEM, originalSenderInfo, receiverInfo);
    }

    public UpdateSystemMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, int newNodeId) {
        super(MessageType.UPDATE_SYSTEM, originalSenderInfo, receiverInfo);
        this.newNodeId = newNodeId;
    }

    public UpdateSystemMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, int newNodeId, int messageId) {
        super(MessageType.UPDATE_SYSTEM, originalSenderInfo, receiverInfo, messageId);
        this.newNodeId = newNodeId;
    }

    @Override
    public Message changeReceiver(Integer newReceiverId) {
            ServentInfo newReceiverInfo = AppConfig.getInfoById(newReceiverId);
            UpdateSystemMessage message = new UpdateSystemMessage(getOriginalSenderInfo(), newReceiverInfo, newNodeId, getMessageId());
            message.newFiles = newFiles;
            message.removedFiles = removedFiles;

            return message;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UpdateSystemMessage other) {

            boolean messageId = getMessageId() == other.getMessageId();
            boolean id = getOriginalSenderInfo().getId() == other.getOriginalSenderInfo().getId();
            boolean port = getOriginalSenderInfo().getListenerPort() == other.getOriginalSenderInfo().getListenerPort();
            boolean serventValue = getReceiverInfo().equals(other.getReceiverInfo());

            return messageId && id && port && serventValue;
        }

        return false;
    }
}
