package servent.handler.buddySystem;

import servent.handler.MessageHandler;
import servent.message.Message;

public class OKHandler implements MessageHandler {

    public Message clientMessage;

    public OKHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        IsHeOKHandler.hisOKState = true;
    }
}
