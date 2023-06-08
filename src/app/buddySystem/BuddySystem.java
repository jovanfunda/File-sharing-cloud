package app.buddySystem;

import app.AppConfig;
import app.Cancellable;
import app.ServentInfo;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;
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

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            while(AppConfig.serventInfoList.size() > 1 && working) {

                ServentInfo nextNode = AppConfig.nextNode();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                MessageUtil.sendMessage(new PingMessage(AppConfig.myServentInfo, AppConfig.nextNode()));

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(AppConfig.gotPong || !working) {
                    AppConfig.gotPong = false;
                    break;
                } else {
                    // saljemo poruku njegovom drugom buddy cvoru jel sve u redu
                    MessageUtil.sendMessage(new PingMessage(AppConfig.myServentInfo, nextNode));
                    try {
                        Thread.sleep(8000);
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
                            if(servent != AppConfig.myServentInfo) {
                                UpdateSystemMessage message = new UpdateSystemMessage(AppConfig.myServentInfo, servent);
                                message.serventThatFailed = nextNode;
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
