package commandTest;

import com.jModule.def.CommandLogic;

public class TestLogic extends CommandLogic {

	@Override
	public void runCommand(String[] args) {
		
		// print paramenters and given arguments
		int paramNum = super.getParams().size();
		for (int i = 0; i < paramNum; i++) {
			System.out.println(super.getParams().get(i) + ": " + args[i]);
		}
		
		// return all given arguments in a string
		String str = "";
		for (String arg : args) {
			str += arg;
		}
		System.out.println(str);
	}

}
