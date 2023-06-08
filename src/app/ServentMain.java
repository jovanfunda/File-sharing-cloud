package app;

import java.util.ArrayList;

import app.buddySystem.BuddySystem;
import cli.CLIParser;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;
import servent.SimpleServentListener;

/**
 * Describes the procedure for starting a single Servent
 *
 * @author bmilojkovic
 */
public class ServentMain {

	public static void main(String[] args) {

		if (args.length != 2) {
			AppConfig.timestampedErrorPrint("Please provide ip address and port of this servent.");
		}

		String ipAddress = args[0];
		int portNumber = -1;
		
		try {
			portNumber = Integer.parseInt(args[1]);
			
			if (portNumber < 1000 || portNumber > 2000) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			AppConfig.timestampedErrorPrint("Port number should be in range 1000-2000. Exiting...");
			System.exit(0);
		}
		
		AppConfig.timestampedStandardPrint("Starting servent on port " + portNumber);

		AppConfig.myServentInfo = new ServentInfo(ipAddress, -1, portNumber, new ArrayList<>());

		SuzukiMutex mutex = new SuzukiMutex();
		
		SimpleServentListener simpleListener = new SimpleServentListener(portNumber, mutex);
		Thread listenerThread = new Thread(simpleListener);
		listenerThread.start();

		CLIParser cliParser = new CLIParser(simpleListener, mutex.buddySystem, mutex);
		Thread cliThread = new Thread(cliParser);
		cliThread.start();
		
	}
}
