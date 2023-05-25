package app.bootstrap;

public class BootstrapStopCommand {

    private BootstrapCLIParser parser;
    private BootstrapListener listener;

    public BootstrapStopCommand(BootstrapCLIParser parser, BootstrapListener listener) {
        this.parser = parser;
        this.listener = listener;
    }

    public void execute() {
        System.out.println("Stopping...");
        parser.stop();
        listener.stop();
    }
}
