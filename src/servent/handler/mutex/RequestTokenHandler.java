package servent.handler.mutex;

import app.AppConfig;
import app.ServentInfo;
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

    public RequestTokenHandler(Message clientMessage, DistributedMutex mutex){
        this.clientMessage = clientMessage;
        this.mutex = mutex;
    }

    @Override
    public void run() {

        if(clientMessage.getReceiverInfo().getId() == AppConfig.myServentInfo.getId()) {

            if(clientMessage.getOriginalSenderInfo().getId() == -1) {
                if(((SuzukiMutex) mutex).hasToken() && ((SuzukiMutex) mutex).usingToken) {
                    ((SuzukiMutex) mutex).newNodeWaiting = clientMessage.getOriginalSenderInfo();
                } else if(((SuzukiMutex) mutex).hasToken() && !((SuzukiMutex) mutex).usingToken) {
                    ((SuzukiMutex) mutex).setTokenActive(false);
                    TokenMessage tokenMessage = new TokenMessage(AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo(), ((SuzukiMutex) mutex).serventsWaiting);
                    tokenMessage.finishedRequests = ((SuzukiMutex) mutex).finishedRequests;
                    MessageUtil.sendMessage(tokenMessage);
                } else {
                    // Kada se novi cvor prikljucuje salje samo ovom nodu, pa ce ovaj node da prosledi dalje ukoliko nema token
                    for (ServentInfo s : AppConfig.serventInfoList) {
                        if (s != AppConfig.myServentInfo) {
                            clientMessage = clientMessage.changeReceiver(s.getId());
                            MessageUtil.sendMessage(clientMessage);
                        }
                    }
                }
                return;
            }

            if (((SuzukiMutex) mutex).requestsReceived.get(clientMessage.getOriginalSenderInfo().getId()) <= ((RequestTokenMessage) clientMessage).sequenceCounter) {
                ((SuzukiMutex) mutex).requestsReceived.set(clientMessage.getOriginalSenderInfo().getId(), ((RequestTokenMessage) clientMessage).sequenceCounter);
            }

            if (((SuzukiMutex) mutex).hasToken() && !((SuzukiMutex) mutex).usingToken) {
                ((SuzukiMutex) mutex).setTokenActive(false);
                TokenMessage tokenMessage = new TokenMessage(AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo(), ((SuzukiMutex) mutex).serventsWaiting);
                tokenMessage.finishedRequests = ((SuzukiMutex) mutex).finishedRequests;
//                tokenMessage.serventsWaiting.poll(); ??
                AppConfig.timestampedStandardPrint("Saljem poruku ka " + clientMessage.getOriginalSenderInfo().getId());
                MessageUtil.sendMessage(tokenMessage);
            }
        }
    }
}
