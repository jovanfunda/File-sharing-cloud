package cli.command;

import app.AppConfig;
import mutex.DistributedMutex;
import mutex.SuzukiMutex;

public class InfoCommand implements CLICommand {

	private DistributedMutex mutex;

	public InfoCommand(DistributedMutex mutex) {
		this.mutex = mutex;
	}

	@Override
	public String commandName() {
		return "info";
	}

	@Override
	public void execute(String args) {
		AppConfig.timestampedStandardPrint("My info: " + AppConfig.myServentInfo);
		AppConfig.timestampedStandardPrint("Neighbors:");
		AppConfig.timestampedStandardPrint("Token: " + ((SuzukiMutex) mutex).hasToken());
		String neighbors = "";
		for (Integer neighbor : AppConfig.myServentInfo.getNeighbors()) {
			neighbors += neighbor + " ";
		}
		
		AppConfig.timestampedStandardPrint(neighbors);
	}

}
