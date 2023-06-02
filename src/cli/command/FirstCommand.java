package cli.command;

import app.AppConfig;
import servent.message.hello.HelloToBootstrapMessage;
import servent.message.util.MessageUtil;

public class FirstCommand implements CLICommand {

    @Override
    public String commandName() {
        return "[";
    }

    @Override
    public void execute(String args) {
        MessageUtil.sendMessage(new HelloToBootstrapMessage(AppConfig.myServentInfo));
    }
}
