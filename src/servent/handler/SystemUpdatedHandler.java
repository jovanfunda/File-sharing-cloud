package servent.handler;

import app.AppConfig;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;
import servent.message.Message;
import servent.message.MessageType;

public class SystemUpdatedHandler implements MessageHandler {

    private Message clientMessage;
    private DistributedMutex mutex;

    public SystemUpdatedHandler(Message clientMessage, DistributedMutex mutex) {
        this.clientMessage = clientMessage;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.SYSTEM_UPDATED) {
            if(clientMessage.getReceiverInfo() == AppConfig.myServentInfo) {
                ((SuzukiMutex) mutex).messagesReceived.addAndGet(1);
            }
        } else {
            AppConfig.timestampedErrorPrint("Got message that is not of type System updated");
        }
    }
}
