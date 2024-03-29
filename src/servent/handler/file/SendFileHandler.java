package servent.handler.file;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.file.PullFileMessage;
import servent.message.file.SendFileMessage;
import servent.message.update.UpdateSystemMessage;
import servent.message.util.MessageUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SendFileHandler implements MessageHandler {

    private Message clientMessage;

    public SendFileHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {

        if (clientMessage.getReceiverInfo().getId() == AppConfig.myServentInfo.getId()) {
            FileOutputStream fileOutputStream = null;
            try {

                File directory = new File("directory" + AppConfig.myServentInfo.getId());
                if (!directory.exists())
                    directory.mkdir();

                fileOutputStream = new FileOutputStream("directory" + AppConfig.myServentInfo.getId() + "\\" + ((SendFileMessage) clientMessage).fileName);

                fileOutputStream.write(((SendFileMessage) clientMessage).fileInputStream);
                fileOutputStream.close();

                System.out.println("File " + ((SendFileMessage) clientMessage).fileName + " copied successfully.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
