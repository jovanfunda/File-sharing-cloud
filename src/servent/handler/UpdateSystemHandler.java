package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.update.SystemUpdatedMessage;
import servent.message.util.MessageUtil;

public class UpdateSystemHandler implements MessageHandler {

    private Message clientMessage;

    public UpdateSystemHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.UPDATE_SYSTEM) {
            if(clientMessage.getReceiverInfo().getId() == AppConfig.myServentInfo.getId()) {

                // dobili smo nove informacije o sistemu, trebali bismo da ih sacuvamo
                // to se radi unutar AppConfig-a

                Message systemUpdated = new SystemUpdatedMessage(AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo());
                MessageUtil.sendMessage(systemUpdated);
            }
        } else {
            AppConfig.timestampedErrorPrint("Got message that is not of type Update System");
        }
    }
}
