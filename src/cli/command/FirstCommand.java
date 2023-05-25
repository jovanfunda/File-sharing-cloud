package cli.command;

import app.AppConfig;
import servent.message.HelloToBootstrapMessage;
import servent.message.util.MessageUtil;

public class FirstCommand implements CLICommand {

    @Override
    public String commandName() {
        return "first";
    }

    @Override
    public void execute(String args) {
        MessageUtil.sendMessage(new HelloToBootstrapMessage(AppConfig.myServentInfo));
    }
}
