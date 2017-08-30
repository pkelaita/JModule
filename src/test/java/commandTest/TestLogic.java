package commandTest;

import com.simpleCLI.def.CommandLogic;

public class TestLogic extends CommandLogic {

	@Override
	public void runCommand(String[] args) {
		int paramNum = super.getParams().size();
		for (int i = 0; i < paramNum; i++) {
			System.out.println(super.getParams().get(i) + ":" + args[i]);
		}
	}

}
