package servent.message.hello;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class NewNodeToBootstrapMessage extends BasicMessage {

    public ServentInfo newNode;

    public NewNodeToBootstrapMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.NEW_NODE_TO_BOOTSTRAP, originalSenderInfo, receiverInfo);
    }
}
