package cli.command;

import app.AppConfig;
import app.buddySystem.BuddySystem;
import cli.CLIParser;
import servent.SimpleServentListener;

public class StopCommand implements CLICommand {

	private CLIParser parser;
	private SimpleServentListener listener;
	private BuddySystem buddySystem;

	public StopCommand(CLIParser parser, SimpleServentListener listener, BuddySystem buddySystem) {
		this.parser = parser;
		this.listener = listener;
		this.buddySystem = buddySystem;
	}
	
	@Override
	public String commandName() {
		return "]";
	}

	@Override
	public void execute(String args) {
		AppConfig.timestampedStandardPrint("Stopping...");
		parser.stop();
		listener.stop();
		buddySystem.stop();
	}

}
