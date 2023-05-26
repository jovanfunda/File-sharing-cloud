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

            ((SuzukiMutex)mutex).setNodeWithInfo(clientMessage.getOriginalSenderInfo());

            mutex.lock();

            AppConfig.myServentInfo.setId(newId);
            AppConfig.addServentInfo(AppConfig.myServentInfo);
            AppConfig.timestampedStandardPrint("Primljen sam u arhitekturu i imam ID " + newId + "!");

            Message updateSystem = new UpdateSystemMessage(AppConfig.myServentInfo, AppConfig.myServentInfo);

            for(ServentInfo s : activeServents) {
                updateSystem.changeReceiver(s.getId());
                MessageUtil.sendMessage(updateSystem);
            }

            while(((SuzukiMutex) mutex).messagesReceived.get() != activeServents.size()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            ((SuzukiMutex) mutex).messagesReceived.set(0);

            AppConfig.timestampedStandardPrint("Svi su me prihvatili!");

            mutex.unlock();
        }
    }
}
