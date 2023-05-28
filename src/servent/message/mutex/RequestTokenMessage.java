package servent.message.mutex;

import app.AppConfig;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.Message;
import servent.message.MessageType;

public class RequestTokenMessage extends BasicMessage {

    int sequenceCounter;

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
        if (AppConfig.myServentInfo.getNeighbors().contains(newReceiverId)) {
            ServentInfo newReceiverInfo = AppConfig.getInfoById(newReceiverId);

            Message toReturn = new RequestTokenMessage(getOriginalSenderInfo(),
                    newReceiverInfo, sequenceCounter, getMessageId());

            return toReturn;
        } else {
            AppConfig.timestampedErrorPrint("Trying to make a message for " + newReceiverId + " who is not a neighbor.");

            return null;
        }

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RequestTokenMessage) {
            RequestTokenMessage other = (RequestTokenMessage)obj;

            if (getMessageId() == other.getMessageId() &&
                    getOriginalSenderInfo().getId() == other.getOriginalSenderInfo().getId() &&
                    getOriginalSenderInfo().getListenerPort() == other.getOriginalSenderInfo().getListenerPort()) {
                return true;
            }
        }

        return false;
    }
}
