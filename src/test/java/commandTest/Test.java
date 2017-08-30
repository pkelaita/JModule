package commandTest;

import java.util.ArrayList;

import com.jModule.def.Command;

public class Test {
	public static void main(String[] args) {

		// set up logic and parameters
		TestLogic tl = new TestLogic();
		ArrayList<String> params = new ArrayList<>();
		params.add("arg1");
		params.add("arg2");
		params.add("arg3");
		tl.setParams(params);

		// set up command
		Command c = new Command("testName", "test description", "testReference", tl);

		System.out.println("Name: " + c.getName());
		System.out.println("Description: " + c.getDescription());
		System.out.println(c.getUsage());

		// execute command, then print command's return
		System.out.println("\nExecution:");

		String[] arguments = { "A", "B", "C", };
		String returnStr = c.execute(arguments).toString();

		System.out.println("\nReturns: " + returnStr);

	}
}
