package servent.handler;

import app.AppConfig;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.update.SystemUpdatedMessage;
import servent.message.update.UpdateSystemMessage;
import servent.message.util.MessageUtil;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SystemUpdatedHandler implements MessageHandler {

    private Message clientMessage;
    private final DistributedMutex mutex;

    public SystemUpdatedHandler(Message clientMessage, DistributedMutex mutex) {
        this.clientMessage = clientMessage;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.SYSTEM_UPDATED) {
            if (clientMessage.getReceiverInfo().getId() == AppConfig.myServentInfo.getId()) {
                ((SuzukiMutex) mutex).systemUpdatedMessagesReceived.addAndGet(1);
            }
        } else {
            AppConfig.timestampedErrorPrint("Got message that is not of type System updated");
        }
    }
}
