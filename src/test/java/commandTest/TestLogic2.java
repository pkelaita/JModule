package commandTest;

import com.jModule.def.CommandLogic;

public class TestLogic2 extends CommandLogic {

	private String answer;

	public void setCorrectAnswer(String answer) {
		this.answer = answer;
	}

	@Override
	public Object runCommand(String[] args) {
		while (true) {
			System.out.println("\nWho is the sexiest man alive?");
			if (System.console().readLine().equalsIgnoreCase(answer)) {
				System.out.println("Correct!");
				return null;
			} else {
				System.out.println("Wrong answer, try again");
				continue;
			}
		}
	}

}
