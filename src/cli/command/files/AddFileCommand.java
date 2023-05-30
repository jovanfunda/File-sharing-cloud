package cli.command.files;

import app.AppConfig;
import app.ServentInfo;
import cli.command.CLICommand;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;
import servent.message.update.UpdateSystemMessage;
import servent.message.util.MessageUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddFileCommand implements CLICommand {

    private DistributedMutex mutex;

    public AddFileCommand(DistributedMutex mutex) {
        this.mutex = mutex;
    }

    @Override
    public String commandName() {
        return "add";
    }

    @Override
    public void execute(String args) {
        String fileName = args;

        File directory = new File("directory" + AppConfig.myServentInfo.getId());
        if (!directory.exists())
            directory.mkdir();

        Path sourcePath = Path.of("filesToAdd\\" + fileName);

        if(fileName.endsWith(".txt")) {
            if(new File("filesToAdd\\" + fileName).exists()) {
                Path targetPath = Path.of("directory" + AppConfig.myServentInfo.getId() + "\\" + fileName);
                try {
                    mutex.lock();
                    List<String> newFiles = new ArrayList<>();

                    Files.copy(sourcePath, targetPath);
                    AppConfig.timestampedStandardPrint("File " + fileName + " copied to directory" + AppConfig.myServentInfo.getId());
                    newFiles.add(fileName);

                    List<String> oldList = AppConfig.serventFiles.get(AppConfig.myServentInfo.getId());
                    oldList.add(fileName);
                    AppConfig.serventFiles.put(AppConfig.myServentInfo.getId(), oldList);

                    for(ServentInfo s : AppConfig.serventInfoList) {
                        if (s != AppConfig.myServentInfo) {
                            UpdateSystemMessage message = new UpdateSystemMessage(AppConfig.myServentInfo, AppConfig.getInfoById(s.getId()));
                            message.newFiles = newFiles;
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

                    mutex.unlock();
                } catch (FileAlreadyExistsException e) {
                    AppConfig.timestampedErrorPrint("File " + fileName + " already exists!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                AppConfig.timestampedErrorPrint("File " + fileName + " does not exist in filesToAdd folder");
            }
        } else {
            if(new File("filesToAdd\\" + fileName).exists()) {
                mutex.lock();
                List<String> newFiles = new ArrayList<>();
                for(File file: Objects.requireNonNull(new File(sourcePath.toString()).listFiles())) {
                    try {

                        Files.copy(Path.of(sourcePath + "\\" + file.getName()),Path.of("directory" + AppConfig.myServentInfo.getId() + "\\" + file.getName()));
                        AppConfig.timestampedStandardPrint("Successfully copied " + file.getName());
                        newFiles.add(file.getName());

                        List<String> oldList = AppConfig.serventFiles.get(AppConfig.myServentInfo.getId());
                        oldList.add(file.getName());
                        AppConfig.serventFiles.put(AppConfig.myServentInfo.getId(), oldList);

                    } catch (FileAlreadyExistsException e) {
                        AppConfig.timestampedErrorPrint("File " + file.getName() + "from " + fileName + "folder already exists!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                for(ServentInfo s : AppConfig.serventInfoList) {
                    if(s != AppConfig.myServentInfo) {
                        UpdateSystemMessage message = new UpdateSystemMessage(AppConfig.myServentInfo, AppConfig.getInfoById(s.getId()));
                        message.newFiles = newFiles;
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
                ((SuzukiMutex) mutex).systemUpdatedMessagesReceived.set(0);

                mutex.unlock();
            } else {
                AppConfig.timestampedErrorPrint(fileName + " folder does not exist in filesToAdd folder");
            }
        }
    }
}
