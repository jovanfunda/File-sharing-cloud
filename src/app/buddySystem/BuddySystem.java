package app.buddySystem;

import app.AppConfig;
import app.Cancellable;
import app.Constants;
import app.ServentInfo;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;
import servent.message.Message;
import servent.message.buddySystem.IsHeOKMessage;
import servent.message.buddySystem.PingMessage;
import servent.message.update.UpdateSystemMessage;
import servent.message.util.MessageUtil;

public class BuddySystem implements Runnable, Cancellable {

    private volatile boolean working = true;
    private DistributedMutex mutex;

    public BuddySystem(DistributedMutex mutex) {
        this.mutex = mutex;
    }

    @Override
    public void run() {

        while(working) {

            // Sistem ce redovno proveravati da li ima vise od jednog cvora u sistemu
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            while(AppConfig.serventInfoList.size() > 1 && working) {

                // Nema potrebe odmah slati poruku ukoliko smo videli da sistem radi
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                ServentInfo nextNode = AppConfig.nextNode(AppConfig.myServentInfo);
                MessageUtil.sendMessage(new PingMessage(AppConfig.myServentInfo, nextNode));

                try {
                    Thread.sleep(Constants.lower_limit);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(AppConfig.gotPong || !working) {
                    AppConfig.gotPong = false;
                    break;
                } else {
                    if(AppConfig.serventInfoList.size() >= 3) {
                        MessageUtil.sendMessage(new IsHeOKMessage(AppConfig.myServentInfo, AppConfig.nextNode(nextNode)));
                    }
                    try {
                        Thread.sleep(Constants.upper_limit);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if(AppConfig.gotPong || !working) {
                        AppConfig.gotPong = false;
                        break;
                    } else {
                        // cvor ne radi, radimo reorganizaciju sistema
                        AppConfig.timestampedErrorPrint("BuddySystem, cvor " + nextNode + " je prestao sa radom");

                        AppConfig.serventInfoList.remove(nextNode);

                        mutex.lock();

                        AppConfig.reorganizeArchitecture();

                        for(ServentInfo servent : AppConfig.serventInfoList) {
                            Message message = new UpdateSystemMessage(AppConfig.myServentInfo, servent);
                            if(servent != AppConfig.myServentInfo) {
                                message = message.changeReceiver(servent.getId());
                                ((UpdateSystemMessage) message).serventThatFailed = nextNode;
                                MessageUtil.sendMessage(message);
                            }
                        }

                        while(((SuzukiMutex) mutex).systemUpdatedMessagesReceived.get() != AppConfig.serventInfoList.size()-1) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        ((SuzukiMutex) mutex).systemUpdatedMessagesReceived.set(0);

                        mutex.unlock();
                    }
                }
            }
        }
    }

    @Override
    public void stop() {
        this.working = false;
    }
}
