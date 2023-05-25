package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.HelloToNodeMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.bootstrap.HelloFromBootstrapMessage;
import servent.message.util.MessageUtil;

public class HelloFromBootstrapHandler implements MessageHandler {

    private Message clientMessage;

    public HelloFromBootstrapHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.HELLO_FROM_BOOTSTRAP) {

            ServentInfo iShouldMessage = ((HelloFromBootstrapMessage)clientMessage).getWhoToMessageInfo();

            if(iShouldMessage == null) {

                AppConfig.myServentInfo.setId(0);
                AppConfig.addServentInfo(AppConfig.myServentInfo);

                AppConfig.timestampedStandardPrint("Primljen sam u arhitekturu kao prvi cvor!");

            } else {
                MessageUtil.sendMessage(new HelloToNodeMessage(AppConfig.myServentInfo, iShouldMessage));
            }
        }
    }
}
