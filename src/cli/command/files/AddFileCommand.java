package cli.command.files;

import app.AppConfig;
import app.ServentInfo;
import cli.command.CLICommand;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;
import servent.message.Message;
import servent.message.file.CreateBackupMessage;
import servent.message.file.PullFileMessage;
import servent.message.update.UpdateSystemMessage;
import servent.message.util.MessageUtil;

import java.io.File;
import java.io.FileInputStream;
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

                    UpdateSystemMessage message = new UpdateSystemMessage(AppConfig.myServentInfo, AppConfig.myServentInfo);
                    message.newFiles = newFiles;

                    for(ServentInfo s : AppConfig.serventInfoList) {
                        if (s != AppConfig.myServentInfo) {
                            message = (UpdateSystemMessage) message.changeReceiver(s.getId());
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

                    // posalji mom prethodnom cvoru da treba da drzi backup tog fajla
                    CreateBackupMessage message1 = new CreateBackupMessage(AppConfig.myServentInfo, AppConfig.previousNode(AppConfig.myServentInfo));
                    FileInputStream fileInputStream = new FileInputStream("directory" + AppConfig.myServentInfo.getId() + "\\" + fileName);
                    byte[] fileContent = fileInputStream.readAllBytes();
                    message1.backupFiles.put(fileName, fileContent);
                    MessageUtil.sendMessage(message1);

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
                try {
                    mutex.lock();
                    List<String> newFiles = new ArrayList<>();
                    for (File file : Objects.requireNonNull(new File(sourcePath.toString()).listFiles())) {
                        try {

                            Files.copy(Path.of(sourcePath + "\\" + file.getName()), Path.of("directory" + AppConfig.myServentInfo.getId() + "\\" + file.getName()));
                            AppConfig.timestampedStandardPrint("Successfully copied " + file.getName());
                            newFiles.add(file.getName());

                            List<String> newList = new ArrayList<>(AppConfig.serventFiles.get(AppConfig.myServentInfo.getId()));
                            newList.add(file.getName());
                            AppConfig.serventFiles.put(AppConfig.myServentInfo.getId(), newList);

                        } catch (FileAlreadyExistsException e) {
                            AppConfig.timestampedErrorPrint("File " + file.getName() + "from " + fileName + "folder already exists!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                    UpdateSystemMessage message = new UpdateSystemMessage(AppConfig.myServentInfo, AppConfig.myServentInfo);
                    message.newFiles = newFiles;

                    for (ServentInfo s : AppConfig.serventInfoList) {
                        if (s != AppConfig.myServentInfo) {
                            message = (UpdateSystemMessage) message.changeReceiver(s.getId());
                            MessageUtil.sendMessage(message);
                        }
                    }

                    while (((SuzukiMutex) mutex).systemUpdatedMessagesReceived.get() != AppConfig.serventInfoList.size() - 1) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    ((SuzukiMutex) mutex).systemUpdatedMessagesReceived.set(0);

                    // posalji mom prethodnom cvoru da treba da drzi backup tog fajla, ukoliko ja padnem on nek uzme taj backup i nek ga prebaci kod sebe
                    CreateBackupMessage message1 = new CreateBackupMessage(AppConfig.myServentInfo, AppConfig.previousNode(AppConfig.myServentInfo));
                    for(String newFile : newFiles) {
                        FileInputStream fileInputStream = new FileInputStream("directory" + AppConfig.myServentInfo.getId() + "\\" + newFile);
                        byte[] fileContent = fileInputStream.readAllBytes();
                        message1.backupFiles.put(newFile, fileContent);
                    }
                    MessageUtil.sendMessage(message1);

                    mutex.unlock();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                AppConfig.timestampedErrorPrint(fileName + " folder does not exist in filesToAdd folder");
            }
        }
    }
}
