package servent.handler.buddySystem;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.buddySystem.PongMessage;
import servent.message.util.MessageUtil;

public class PingHandler implements MessageHandler {

    public Message clientMessage;

    public PingHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        MessageUtil.sendMessage(new PongMessage(AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo()));
    }
}
