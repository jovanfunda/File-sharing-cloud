package servent.message.file;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class PullFileMessage extends BasicMessage {

    public String fileName;

    public PullFileMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, String fileName) {
        super(MessageType.PULL_FILE, originalSenderInfo, receiverInfo);
        this.fileName = fileName;
    }
}
