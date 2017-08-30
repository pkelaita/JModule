package commandTest;

import java.util.ArrayList;

import com.simpleCLI.def.Command;

public class Test {
	public static void main(String[] args) {
		TestLogic tl = new TestLogic();
		
		ArrayList<String> params = new ArrayList<>();
		params.add("arg 1");
		params.add("arg 2");
		params.add("arg 3");
		tl.setParams(params);
		
		Command c = new Command("test", tl);
		
		String[] arguments = {"1", "2", "3" };
		c.execute(arguments);
	}
}
