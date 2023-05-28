package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;
import servent.message.hello.HelloFromNodeMessage;
import servent.message.Message;
import servent.message.MessageType;
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

            mutex.lock();

            List<ServentInfo> activeServents = ((HelloFromNodeMessage)clientMessage).getActiveServents();
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

            AppConfig.serventInfoList = new ArrayList<>(activeServents);
            AppConfig.addServentInfo(AppConfig.myServentInfo);

            for(ServentInfo s : activeServents) {
                MessageUtil.sendMessage(new UpdateSystemMessage(AppConfig.myServentInfo, AppConfig.getInfoById(s.getId()), newId));
            }

            while(((SuzukiMutex) mutex).messagesReceived.get() != activeServents.size()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }


            AppConfig.myServentInfo.setId(newId);
            AppConfig.reorganizeArchitecture();

            ((SuzukiMutex) mutex).messagesReceived.set(0);

            AppConfig.timestampedStandardPrint("Svi su me prihvatili! Novi ID mi je " + newId);

            mutex.unlock();
        }
    }
}
