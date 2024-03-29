package mutex;

import app.AppConfig;
import app.ServentInfo;
import app.buddySystem.BuddySystem;
import servent.message.mutex.FirstRequestTokenMessage;
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

    public AtomicInteger requestsTokenReceived = new AtomicInteger(0);

    public List<Integer> requestsReceived;

    public static final AtomicInteger sequenceNumber = new AtomicInteger(1);

    public BuddySystem buddySystem = new BuddySystem(this);

    public SuzukiMutex() {
        requestsReceived = new CopyOnWriteArrayList<>();
    }

    @Override
    public void lock() {

        AppConfig.timestampedStandardPrint("Hocu da lockujem!");

        usingToken = true;

        int num = -1;
        requestsTokenReceived.set(0);

        // Jos uvek nismo prikljuceni u arhitekturu i nemamo listu cvorova, i ako nismo prvi node, jer on nema nodeWithInfo
        if(requestsReceived.size() == 0 && finishedRequests.size() == 0 && AppConfig.serventInfoList.size() != 0) {
            MessageUtil.sendMessage(new FirstRequestTokenMessage(AppConfig.myServentInfo, nodeWithInfo));
        // Ako nismo prvi node, jer ne zelimo da povecam
        } else if (AppConfig.serventInfoList.size() != 0) {
            num = sequenceNumber.addAndGet(1);

            for (ServentInfo s : AppConfig.serventInfoList) {
                if(s != AppConfig.myServentInfo) {
                    MessageUtil.sendMessage(new RequestTokenMessage(AppConfig.myServentInfo, s, num));
                }
            }
        }

        while (!haveToken) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Specijalni slucaj, ukoliko smo dobili poruku od svih a niko nema token
            if(requestsTokenReceived.get() == AppConfig.serventInfoList.size()-1) {
                haveToken = true;
            }
        }

        AppConfig.timestampedStandardPrint("Uzeo sam lock!");
    }

    @Override
    public void unlock() {

        usingToken = false;
        finishedRequests.set(AppConfig.myServentInfo.getId(), requestsReceived.get(AppConfig.myServentInfo.getId()));

        finishedRequests.set(AppConfig.myServentInfo.getId(), finishedRequests.get(AppConfig.myServentInfo.getId()) + 1);
        requestsReceived.set(AppConfig.myServentInfo.getId(), requestsReceived.get(AppConfig.myServentInfo.getId()) + 1);

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
                        AppConfig.timestampedErrorPrint("Lock, ubacio sam " + AppConfig.getInfoById(i) + " u Q");
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
