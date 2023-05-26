package servent.message.hello;

import app.AppConfig;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class HelloToBootstrapMessage extends BasicMessage {

    public HelloToBootstrapMessage(ServentInfo originalSenderInfo) {
        super(MessageType.HELLO_TO_BOOTSTRAP, originalSenderInfo, AppConfig.getBootstrapNode());
    }

}
