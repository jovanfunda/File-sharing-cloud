package servent.handler.bootstrap;

import app.bootstrap.BootstrapNode;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.hello.RemoveNodeToBootstrapMessage;

public class RemoveNodeToBootstrapHandler implements MessageHandler {

    private final Message clientMessage;

    public RemoveNodeToBootstrapHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        BootstrapNode.activeNodes.remove(((RemoveNodeToBootstrapMessage) clientMessage).removedNode);
    }
}
