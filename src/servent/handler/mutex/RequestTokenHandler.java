package servent.handler.mutex;

import app.AppConfig;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.mutex.TokenMessage;
import servent.message.util.MessageUtil;

public class RequestTokenHandler implements MessageHandler {

    private Message clientMessage;
    private final SuzukiMutex mutex;

    public RequestTokenHandler(Message clientMessage, DistributedMutex mutex){
        this.clientMessage = clientMessage;
        this.mutex = (SuzukiMutex) mutex;
    }

    @Override
    public void run() {

        if(mutex.hasToken()) {
            while(mutex.usingToken) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            mutex.setTokenActive(false);
            MessageUtil.sendMessage(new TokenMessage(AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo()));
        } else {
            for(Integer neighbor: AppConfig.myServentInfo.getNeighbors()) {
                clientMessage = clientMessage.changeReceiver(neighbor);
                MessageUtil.sendMessage(clientMessage);
            }
        }
    }
}
