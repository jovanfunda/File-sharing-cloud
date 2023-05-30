package mutex;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.mutex.RequestTokenMessage;
import servent.message.util.MessageUtil;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SuzukiMutex implements DistributedMutex {

    private volatile boolean haveToken = false;
    public volatile boolean usingToken = false;

    public Queue<ServentInfo> serventsWaiting = new ArrayBlockingQueue<>(20);

    public List<Integer> finishedRequests = new CopyOnWriteArrayList<>();

    private ServentInfo infoNode;

    public AtomicInteger systemUpdatedMessagesReceived = new AtomicInteger(0);

    public List<Integer> requestsReceived;

    private static final AtomicInteger sequenceNumber = new AtomicInteger(0);

    public SuzukiMutex() {
        requestsReceived = new CopyOnWriteArrayList<>();
    }

    @Override
    public void lock() {

        AppConfig.timestampedStandardPrint("Hocu da lockujem!");

        usingToken = true;

        if(!haveToken) {
            int num = sequenceNumber.addAndGet(1);

            // Jos uvek nismo prikljuceni u arhitekturu i nemamo listu cvorova
            if(AppConfig.getServentInfoList().size() == 0) {
                MessageUtil.sendMessage(new RequestTokenMessage(AppConfig.myServentInfo, infoNode, num));
            } else {

                Message requestMessage = new RequestTokenMessage(AppConfig.myServentInfo, AppConfig.myServentInfo, num);
                for (ServentInfo s : AppConfig.getServentInfoList()) {
                    if(s != AppConfig.myServentInfo) {
                        requestMessage = requestMessage.changeReceiver(s.getId());
                        MessageUtil.sendMessage(requestMessage);
                    }
                }
            }
        }

        while (!haveToken) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        AppConfig.timestampedStandardPrint("Uzeo sam lock!");
    }

    @Override
    public void unlock() {
        usingToken = false;
//        finishedRequests.set(AppConfig.myServentInfo.getId(), requestsReceived.get(AppConfig.myServentInfo.getId()));
//
//        for(int i = 0; i < requestsReceived.size(); i++) {
//            if(finishedRequests.get(i) + 1 == requestsReceived.get(i)) {
//                if(!serventsWaiting.contains(AppConfig.getInfoById(i))) {
//                    serventsWaiting.add(AppConfig.getInfoById(i));
//                }
//            }
//        }

        AppConfig.timestampedStandardPrint("Unlocked!");
    }

    public boolean hasToken() {
        return haveToken;
    }

    public void setTokenActive(boolean active) {
        haveToken = active;
    }

    public void setNodeWithInfo(ServentInfo infoNode) {
        this.infoNode = infoNode;
    }
}
