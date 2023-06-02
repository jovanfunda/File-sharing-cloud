package servent.message.hello;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class HelloToNodeMessage extends BasicMessage {


    public HelloToNodeMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.HELLO_TO_NODE, originalSenderInfo, receiverInfo);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HelloToNodeMessage other) {

            boolean messageId = getMessageId() == other.getMessageId();
            boolean id = getOriginalSenderInfo().getId() == other.getOriginalSenderInfo().getId();
            boolean port = getOriginalSenderInfo().getListenerPort() == other.getOriginalSenderInfo().getListenerPort();
            boolean serventValue = getReceiverInfo().equals(other.getReceiverInfo());

            return messageId && id && port && serventValue;

        }

        return false;
    }
}
