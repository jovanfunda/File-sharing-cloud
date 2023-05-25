package app.bootstrap;

import app.ServentInfo;

import java.util.ArrayList;
import java.util.List;

// Bootstrap node je uvek fiksiran na localhost i na port 1000, kako bi svi ostali cvorovi znali gde da ga traze
public class BootstrapNode {

    public static List<ServentInfo> activeNodes = new ArrayList<>();

    public static ServentInfo bootstrapInfo;

    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Please provide ip address and port of this bootstrap node.");
        }

        String ipAddress = args[0];
        int portNumber = -1;

        try {
            portNumber = Integer.parseInt(args[1]);

            if (portNumber < 1000 || portNumber > 2000) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            System.err.println("Second argument should be int in range 1000-2000. Exiting...");
            System.exit(0);
        }

        System.out.println("Starting bootstrap node at port " + portNumber);

        bootstrapInfo = new ServentInfo(ipAddress, -1, portNumber, new ArrayList<>());

        BootstrapListener bootstrapListener = new BootstrapListener(portNumber);
        Thread listenerThread = new Thread(bootstrapListener);
        listenerThread.start();

        BootstrapCLIParser bootstrapCLIParser = new BootstrapCLIParser(bootstrapListener);
        Thread cliParserThread = new Thread(bootstrapCLIParser);
        cliParserThread.start();
    }
}
