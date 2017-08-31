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
		
		// set up modules with multiple commands
		Command c1 = new Command("Command 1", "this is a command", logic);
		c1.addPossibleReference("cmd1");
		c1.addPossibleReference("c1");
		
		Command c2 = new Command("Command 2", "this is another command", logic);
		c2.addPossibleReference("cmd2");
		c2.addPossibleReference("c2");
		
		Module home = new Module("home");
		home.addCommand(c1);
		home.addCommand(c2);
		
		Module m2 = new Module("mod2");
		m2.addCommand(c1);
		m2.addCommand(c2);
		
		ConsoleClient client = new ConsoleClient("TestApp", home);
		client.addModule(m2);
		client.runConsole();
	}
}
