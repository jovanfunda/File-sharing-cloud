package servent.handler.buddySystem;

import servent.handler.MessageHandler;
import servent.message.Message;

public class HeIsNotOKHandler implements MessageHandler {

    public Message clientMessage;

    public HeIsNotOKHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {

    }
}
