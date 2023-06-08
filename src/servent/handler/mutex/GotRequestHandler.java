package servent.handler.mutex;

import app.AppConfig;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;

public class GotRequestHandler implements MessageHandler {

    private Message clientMessage;
    private final DistributedMutex mutex;

    public GotRequestHandler(Message clientMessage, DistributedMutex mutex){
        this.clientMessage = clientMessage;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.GOT_REQUEST) {
            if (clientMessage.getReceiverInfo().getId() == AppConfig.myServentInfo.getId()) {
                ((SuzukiMutex) mutex).requestsTokenReceived.addAndGet(1);
            }

        } else {
            AppConfig.timestampedErrorPrint("Got message that is not of type Got request");
        }
    }
}
