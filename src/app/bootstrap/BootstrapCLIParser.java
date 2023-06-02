package app.bootstrap;

import java.util.Scanner;

public class BootstrapCLIParser implements Runnable {

    private volatile boolean working = true;

    private final BootstrapStopCommand stopCommand;

    public BootstrapCLIParser(BootstrapListener listener) {
        this.stopCommand = new BootstrapStopCommand(this, listener);
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);

        while (working) {
            String commandLine = sc.nextLine();

            if(commandLine.equals("]")) {
                stopCommand.execute();
            } else {
                System.err.println("Unknown command: " + commandLine);
            }
        }

        sc.close();
    }

    public void stop() {
        this.working = false;
    }
}
