package commandTest;

import java.util.ArrayList;

import com.jModule.def.Command;
import com.jModule.exec.ConsoleClient;
import com.jModule.exec.Module;

public class ConsoleTest {
	public static void main(String[] args) {
		
		// set up logic and parameters
		TestLogic logic = new TestLogic();
		ArrayList<String> params = new ArrayList<>();
		params.add("arg1");
		params.add("arg2");
		params.add("arg3");
		logic.setParams(params);
		
		TestLogic2 logic2 = new TestLogic2();
		logic2.setCorrectAnswer("pierce kelaita");
		
		// set up modules with multiple commands
		Command c1 = new Command("Command 1", "this is a command", logic);
		c1.addReference("cmd1");
		c1.addReference("c1");
		
		Command c2 = new Command("Command 2", "this is another command", logic);
		c2.addReference("cmd2");
		c2.addReference("c2");
		
		Command c3 = new Command ("Command 3", "this command has no parameters", logic2);
		c3.addReference("cmd3");
		c3.addReference("c3");
		
		Module home = new Module("home");
		home.addCommand(c1);
		home.addCommand(c2);
		
		Module m2 = new Module("mod2");
		m2.addCommand(c1);
		m2.addCommand(c2);
		m2.addCommand(c3);
		
		// set up and run client
		ConsoleClient client = new ConsoleClient("TestApp", home);
		client.addModule(m2);
		client.runConsole();
	}
}
