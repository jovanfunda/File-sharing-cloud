package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.update.SystemUpdatedMessage;
import servent.message.update.UpdateSystemMessage;
import servent.message.util.MessageUtil;

public class UpdateSystemHandler implements MessageHandler {

    private final Message clientMessage;

    public UpdateSystemHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.UPDATE_SYSTEM) {
            if(clientMessage.getReceiverInfo().getId() == AppConfig.myServentInfo.getId()) {

                ServentInfo newServent = new ServentInfo(clientMessage.getOriginalSenderInfo().getIpAddress(),
                        ((UpdateSystemMessage)clientMessage).newNodeId,
                        clientMessage.getOriginalSenderInfo().getListenerPort(),
                        clientMessage.getOriginalSenderInfo().getNeighbors());

                AppConfig.addServentInfo(newServent);
                AppConfig.reorganizeArchitecture();

                MessageUtil.sendMessage(new SystemUpdatedMessage(AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo()));
            }
        } else {
            AppConfig.timestampedErrorPrint("Got message that is not of type Update System");
        }
    }
}
