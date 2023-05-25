package servent.message;

import app.AppConfig;
import app.ServentInfo;

import java.util.List;

public class HelloFromNodeMessage extends BasicMessage {

    private List<ServentInfo> activeServents;

    public HelloFromNodeMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.HELLO_FROM_NODE, originalSenderInfo, receiverInfo);
        this.activeServents = AppConfig.getServentInfoList();
    }

    public List<ServentInfo> getActiveServents() {
        return activeServents;
    }
}
