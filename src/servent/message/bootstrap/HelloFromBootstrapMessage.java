package servent.message.bootstrap;

import app.ServentInfo;
import app.bootstrap.BootstrapNode;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class HelloFromBootstrapMessage extends BasicMessage {

    private ServentInfo whoToMessageInfo;

    public HelloFromBootstrapMessage(ServentInfo receiverInfo) {
        super(MessageType.HELLO_FROM_BOOTSTRAP, BootstrapNode.bootstrapInfo, receiverInfo);
    }

    public HelloFromBootstrapMessage(ServentInfo receiverInfo, ServentInfo whoToMessageInfo) {
        super(MessageType.HELLO_FROM_BOOTSTRAP, BootstrapNode.bootstrapInfo, receiverInfo);
        this.whoToMessageInfo = whoToMessageInfo;
    }

    public ServentInfo getWhoToMessageInfo() {
        return whoToMessageInfo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HelloFromBootstrapMessage other) {

            boolean messageId = getMessageId() == other.getMessageId();
            boolean id = getOriginalSenderInfo().getId() == other.getOriginalSenderInfo().getId();
            boolean port = getOriginalSenderInfo().getListenerPort() == other.getOriginalSenderInfo().getListenerPort();
            boolean serventValue = getReceiverInfo().equals(other.getReceiverInfo());

            return messageId && id && port && serventValue;

        }

        return false;
    }
}
