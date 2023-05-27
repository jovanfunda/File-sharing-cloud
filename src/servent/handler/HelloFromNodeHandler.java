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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

            AppConfig.timestampedStandardPrint("Novi ID mi je " + newId);

            Set<Integer> myNewNeighbors = new HashSet<>();
            for(ServentInfo activeServent: activeServents) {
                if(activeServent.getId() == newId + 1 ||
                        activeServent.getId() == newId - 1 ||
                        activeServent.getId() == newId + 2 ||
                        activeServent.getId() == newId - 2) {
                    myNewNeighbors.add(activeServent.getId());
                }
            }

            AppConfig.timestampedStandardPrint("moje nove komsije.." + myNewNeighbors);

            for(Integer newNeighbor: myNewNeighbors) {
                AppConfig.myServentInfo.addNeighbor(newNeighbor);
            }

            AppConfig.serventInfoList = activeServents;

            Message updateSystem = new UpdateSystemMessage(AppConfig.myServentInfo, AppConfig.myServentInfo);

            for(ServentInfo s : activeServents) {
                updateSystem = updateSystem.changeReceiver(s.getId());
                MessageUtil.sendMessage(updateSystem);
                AppConfig.timestampedStandardPrint("Poslao sam poruku u HelloFromNodeHandler " + updateSystem);
            }

            while(((SuzukiMutex) mutex).messagesReceived.get() != activeServents.size()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            AppConfig.myServentInfo.setId(newId);
            AppConfig.addServentInfo(AppConfig.myServentInfo);

            ((SuzukiMutex) mutex).messagesReceived.set(0);

            AppConfig.timestampedStandardPrint("Svi su me prihvatili!");

            mutex.unlock();
        }
    }
}
