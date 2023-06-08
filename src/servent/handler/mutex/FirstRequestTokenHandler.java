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

public class FirstRequestTokenHandler implements MessageHandler {

    private Message clientMessage;
    private final DistributedMutex mutex;

    public FirstRequestTokenHandler(Message clientMessage, DistributedMutex mutex){
        this.clientMessage = clientMessage;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        if (((SuzukiMutex) mutex).hasToken() && ((SuzukiMutex) mutex).usingToken) {
            ((SuzukiMutex) mutex).newNodeWaiting = clientMessage.getOriginalSenderInfo();
        } else if (((SuzukiMutex) mutex).hasToken() && !((SuzukiMutex) mutex).usingToken) {
            ((SuzukiMutex) mutex).setTokenActive(false);
            TokenMessage tokenMessage = new TokenMessage(AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo(), ((SuzukiMutex) mutex).serventsWaiting);
            tokenMessage.finishedRequests = ((SuzukiMutex) mutex).finishedRequests;
            MessageUtil.sendMessage(tokenMessage);
        } else {
            Message message = new RequestTokenMessage(clientMessage.getOriginalSenderInfo(), clientMessage.getOriginalSenderInfo(), -1);
            for (ServentInfo s : AppConfig.serventInfoList) {
                if (s != AppConfig.myServentInfo) {
                    message = message.changeReceiver(s.getId());
                    MessageUtil.sendMessage(message);
                }
            }
        }
    }
}
