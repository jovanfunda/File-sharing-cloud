package servent.handler.buddySystem;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.buddySystem.AreYouOKMessage;
import servent.message.buddySystem.HeIsNotOKMessage;
import servent.message.buddySystem.HeIsOKMessage;
import servent.message.util.MessageUtil;

public class IsHeOKHandler implements MessageHandler {

    public Message clientMessage;

    public static boolean hisOKState = false;

    public IsHeOKHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        MessageUtil.sendMessage(new AreYouOKMessage(AppConfig.myServentInfo, AppConfig.previousNode(AppConfig.myServentInfo)));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(hisOKState) {
            MessageUtil.sendMessage(new HeIsOKMessage(AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo()));
            hisOKState = false;
        } else {
            MessageUtil.sendMessage(new HeIsNotOKMessage(AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo()));
        }

    }
}
