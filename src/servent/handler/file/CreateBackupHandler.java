package servent.handler.file;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.file.CreateBackupMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class CreateBackupHandler implements MessageHandler {

    private Message clientMessage;

    public CreateBackupHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }


    @Override
    public void run() {

        File directory = new File("Backup" + AppConfig.myServentInfo.getId());
        if (!directory.exists())
            directory.mkdir();

        for(Map.Entry<String, byte[]> file : ((CreateBackupMessage) clientMessage).backupFiles.entrySet()) {
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream("Backup" + AppConfig.myServentInfo.getId() + "\\" + file.getKey());

                fileOutputStream.write(file.getValue());
                fileOutputStream.close();

                System.out.println("Created backup for file " + file.getKey() + ".");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
