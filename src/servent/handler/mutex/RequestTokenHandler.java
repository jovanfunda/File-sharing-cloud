package servent.handler.mutex;

import app.AppConfig;
import app.ServentInfo;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.mutex.FirstRequestTokenMessage;
import servent.message.mutex.GotRequestMessage;
import servent.message.mutex.RequestTokenMessage;
import servent.message.mutex.TokenMessage;
import servent.message.util.MessageUtil;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RequestTokenHandler implements MessageHandler {

    private Message clientMessage;
    private final DistributedMutex mutex;

    public RequestTokenHandler(Message clientMessage, DistributedMutex mutex){
        this.clientMessage = clientMessage;
        this.mutex = mutex;
    }

    @Override
    public void run() {

        if(clientMessage.getReceiverInfo().getId() == AppConfig.myServentInfo.getId()) {

            MessageUtil.sendMessage(new GotRequestMessage(AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo()));

            // Ukoliko mi je cvor koji nije u arhitekturi poslao zahtev preko FirstTokenRequesta
            if(clientMessage.getOriginalSenderInfo().getId() == -1) {
                if (((SuzukiMutex) mutex).hasToken() && !((SuzukiMutex) mutex).usingToken) {
                    ((SuzukiMutex) mutex).setTokenActive(false);
                    TokenMessage tokenMessage = new TokenMessage(AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo(), ((SuzukiMutex) mutex).serventsWaiting);
                    tokenMessage.finishedRequests = ((SuzukiMutex) mutex).finishedRequests;
                    MessageUtil.sendMessage(tokenMessage);
                }
                return;
            }

            // Apdejtujemo nas request received u zavisnoti od poruke koje smo dobili
            if (((SuzukiMutex) mutex).requestsReceived.get(clientMessage.getOriginalSenderInfo().getId()) <= ((RequestTokenMessage) clientMessage).sequenceCounter) {
                ((SuzukiMutex) mutex).requestsReceived.set(clientMessage.getOriginalSenderInfo().getId(), ((RequestTokenMessage) clientMessage).sequenceCounter);
            }

            // Ukoliko zahtev nije zastareo
            if (((SuzukiMutex) mutex).requestsReceived.get(clientMessage.getOriginalSenderInfo().getId()) <= ((RequestTokenMessage) clientMessage).sequenceCounter) {
                if (((SuzukiMutex) mutex).hasToken() && !((SuzukiMutex) mutex).usingToken) {
                    ((SuzukiMutex) mutex).setTokenActive(false);
                    TokenMessage tokenMessage = new TokenMessage(AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo(), ((SuzukiMutex) mutex).serventsWaiting);
                    tokenMessage.finishedRequests = ((SuzukiMutex) mutex).finishedRequests;
                    MessageUtil.sendMessage(tokenMessage);
                }
            }
        }
    }
}
