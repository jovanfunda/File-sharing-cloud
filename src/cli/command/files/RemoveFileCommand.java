package cli.command.files;

import app.AppConfig;
import app.ServentInfo;
import cli.command.CLICommand;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;
import servent.message.update.UpdateSystemMessage;
import servent.message.util.MessageUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RemoveFileCommand implements CLICommand {

    private DistributedMutex mutex;

    public RemoveFileCommand(DistributedMutex mutex) {
        this.mutex = mutex;
    }

    @Override
    public String commandName() {
        return "remove";
    }

    @Override
    public void execute(String args) {
        String fileName = args;

        boolean found = false;

        for(String file: AppConfig.serventFiles.get(AppConfig.myServentInfo.getId())) {
            if(fileName.equals(file)) {
                found = true;
                break;
            }
        }

        if(found) {
            mutex.lock();
            List<String> removedFiles = new ArrayList<>();

            try {
                Files.delete(Path.of("directory" + AppConfig.myServentInfo.getId() + "\\" + fileName));
                removedFiles.add(fileName);
            } catch (IOException e) {
                AppConfig.timestampedErrorPrint("File with name " + fileName + " does not exist.");
            }

            List<String> newFiles = new ArrayList<>(AppConfig.serventFiles.get(AppConfig.myServentInfo.getId()));
            newFiles.remove(fileName);
            AppConfig.serventFiles.put(AppConfig.myServentInfo.getId(), newFiles);


            for(ServentInfo s : AppConfig.serventInfoList) {
                if (s != AppConfig.myServentInfo) {
                    UpdateSystemMessage message = new UpdateSystemMessage(AppConfig.myServentInfo, AppConfig.getInfoById(s.getId()));
                    message.removedFiles = removedFiles;
                    MessageUtil.sendMessage(message);
                }
            }

            while(((SuzukiMutex) mutex).systemUpdatedMessagesReceived.get() != AppConfig.serventInfoList.size() - 1) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            AppConfig.timestampedStandardPrint("File " + fileName + " successfully removed");

            ((SuzukiMutex) mutex).systemUpdatedMessagesReceived.set(0);
            mutex.unlock();

        } else {
            AppConfig.timestampedErrorPrint("File " + fileName + " not found on this servent, try info to see where is it stored");
        }
    }
}
