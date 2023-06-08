package servent.handler.buddySystem;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;

public class PongHandler implements MessageHandler {

    public Message clientMessage;

    public PongHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        AppConfig.gotPong = true;
    }
}
