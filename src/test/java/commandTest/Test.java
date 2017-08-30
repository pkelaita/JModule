package commandTest;

import java.util.ArrayList;

import com.jModule.def.Command;
import com.jModule.exec.Module;

public class Test {
	public static void main(String[] args) {

		// set up logic and parameters
		TestLogic logic = new TestLogic();
		ArrayList<String> params = new ArrayList<>();
		params.add("arg1");
		params.add("arg2");
		params.add("arg3");
		logic.setParams(params);

		// set up command
		Command c1 = new Command("Command 1", "this is a command", logic);

		System.out.println("Name: " + c1.getName());
		System.out.println("Description: " + c1.getDescription());
		System.out.println(c1.getUsage());

		// execute command, then print command's return
		System.out.println("\nExecution:");

		String[] arguments = { "A", "B", "C", };
		String returnStr = c1.execute(arguments).toString();

		System.out.println("\nReturns: " + returnStr);
		
		// set up module with multiple commands
		Command c2 = new Command("Command 2", "this is another command", logic);
		Module module = new Module("Home");
		module.addCommand(c1);
		module.addCommand(c2);
		
		System.out.println("\n\nModule help message:\n");
		System.out.println(module.getHelpMessage());
		
		System.out.println();
		System.out.println("Module prompt: " + module.getPrompt());
	}
}
