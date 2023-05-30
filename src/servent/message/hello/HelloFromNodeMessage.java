package servent.message.hello;

import app.AppConfig;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;
import java.util.List;
import java.util.Map;

public class HelloFromNodeMessage extends BasicMessage {

    private final List<ServentInfo> activeServents;

    public final Map<Integer, List<String>> serventFiles;


    public HelloFromNodeMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.HELLO_FROM_NODE, originalSenderInfo, receiverInfo);
        this.activeServents = AppConfig.getServentInfoList();
        this.serventFiles = AppConfig.serventFiles;
    }

    public List<ServentInfo> getActiveServents() {
        return activeServents;
    }
}
