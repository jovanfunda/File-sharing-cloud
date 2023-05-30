package servent.message.file;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;


public class SendFileMessage extends BasicMessage {

    public byte[] fileInputStream;
    public String fileName;

    public SendFileMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, byte[] fileInputStream, String fileName) {
        super(MessageType.SEND_FILE, originalSenderInfo, receiverInfo);
        this.fileInputStream = fileInputStream;
        this.fileName = fileName;
    }
}
