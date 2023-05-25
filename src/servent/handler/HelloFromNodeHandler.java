package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.HelloFromNodeMessage;
import servent.message.Message;
import servent.message.MessageType;

import java.util.List;

public class HelloFromNodeHandler implements MessageHandler {

    private Message clientMessage;

    public HelloFromNodeHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.HELLO_FROM_NODE) {
            List<ServentInfo> activeServents = ((HelloFromNodeMessage)clientMessage).getActiveServents();
            int newId = -1;

            for(int i = 0; i < activeServents.size(); i++) {
                if(activeServents.get(i).getId() != i) {
                    newId = i;
                    break;
                }
            }

            if(newId == -1) {
                newId = activeServents.size();
            }

            AppConfig.myServentInfo.setId(newId);
            AppConfig.addServentInfo(AppConfig.myServentInfo);
            AppConfig.timestampedStandardPrint("Primljen sam u arhitekturu i imam ID " + newId + "!");

        }
    }
}
