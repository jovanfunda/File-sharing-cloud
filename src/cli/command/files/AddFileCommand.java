package cli.command.files;

import app.AppConfig;
import cli.command.CLICommand;

import java.io.File;
import java.io.IOException;
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
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            if(new File("filesToAdd\\" + fileName).exists()) {
                for(File file: Objects.requireNonNull(new File(sourcePath.toString()).listFiles())) {
                    try {
                        Path targetPath = Path.of("directory" + AppConfig.myServentInfo.getId() + "\\" + file.getName());
                        new File("directory" + AppConfig.myServentInfo.getId()+"\\"+file.getName()).createNewFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
