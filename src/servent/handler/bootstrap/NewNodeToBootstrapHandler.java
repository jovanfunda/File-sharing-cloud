package servent.handler.bootstrap;

import app.bootstrap.BootstrapNode;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.hello.NewNodeToBootstrapMessage;

public class NewNodeToBootstrapHandler implements MessageHandler {

    private final Message clientMessage;

    public NewNodeToBootstrapHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        BootstrapNode.activeNodes.add(((NewNodeToBootstrapMessage) clientMessage).newNode);
    }
}
