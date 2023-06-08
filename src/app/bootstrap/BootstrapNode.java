package app.bootstrap;

import app.Constants;
import app.ServentInfo;

import java.util.ArrayList;
import java.util.List;

public class BootstrapNode {

    public static List<ServentInfo> activeNodes = new ArrayList<>();

    public static ServentInfo bootstrapInfo;

    public static void main(String[] args) {

        Constants.appConfig();

        String ipAddress = Constants.bootstrapIP;
        int portNumber = Constants.bootstrapPort;

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
