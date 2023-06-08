package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;
import servent.message.hello.HelloToNodeMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.bootstrap.HelloFromBootstrapMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayList;

public class HelloFromBootstrapHandler implements MessageHandler {

    private Message clientMessage;

    private DistributedMutex mutex;

    public HelloFromBootstrapHandler(Message clientMessage, DistributedMutex mutex) {
        this.clientMessage = clientMessage;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.HELLO_FROM_BOOTSTRAP) {

            ServentInfo iShouldMessage = ((HelloFromBootstrapMessage)clientMessage).getWhoToMessageInfo();

            if(iShouldMessage == null) {

                ((SuzukiMutex)mutex).setTokenActive(true);

                mutex.lock();

                AppConfig.myServentInfo.setId(0);
                AppConfig.addServentInfo(AppConfig.myServentInfo);

                ((SuzukiMutex) mutex).finishedRequests.add(0);
                ((SuzukiMutex) mutex).requestsReceived.add(0);
                AppConfig.serventFiles.put(0, new ArrayList<>());

                AppConfig.timestampedStandardPrint("Primljen sam u arhitekturu kao prvi cvor!");

                Thread buddySystemThread = new Thread(((SuzukiMutex) mutex).buddySystem);
                buddySystemThread.start();

                mutex.unlock();

            } else {
                MessageUtil.sendMessage(new HelloToNodeMessage(AppConfig.myServentInfo, iShouldMessage));
                AppConfig.timestampedStandardPrint("Javljam se cvoru sa ID i portom " + iShouldMessage.getId() + " - " + iShouldMessage.getListenerPort());
            }
        }
    }
}
