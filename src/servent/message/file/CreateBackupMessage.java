package servent.message.file;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

import java.util.HashMap;
import java.util.Map;

public class CreateBackupMessage extends BasicMessage {

    public Map<String, byte[]> backupFiles = new HashMap<>();

    public CreateBackupMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(MessageType.CREATE_BACKUP, originalSenderInfo, receiverInfo);
    }
}
