package servent.handler.bootstrap;

import app.ServentInfo;
import app.bootstrap.BootstrapNode;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.bootstrap.HelloFromBootstrapMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayList;

public class HelloToBootstrapHandler implements MessageHandler {

    private final Message clientMessage;

    public HelloToBootstrapHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        System.out.println("Poruka za ulaz od porta " + clientMessage.getOriginalSenderInfo().getListenerPort());

        if(BootstrapNode.activeNodes.size() == 0) {
            MessageUtil.sendMessage(new HelloFromBootstrapMessage(clientMessage.getOriginalSenderInfo()));
            BootstrapNode.activeNodes.add(new ServentInfo("localhost", 0 ,1100, new ArrayList<>()));
        } else if(BootstrapNode.activeNodes.size() == 1) {
            MessageUtil.sendMessage(new HelloFromBootstrapMessage(clientMessage.getOriginalSenderInfo(), BootstrapNode.activeNodes.get(0)));
            BootstrapNode.activeNodes.add(new ServentInfo("", -1, 1, new ArrayList<>()));
        } else {
            MessageUtil.sendMessage(new HelloFromBootstrapMessage(clientMessage.getOriginalSenderInfo(), BootstrapNode.activeNodes.get(0)));
//            MessageUtil.sendMessage(new HelloFromBootstrapMessage(clientMessage.getOriginalSenderInfo(), BootstrapNode.activeNodes.get(new Random().nextInt(BootstrapNode.activeNodes.size()))));
            BootstrapNode.activeNodes.add(new ServentInfo("", -1, 1, new ArrayList<>()));
        }

    }
}
