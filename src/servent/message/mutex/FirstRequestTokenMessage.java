package servent.message.mutex;

import app.AppConfig;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.Message;
import servent.message.MessageType;

public class FirstRequestTokenMessage extends BasicMessage {

    public FirstRequestTokenMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.FIRST_REQUEST, originalSenderInfo, receiverInfo);
    }

    public FirstRequestTokenMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, int messageId) {
        super(MessageType.FIRST_REQUEST, originalSenderInfo, receiverInfo, messageId);
    }

    @Override
    public Message changeReceiver(Integer newReceiverId) {
        ServentInfo newReceiverInfo = AppConfig.getInfoById(newReceiverId);

        return new FirstRequestTokenMessage(getOriginalSenderInfo(),
                newReceiverInfo, getMessageId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FirstRequestTokenMessage other) {

            boolean messageId = getMessageId() == other.getMessageId();
            boolean id = getOriginalSenderInfo().getId() == other.getOriginalSenderInfo().getId();
            boolean port = getOriginalSenderInfo().getListenerPort() == other.getOriginalSenderInfo().getListenerPort();
            boolean serventValue = getReceiverInfo().equals(other.getReceiverInfo());

            return messageId && id && port && serventValue;

        }

        return false;
    }
}
