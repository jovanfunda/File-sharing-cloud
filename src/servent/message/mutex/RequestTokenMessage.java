package servent.message.mutex;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class RequestTokenMessage extends BasicMessage {

    int sequenceCounter;

    public RequestTokenMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, int sequenceNumber) {
        super(MessageType.REQUEST_TOKEN, originalSenderInfo, receiverInfo);
        sequenceCounter = sequenceNumber;
    }

    public int getSequenceCounter() {
        return sequenceCounter;
    }
}
