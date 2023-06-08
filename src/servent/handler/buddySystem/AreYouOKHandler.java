package servent.handler.buddySystem;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.buddySystem.OKMessage;
import servent.message.util.MessageUtil;

public class AreYouOKHandler implements MessageHandler {

    public Message clientMessage;

    public AreYouOKHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        MessageUtil.sendMessage(new OKMessage(AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo()));
    }
}
