package cli.command.files;

import app.AppConfig;
import cli.command.CLICommand;
import servent.message.file.PullFileMessage;
import servent.message.util.MessageUtil;

public class PullFileCommand implements CLICommand {

    @Override
    public String commandName() {
        return "pull";
    }

    @Override
    public void execute(String args) {
        String fileName = args;

        boolean found = false;
        Integer serventWhereItIsStored = -1;

        for (Integer serventId : AppConfig.serventFiles.keySet()) {
            if(!found) {
                for (String fileInServent : AppConfig.serventFiles.get(serventId)) {
                    if(fileInServent.equals(fileName)) {
                        found = true;
                        serventWhereItIsStored = serventId;
                        break;
                    }
                }
            }
        }

        if(serventWhereItIsStored != -1) {
            AppConfig.timestampedStandardPrint("Fajl postoji na serventu sa ID " + serventWhereItIsStored);
        } else {
            AppConfig.timestampedErrorPrint("File is not found anywhere!");
        }

        MessageUtil.sendMessage(new PullFileMessage(AppConfig.myServentInfo, AppConfig.getInfoById(serventWhereItIsStored), fileName));

    }
}
