package servent.message.update;

import app.AppConfig;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.Message;
import servent.message.MessageType;

import java.util.Objects;

public class SystemUpdatedMessage extends BasicMessage {

    public SystemUpdatedMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.SYSTEM_UPDATED, originalSenderInfo, receiverInfo);
    }

    public SystemUpdatedMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, int messageId) {
        super(MessageType.SYSTEM_UPDATED, originalSenderInfo, receiverInfo, "", messageId);
    }

    @Override
    public Message changeReceiver(Integer newReceiverId) {
        ServentInfo newReceiverInfo = AppConfig.getInfoById(newReceiverId);

        return new SystemUpdatedMessage(getOriginalSenderInfo(), newReceiverInfo, getMessageId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SystemUpdatedMessage other) {

            boolean messageId = getMessageId() == other.getMessageId();
            boolean id = getOriginalSenderInfo().getId() == other.getOriginalSenderInfo().getId();
            boolean port = getOriginalSenderInfo().getListenerPort() == other.getOriginalSenderInfo().getListenerPort();
            boolean serventValue = getReceiverInfo().equals(other.getReceiverInfo());

            return messageId && id && port && serventValue;
        }

        return false;
    }
}
