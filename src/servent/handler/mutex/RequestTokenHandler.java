package servent.handler.mutex;

import app.AppConfig;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.mutex.RequestTokenMessage;
import servent.message.mutex.TokenMessage;
import servent.message.util.MessageUtil;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RequestTokenHandler implements MessageHandler {

    private Message clientMessage;
    private final DistributedMutex mutex;
    private static final Set<RequestTokenMessage> receivedMessages = Collections.newSetFromMap(new ConcurrentHashMap<RequestTokenMessage, Boolean>());

    public RequestTokenHandler(Message clientMessage, DistributedMutex mutex){
        this.clientMessage = clientMessage;
        this.mutex = mutex;
    }

    @Override
    public void run() {

        boolean didPut = receivedMessages.add((RequestTokenMessage) clientMessage);

        if (didPut) {

            AppConfig.timestampedStandardPrint(((SuzukiMutex)mutex).requestsReceived  + " " + ((RequestTokenMessage)clientMessage).sequenceCounter);

            if(clientMessage.getOriginalSenderInfo().getId() != -1) {
                if (((SuzukiMutex) mutex).requestsReceived.get(clientMessage.getOriginalSenderInfo().getId()) <= ((RequestTokenMessage) clientMessage).sequenceCounter) {
                    ((SuzukiMutex) mutex).requestsReceived.set(clientMessage.getOriginalSenderInfo().getId(), ((RequestTokenMessage) clientMessage).sequenceCounter);
                } else {
                    return;
                    // ? zahtev je zastareo
                }
            }

            if (((SuzukiMutex)mutex).hasToken()) {
                while (((SuzukiMutex)mutex).usingToken) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                ((SuzukiMutex)mutex).setTokenActive(false);
                MessageUtil.sendMessage(new TokenMessage(AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo(), ((SuzukiMutex)mutex).serventsWaiting, ((SuzukiMutex)mutex).finishedRequests));
            } else {
                for (Integer neighbor : AppConfig.myServentInfo.getNeighbors()) {
                    clientMessage = clientMessage.changeReceiver(neighbor);
                    MessageUtil.sendMessage(clientMessage);
                }
            }
        }
    }
}
