package servent.handler.file;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.file.PullFileMessage;
import servent.message.file.SendFileMessage;
import servent.message.update.UpdateSystemMessage;
import servent.message.util.MessageUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PullFileHandler implements MessageHandler {

    private Message clientMessage;

    public PullFileHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {

        if (clientMessage.getReceiverInfo().getId() == AppConfig.myServentInfo.getId()) {
            SendFileMessage sendFileMessage = null;
            try {
                FileInputStream fileInputStream = new FileInputStream("directory" + AppConfig.myServentInfo.getId() + "\\" + ((PullFileMessage) clientMessage).fileName);

                byte[] fileContent = fileInputStream.readAllBytes();
                sendFileMessage = new SendFileMessage(AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo(), fileContent, ((PullFileMessage) clientMessage).fileName);
                fileInputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            MessageUtil.sendMessage(sendFileMessage);
        }
    }
}
