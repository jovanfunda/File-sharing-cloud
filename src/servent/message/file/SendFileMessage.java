package servent.message.file;

import app.AppConfig;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.update.SystemUpdatedMessage;

import java.util.Objects;


public class SendFileMessage extends BasicMessage {

    public byte[] fileInputStream;
    public String fileName;

    public SendFileMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, byte[] fileInputStream, String fileName) {
        super(MessageType.SEND_FILE, originalSenderInfo, receiverInfo);
        this.fileInputStream = fileInputStream;
        this.fileName = fileName;
    }

    public SendFileMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, byte[] fileInputStream, String fileName, int messageId) {
        super(MessageType.SEND_FILE, originalSenderInfo, receiverInfo, "", messageId);
        this.fileInputStream = fileInputStream;
        this.fileName = fileName;
    }

    @Override
    public Message changeReceiver(Integer newReceiverId) {
        ServentInfo newReceiverInfo = AppConfig.getInfoById(newReceiverId);

        return new SendFileMessage(getOriginalSenderInfo(),
                newReceiverInfo, fileInputStream, fileName, getMessageId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SendFileMessage other) {

            boolean messageId = getMessageId() == other.getMessageId();
            boolean id = getOriginalSenderInfo().getId() == other.getOriginalSenderInfo().getId();
            boolean port = getOriginalSenderInfo().getListenerPort() == other.getOriginalSenderInfo().getListenerPort();
            boolean serventValue = getReceiverInfo().equals(other.getReceiverInfo());

            return messageId && id && port && serventValue;

        }

        return false;
    }
}
