package servent.message;

import app.ServentInfo;

public class HelloToNodeMessage extends BasicMessage {


    public HelloToNodeMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.HELLO_TO_NODE, originalSenderInfo, receiverInfo);
    }
}
