package cli.command.files;

import app.AppConfig;
import cli.command.CLICommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class AddFileCommand implements CLICommand {

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
                    Files.copy(sourcePath, targetPath);
                    AppConfig.timestampedStandardPrint("File " + fileName + " copied to directory" + AppConfig.myServentInfo.getId());
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
                for(File file: Objects.requireNonNull(new File(sourcePath.toString()).listFiles())) {
                    try {
                        Files.copy(Path.of(sourcePath + "\\" + file.getName()),Path.of("directory" + AppConfig.myServentInfo.getId() + "\\" + file.getName()));
                        AppConfig.timestampedStandardPrint("Succesfully copied " + file.getName());
                    } catch (FileAlreadyExistsException e) {
                        AppConfig.timestampedErrorPrint("File " + fileName + " already exists!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                AppConfig.timestampedErrorPrint(fileName + " folder does not exist in filesToAdd folder");
            }
        }
    }
}
