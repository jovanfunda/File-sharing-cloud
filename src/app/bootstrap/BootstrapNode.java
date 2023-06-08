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

        System.out.println("Starting bootstrap node at port " + Constants.bootstrapPort);

        bootstrapInfo = new ServentInfo(Constants.bootstrapIP, -1, Constants.bootstrapPort, new ArrayList<>());

        BootstrapListener bootstrapListener = new BootstrapListener(Constants.bootstrapPort);
        Thread listenerThread = new Thread(bootstrapListener);
        listenerThread.start();

        BootstrapCLIParser bootstrapCLIParser = new BootstrapCLIParser(bootstrapListener);
        Thread cliParserThread = new Thread(bootstrapCLIParser);
        cliParserThread.start();
    }
}
