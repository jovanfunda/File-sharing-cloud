package servent.message.file;

import app.AppConfig;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.Message;
import servent.message.MessageType;

public class PullFileMessage extends BasicMessage {

    public String fileName;

    public PullFileMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, String fileName) {
        super(MessageType.PULL_FILE, originalSenderInfo, receiverInfo);
        this.fileName = fileName;
    }

    public PullFileMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, String fileName, int messageId) {
        super(MessageType.PULL_FILE, originalSenderInfo, receiverInfo, messageId);
        this.fileName = fileName;
    }

    @Override
    public Message changeReceiver(Integer newReceiverId) {
        ServentInfo newReceiverInfo = AppConfig.getInfoById(newReceiverId);

        return new PullFileMessage(getOriginalSenderInfo(),
                newReceiverInfo, fileName, getMessageId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PullFileMessage other) {

            boolean messageId = getMessageId() == other.getMessageId();
            boolean id = getOriginalSenderInfo().getId() == other.getOriginalSenderInfo().getId();
            boolean port = getOriginalSenderInfo().getListenerPort() == other.getOriginalSenderInfo().getListenerPort();
            boolean serventValue = getReceiverInfo().equals(other.getReceiverInfo());

            return messageId && id && port && serventValue;

        }

        return false;
    }
}
