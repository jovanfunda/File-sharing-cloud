package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.update.SystemUpdatedMessage;
import servent.message.update.UpdateSystemMessage;
import servent.message.util.MessageUtil;

import java.util.*;

public class UpdateSystemHandler implements MessageHandler {

    private Message clientMessage;
    private DistributedMutex mutex;

    public UpdateSystemHandler(Message clientMessage, DistributedMutex mutex) {
        this.clientMessage = clientMessage;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.UPDATE_SYSTEM) {

            if (clientMessage.getReceiverInfo().getId() == AppConfig.myServentInfo.getId()) {
                if (((UpdateSystemMessage) clientMessage).newNodeId != -1) {
                    ServentInfo newServent = new ServentInfo(clientMessage.getOriginalSenderInfo().getIpAddress(),
                            ((UpdateSystemMessage) clientMessage).newNodeId,
                            clientMessage.getOriginalSenderInfo().getListenerPort(),
                            clientMessage.getOriginalSenderInfo().getNeighbors());

                    AppConfig.addServentInfo(newServent);
                    AppConfig.reorganizeArchitecture();
                    ((SuzukiMutex) mutex).finishedRequests.add(0);
                    ((SuzukiMutex) mutex).requestsReceived.add(0);
                    AppConfig.serventFiles.put(((UpdateSystemMessage) clientMessage).newNodeId, new ArrayList<>());

                } else if (((UpdateSystemMessage) clientMessage).newFiles.size() != 0) {

                    List<String> filesThatIHave = AppConfig.serventFiles.get(AppConfig.myServentInfo.getId());


                    List<String> newFilesList = new ArrayList<>(AppConfig.serventFiles.get(clientMessage.getOriginalSenderInfo().getId()));

                    for(String fileName : ((UpdateSystemMessage) clientMessage).newFiles) {
                        if(!filesThatIHave.contains(fileName)) {
                            newFilesList.add(fileName);
                        }
                    }
                    AppConfig.serventFiles.put(clientMessage.getOriginalSenderInfo().getId(), newFilesList);

                    ((UpdateSystemMessage) clientMessage).newFiles = new ArrayList<>();

                    int prevFinishedRequest = ((SuzukiMutex) mutex).finishedRequests.get(clientMessage.getOriginalSenderInfo().getId());
                    ((SuzukiMutex) mutex).finishedRequests.set(clientMessage.getOriginalSenderInfo().getId(), prevFinishedRequest + 1);

                } else if (((UpdateSystemMessage) clientMessage).removedFiles.size() != 0) {

                    List<String> filesThatIHave = AppConfig.serventFiles.get(AppConfig.myServentInfo.getId());

                    List<String> removedFilesList = new ArrayList<>(AppConfig.serventFiles.get(clientMessage.getOriginalSenderInfo().getId()));

                    for(String fileName : ((UpdateSystemMessage) clientMessage).removedFiles) {
                        if(filesThatIHave.contains(fileName)) {
                            removedFilesList.remove(fileName);
                        }
                    }
                    AppConfig.serventFiles.put(clientMessage.getOriginalSenderInfo().getId(), removedFilesList);

                    ((UpdateSystemMessage) clientMessage).removedFiles = new ArrayList<>();

                    int prevFinishedRequest = ((SuzukiMutex) mutex).finishedRequests.get(clientMessage.getOriginalSenderInfo().getId());
                    ((SuzukiMutex) mutex).finishedRequests.set(clientMessage.getOriginalSenderInfo().getId(), prevFinishedRequest + 1);
                }

                MessageUtil.sendMessage(new SystemUpdatedMessage(AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo()));
            }
        } else {
            AppConfig.timestampedErrorPrint("Got message that is not of type Update System");
        }
    }
}
