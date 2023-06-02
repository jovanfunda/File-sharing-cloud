package mutex;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.mutex.RequestTokenMessage;
import servent.message.mutex.TokenMessage;
import servent.message.util.MessageUtil;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SuzukiMutex implements DistributedMutex {

    private volatile boolean haveToken = false;
    public volatile boolean usingToken = false;

    public ServentInfo nodeWithInfo;

    public ServentInfo newNodeWaiting;

    public Queue<ServentInfo> serventsWaiting = new ArrayBlockingQueue<>(20);

    public List<Integer> finishedRequests = new CopyOnWriteArrayList<>();

    public AtomicInteger systemUpdatedMessagesReceived = new AtomicInteger(0);

    public List<Integer> requestsReceived;

    public static final AtomicInteger sequenceNumber = new AtomicInteger(0);

    public SuzukiMutex() {
        requestsReceived = new CopyOnWriteArrayList<>();
    }

    @Override
    public void lock() {

        AppConfig.timestampedStandardPrint("Hocu da lockujem!");

        usingToken = true;

        if(!haveToken) {
            int num = -1;

            // Jos uvek nismo prikljuceni u arhitekturu i nemamo listu cvorova
            if(requestsReceived.size() == 0 && finishedRequests.size() == 0) {
                MessageUtil.sendMessage(new RequestTokenMessage(AppConfig.myServentInfo, nodeWithInfo, num));

            } else {
                num = sequenceNumber.addAndGet(1);

//                Message requestMessage = new RequestTokenMessage(AppConfig.myServentInfo, AppConfig.myServentInfo, num);
                for (ServentInfo s : AppConfig.getServentInfoList()) {
                    if(s != AppConfig.myServentInfo) {
//                        requestMessage = requestMessage.changeReceiver(s.getId());
                        MessageUtil.sendMessage(new RequestTokenMessage(AppConfig.myServentInfo, s, num));
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
        finishedRequests.set(AppConfig.myServentInfo.getId(), requestsReceived.get(AppConfig.myServentInfo.getId()));

        // ukoliko postoji novi node, bolje posalji njemu token poruku ovde
        if(newNodeWaiting != null) {
            AppConfig.timestampedStandardPrint("Token saljem novom nodu");
            haveToken = false;
            MessageUtil.sendMessage(new TokenMessage(AppConfig.myServentInfo, newNodeWaiting, serventsWaiting));
            newNodeWaiting = null;

        } else {

            for (int i = 0; i < requestsReceived.size(); i++) {
                if (finishedRequests.get(i) + 1 == requestsReceived.get(i)) {
                    if (!serventsWaiting.contains(AppConfig.getInfoById(i))) {
                        serventsWaiting.add(AppConfig.getInfoById(i));
                    }
                }
            }

            if (serventsWaiting.size() != 0) {
                AppConfig.timestampedStandardPrint("Ovi serventi cekaju, redom:");
                for (ServentInfo waitingServent : serventsWaiting) {
                    AppConfig.timestampedStandardPrint("" + waitingServent);
                }
                haveToken = false;
                MessageUtil.sendMessage(new TokenMessage(AppConfig.myServentInfo, serventsWaiting.poll(), serventsWaiting));
            }
        }

        AppConfig.timestampedStandardPrint("Unlocked!");
    }

    public boolean hasToken() {
        return haveToken;
    }

    public void setTokenActive(boolean active) {
        haveToken = active;
    }

    public void setNodeWithInfo(ServentInfo s) {
        this.nodeWithInfo = s;
    }
}
