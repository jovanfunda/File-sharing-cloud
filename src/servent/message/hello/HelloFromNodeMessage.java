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

    public final List<Integer> requestsList;


    public HelloFromNodeMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, List<Integer> requestsList) {
        super(MessageType.HELLO_FROM_NODE, originalSenderInfo, receiverInfo);
        this.activeServents = AppConfig.getServentInfoList();
        this.serventFiles = AppConfig.serventFiles;
        this.requestsList = requestsList;
    }

    public List<ServentInfo> getActiveServents() {
        return activeServents;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HelloFromNodeMessage other) {

            boolean messageId = getMessageId() == other.getMessageId();
            boolean id = getOriginalSenderInfo().getId() == other.getOriginalSenderInfo().getId();
            boolean port = getOriginalSenderInfo().getListenerPort() == other.getOriginalSenderInfo().getListenerPort();
            boolean serventValue = getReceiverInfo().equals(other.getReceiverInfo());

            return messageId && id && port && serventValue;

        }

        return false;
    }
}
