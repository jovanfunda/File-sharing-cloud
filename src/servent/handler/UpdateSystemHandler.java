package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.hello.NewNodeToBootstrapMessage;
import servent.message.hello.RemoveNodeToBootstrapMessage;
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

                int newNodeMessageId = -1;

                if (((UpdateSystemMessage) clientMessage).newNodeId != -1) {
                    ServentInfo newServent = new ServentInfo(clientMessage.getOriginalSenderInfo().getIpAddress(),
                            ((UpdateSystemMessage) clientMessage).newNodeId,
                            clientMessage.getOriginalSenderInfo().getListenerPort(),
                            clientMessage.getOriginalSenderInfo().getNeighbors());

                    AppConfig.addServentInfo(newServent);
                    AppConfig.reorganizeArchitecture();

                    if(((SuzukiMutex) mutex).finishedRequests.size() == ((UpdateSystemMessage) clientMessage).newNodeId) {
                        ((SuzukiMutex) mutex).finishedRequests.add(0);
                        ((SuzukiMutex) mutex).requestsReceived.add(0);
                    } else {
                        for(Message m : AppConfig.receivedMessages) {
                            if(m.getOriginalSenderInfo().getId() == ((UpdateSystemMessage) clientMessage).newNodeId) {
                                if(m.getMessageId() > newNodeMessageId) {
                                    newNodeMessageId = m.getMessageId();
                                }
                            }
                        }
                    }

                    AppConfig.serventFiles.put(((UpdateSystemMessage) clientMessage).newNodeId, new ArrayList<>());
                    // ovo treba da se promeni kod Backupa


                    ((SuzukiMutex) mutex).finishedRequests.set(((UpdateSystemMessage) clientMessage).newNodeId, 1);
                    ((SuzukiMutex) mutex).requestsReceived.set(((UpdateSystemMessage) clientMessage).newNodeId, 1);

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

                    List<String> newFilesList = new ArrayList<>(AppConfig.serventFiles.get(clientMessage.getOriginalSenderInfo().getId()));

                    for(String fileName : ((UpdateSystemMessage) clientMessage).removedFiles) {
                        newFilesList.remove(fileName);
                    }

                    AppConfig.serventFiles.put(clientMessage.getOriginalSenderInfo().getId(), newFilesList);

                    ((UpdateSystemMessage) clientMessage).removedFiles = new ArrayList<>();

                    int prevFinishedRequest = ((SuzukiMutex) mutex).finishedRequests.get(clientMessage.getOriginalSenderInfo().getId());
                    ((SuzukiMutex) mutex).finishedRequests.set(clientMessage.getOriginalSenderInfo().getId(), prevFinishedRequest + 1);
                } else if (((UpdateSystemMessage) clientMessage).serventThatFailed != null) {

                    ServentInfo serventThatFailed = ((UpdateSystemMessage) clientMessage).serventThatFailed;

                    AppConfig.serventInfoList.remove(serventThatFailed);

                    AppConfig.reorganizeArchitecture();

                    int prevFinishedRequest = ((SuzukiMutex) mutex).finishedRequests.get(clientMessage.getOriginalSenderInfo().getId());
                    ((SuzukiMutex) mutex).finishedRequests.set(clientMessage.getOriginalSenderInfo().getId(), prevFinishedRequest + 1);

                    RemoveNodeToBootstrapMessage message = new RemoveNodeToBootstrapMessage(AppConfig.myServentInfo, AppConfig.getBootstrapNode());
                    message.removedNode = serventThatFailed;
                    MessageUtil.sendMessage(message);
                }

                SystemUpdatedMessage message = new SystemUpdatedMessage(AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo());

                if(newNodeMessageId != -1) {
                    message.newNodeMessageId = newNodeMessageId;
                }

                MessageUtil.sendMessage(message);
            }
        } else {
            AppConfig.timestampedErrorPrint("Got message that is not of type Update System");
        }
    }
}
