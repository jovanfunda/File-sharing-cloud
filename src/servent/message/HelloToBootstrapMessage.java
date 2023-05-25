package servent.message;

import app.AppConfig;
import app.ServentInfo;

public class HelloToBootstrapMessage extends BasicMessage {

    public HelloToBootstrapMessage(ServentInfo originalSenderInfo) {
        super(MessageType.HELLO_TO_BOOTSTRAP, originalSenderInfo, AppConfig.getBootstrapNode());
    }

}
