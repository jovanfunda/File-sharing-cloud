package servent.message.buddySystem;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class PingMessage extends BasicMessage {

    public PingMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.PING, originalSenderInfo, receiverInfo);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PingMessage other) {

            boolean messageId = getMessageId() == other.getMessageId();
            boolean id = getOriginalSenderInfo().getId() == other.getOriginalSenderInfo().getId();
            boolean port = getOriginalSenderInfo().getListenerPort() == other.getOriginalSenderInfo().getListenerPort();
            boolean serventValue = getReceiverInfo().equals(other.getReceiverInfo());

            return messageId && id && port && serventValue;

        }

        return false;
    }
}
