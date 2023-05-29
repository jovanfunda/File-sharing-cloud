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

		StringBuilder neighbors = new StringBuilder();
		for (Integer neighbor : AppConfig.myServentInfo.getNeighbors()) {
			neighbors.append(neighbor).append(" ");
		}
		AppConfig.timestampedStandardPrint("Neighbors: " + neighbors);

		AppConfig.timestampedStandardPrint("Token: " + ((SuzukiMutex) mutex).hasToken());

		AppConfig.timestampedStandardPrint(((SuzukiMutex) mutex).finishedRequests + "");
		AppConfig.timestampedStandardPrint(((SuzukiMutex) mutex).requestsReceived + "");

	}

}
