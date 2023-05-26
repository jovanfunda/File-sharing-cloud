package servent.message.hello;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class HelloToNodeMessage extends BasicMessage {


    public HelloToNodeMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.HELLO_TO_NODE, originalSenderInfo, receiverInfo);
    }
}
