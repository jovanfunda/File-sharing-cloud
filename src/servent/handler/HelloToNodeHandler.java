package servent.handler;

import app.AppConfig;
import servent.message.hello.HelloFromNodeMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;

public class HelloToNodeHandler implements MessageHandler {

    private Message clientMessage;

    public HelloToNodeHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.HELLO_TO_NODE) {
            MessageUtil.sendMessage(new HelloFromNodeMessage(AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo()));
        }
    }
}
