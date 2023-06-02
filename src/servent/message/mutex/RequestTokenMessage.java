package servent.message.mutex;

import app.AppConfig;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.Message;
import servent.message.MessageType;

public class RequestTokenMessage extends BasicMessage {

    public int sequenceCounter;

    public RequestTokenMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, int sequenceNumber) {
        super(MessageType.REQUEST_TOKEN, originalSenderInfo, receiverInfo);
        sequenceCounter = sequenceNumber;
    }

    public RequestTokenMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, int sequenceNumber, int messageId) {
        super(MessageType.REQUEST_TOKEN, originalSenderInfo, receiverInfo, "", messageId);
        sequenceCounter = sequenceNumber;
    }

    @Override
    public Message changeReceiver(Integer newReceiverId) {
        ServentInfo newReceiverInfo = AppConfig.getInfoById(newReceiverId);

        return new RequestTokenMessage(getOriginalSenderInfo(),
                newReceiverInfo, sequenceCounter, getMessageId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RequestTokenMessage other) {

            boolean messageId = getMessageId() == other.getMessageId();
            boolean id = getOriginalSenderInfo().getId() == other.getOriginalSenderInfo().getId();
            boolean port = getOriginalSenderInfo().getListenerPort() == other.getOriginalSenderInfo().getListenerPort();
            boolean serventValue = getReceiverInfo().equals(other.getReceiverInfo());

            return messageId && id && port && serventValue;

        }

        return false;
    }
}
