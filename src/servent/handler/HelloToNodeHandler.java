package servent.handler;

import app.AppConfig;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;
import servent.message.hello.HelloFromNodeMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;

public class HelloToNodeHandler implements MessageHandler {

    private Message clientMessage;

    private DistributedMutex mutex;

    public HelloToNodeHandler(Message clientMessage, DistributedMutex mutex) {
        this.clientMessage = clientMessage;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.HELLO_TO_NODE) {
            MessageUtil.sendMessage(new HelloFromNodeMessage(AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo(), ((SuzukiMutex) mutex).finishedRequests));
        }
    }
}
