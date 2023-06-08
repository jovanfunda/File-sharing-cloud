package servent.handler.bootstrap;

import app.AppConfig;
import app.bootstrap.BootstrapNode;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.bootstrap.HelloFromBootstrapMessage;
import servent.message.util.MessageUtil;

import java.util.Random;

public class HelloToBootstrapHandler implements MessageHandler {

    private final Message clientMessage;

    public HelloToBootstrapHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        System.out.println("Poruka za ulaz od porta " + clientMessage.getOriginalSenderInfo().getListenerPort());

        if(BootstrapNode.activeNodes.isEmpty()) {
            MessageUtil.sendMessage(new HelloFromBootstrapMessage(clientMessage.getOriginalSenderInfo()));
        } else {
            MessageUtil.sendMessage(new HelloFromBootstrapMessage(clientMessage.getOriginalSenderInfo(), BootstrapNode.activeNodes.get(new Random().nextInt(BootstrapNode.activeNodes.size()))));
        }
    }
}
