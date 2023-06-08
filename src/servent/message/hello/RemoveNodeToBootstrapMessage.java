package servent.message.hello;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class RemoveNodeToBootstrapMessage extends BasicMessage {

    public ServentInfo removedNode;

    public RemoveNodeToBootstrapMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.REMOVE_NODE_TO_BOOTSTRAP, originalSenderInfo, receiverInfo);
    }
}
