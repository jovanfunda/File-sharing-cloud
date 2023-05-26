package mutex;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.mutex.RequestTokenMessage;
import servent.message.util.MessageUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SuzukiMutex implements DistributedMutex {

    private volatile boolean haveToken = false;
    public volatile boolean usingToken = false;

    private ServentInfo infoNode;

    public AtomicInteger messagesReceived = new AtomicInteger(0);

    public List<Integer> requestsReceived;

    private static AtomicInteger sequenceNumber = new AtomicInteger(0);

    public SuzukiMutex() {
        requestsReceived = new CopyOnWriteArrayList<>();
    }

    // imamo problem
    // token ce biti kod Serventa 1 i on i dalje nece da ima za komsiju broj 2 tkd ne moze da mu salje request za token
    // treba namestiti da su oni komsije i da su arhitektura sami za sebe

    @Override
    public void lock() {

        AppConfig.timestampedStandardPrint("Zapocinjem lock!");

        usingToken = true;

        if(!haveToken) {
            int num = sequenceNumber.addAndGet(1);

            // Jos uvek nismo prikljuceni u arhitekturu i nemamo listu cvorova
            if(AppConfig.getServentInfoList().size() == 0) {
                MessageUtil.sendMessage(new RequestTokenMessage(AppConfig.myServentInfo, infoNode, num));
            } else {

                Message requestMessage = new RequestTokenMessage(AppConfig.myServentInfo, AppConfig.myServentInfo, num);
                for (ServentInfo s : AppConfig.getServentInfoList()) {
                    requestMessage.changeReceiver(s.getId());
                    MessageUtil.sendMessage(requestMessage);
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

        AppConfig.timestampedStandardPrint("Zavrsio sam lock!");
    }

    @Override
    public void unlock() {
        usingToken = false;
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

    public ServentInfo getNodeWithInfo() {
        return infoNode;
    }

}
