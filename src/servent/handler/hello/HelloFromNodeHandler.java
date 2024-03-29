package servent.handler.hello;

import app.AppConfig;
import app.ServentInfo;
import app.buddySystem.BuddySystem;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;
import servent.handler.MessageHandler;
import servent.handler.SystemUpdatedHandler;
import servent.message.BasicMessage;
import servent.message.hello.HelloFromNodeMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.hello.NewNodeToBootstrapMessage;
import servent.message.update.UpdateSystemMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;

public class HelloFromNodeHandler implements MessageHandler {

    private Message clientMessage;
    private DistributedMutex mutex;

    public HelloFromNodeHandler(Message clientMessage, DistributedMutex mutex) {
        this.clientMessage = clientMessage;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.HELLO_FROM_NODE) {

            ((SuzukiMutex)mutex).setNodeWithInfo(clientMessage.getOriginalSenderInfo());

            List<ServentInfo> activeServents = ((HelloFromNodeMessage)clientMessage).getActiveServents();

            AppConfig.serventInfoList = new ArrayList<>(activeServents);
            AppConfig.addServentInfo(AppConfig.myServentInfo);

            mutex.lock();

            int newId = -1;

            for(int i = 0; i < activeServents.size(); i++) {
                if(activeServents.get(i).getId() != i) {
                    newId = i;
                    break;
                }
            }

            if(newId == -1) {
                newId = activeServents.size();
            }

            for(ServentInfo s : activeServents) {
                MessageUtil.sendMessage(new UpdateSystemMessage(AppConfig.myServentInfo, AppConfig.getInfoById(s.getId()), newId));
            }

            while(((SuzukiMutex) mutex).systemUpdatedMessagesReceived.get() != activeServents.size()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            ((SuzukiMutex) mutex).systemUpdatedMessagesReceived.set(0);

            AppConfig.myServentInfo.setId(newId);
            AppConfig.reorganizeArchitecture();

            BasicMessage.messageCounter.set(SystemUpdatedHandler.maxMessageId);
            SystemUpdatedHandler.maxMessageId = 0;

            AppConfig.serventFiles = ((HelloFromNodeMessage) clientMessage).serventFiles;
            AppConfig.serventFiles.put(newId, new ArrayList<>());
            // ovo treba da se promeni kod Backup-a


            ((SuzukiMutex) mutex).finishedRequests = new ArrayList<>(((HelloFromNodeMessage) clientMessage).requestsList);
            ((SuzukiMutex) mutex).requestsReceived = new ArrayList<>(((HelloFromNodeMessage) clientMessage).requestsList);

            if(((SuzukiMutex) mutex).finishedRequests.size() == newId) {
                ((SuzukiMutex) mutex).finishedRequests.add(0);
                ((SuzukiMutex) mutex).requestsReceived.add(0);
            } else {
                ((SuzukiMutex) mutex).finishedRequests.set(newId, ((SuzukiMutex) mutex).finishedRequests.get(newId) - 1);
                ((SuzukiMutex) mutex).requestsReceived.set(newId, ((SuzukiMutex) mutex).requestsReceived.get(newId) - 1);
            }

            // Saljemo poruku bootstrapu da imamo novi cvor u sistemu
            NewNodeToBootstrapMessage message = new NewNodeToBootstrapMessage(AppConfig.myServentInfo, AppConfig.getBootstrapNode());
            message.newNode = AppConfig.myServentInfo;
            MessageUtil.sendMessage(message);

            AppConfig.timestampedStandardPrint("Svi su me prihvatili! Novi ID mi je " + newId);

            Thread buddySystemThread = new Thread(((SuzukiMutex) mutex).buddySystem);
            buddySystemThread.start();

            mutex.unlock();
        }
    }
}
