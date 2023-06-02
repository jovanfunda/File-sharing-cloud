package servent.message.hello;

import app.AppConfig;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;
import servent.message.file.PullFileMessage;

public class HelloToBootstrapMessage extends BasicMessage {

    public HelloToBootstrapMessage(ServentInfo originalSenderInfo) {
        super(MessageType.HELLO_TO_BOOTSTRAP, originalSenderInfo, AppConfig.getBootstrapNode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HelloToBootstrapMessage other) {

            boolean messageId = getMessageId() == other.getMessageId();
            boolean id = getOriginalSenderInfo().getId() == other.getOriginalSenderInfo().getId();
            boolean port = getOriginalSenderInfo().getListenerPort() == other.getOriginalSenderInfo().getListenerPort();
            boolean serventValue = getReceiverInfo().equals(other.getReceiverInfo());

            return messageId && id && port && serventValue;

        }

        return false;
    }

}
