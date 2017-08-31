import java.util.ArrayList;
import java.util.Random;

import com.jModule.def.Command;
import com.jModule.def.CommandLogic;
import com.jModule.exec.ConsoleClient;
import com.jModule.exec.Module;

/*
 * The classes in this file serve as an example of how to use the JModule API
 * Note that data referenced and changed by multiple commands is stored in its
 * own class, 'Performance.' This style is reccommended when writing commands
 * that reference shared data.
 */

public class ExampleApp {

	public static void main(String[] args) {

		// set up commands

		// 'add'
		ArrayList<String> addParams = new ArrayList<>();
		addParams.add("first number");
		addParams.add("second number");
		AddCmdLogic addLogic = new AddCmdLogic(); // set up logic with parameters
		addLogic.setParams(addParams); // add parameters to the command logic
		Command addCmd = new Command("add", "Adds 2 numbers together", addLogic); // set up command with logic

		// 'subtract'
		ArrayList<String> subtractParams = new ArrayList<>();
		subtractParams.add("first number");
		subtractParams.add("second number");
		SubtractCmdLogic subtractLogic = new SubtractCmdLogic(); // set up logic with paramters
		subtractLogic.setParams(subtractParams); // add parameters to the command logic
		Command subCmd = new Command("subtract", "Subtracts 2 numbers", subtractLogic); // set up command with logic
		subCmd.addReference("sub"); // add alternate reference to command

		// 'quizme'
		Command quizCmd = new Command("quizme", "Tests your knowledge of math", new QuizCmdLogic());
		// this command has no paramters
		quizCmd.addReference("qm");

		// 'info'
		Command infoCmd = new Command("info", "Tells you your quiz performance record", new InfoCmdLogic());
		// this command also has no parameters

		// set up modules - useful for grouping commands of similar type together
		Module math = new Module("math");
		math.addCommand(addCmd);
		math.addCommand(subCmd);

		Module quiz = new Module("quiz");
		quiz.addCommand(quizCmd);
		quiz.addCommand(infoCmd);

		// set up and run console application
		ConsoleClient client = new ConsoleClient("ExampleEducationApp", math); // 'math' is the home module, so it will
																				// come up when the program first runs
		client.addModule(quiz);
		client.runConsole();

	}
}

// below we have our logic classes - these define the actual logic of each
// command

class InfoCmdLogic extends CommandLogic {

	@Override
	public void runCommand(String[] args) {
		System.out.println("Correct answers: " + Performance.getCorrect()); // this references another class used to
																			// store data
		System.out.println("Incorrect answers: " + Performance.getIncorrect());
	}

}

class QuizCmdLogic extends CommandLogic {

	@Override
	public void runCommand(String[] args) {
		boolean isAdd = new Random().nextBoolean();
		String operation;
		if (isAdd) {
			operation = " + ";
		} else {
			operation = " - ";
		}

		int a = new Random().nextInt(10);
		int b = new Random().nextInt(10);
		System.out.println("What is " + a + operation + b + "? ");

		try {
			int answer;
			if (isAdd) {
				answer = a + b;
			} else {
				answer = a - b;
			}

			int result = Integer.parseInt(System.console().readLine());
			if (answer == result) {
				System.out.println("Correct\n");
				Performance.addCorrect();
			} else {
				System.out.println("Incorrect - correct answer was " + answer + ".\n");
				Performance.addIncorrect();
			}
		} catch (NumberFormatException nfe) {
			System.out.println("Invalid input!\n");
		}
	}

}

class AddCmdLogic extends CommandLogic {

	@Override
	public void runCommand(String[] args) {
		try {
			int a = Integer.parseInt(args[0]);
			int b = Integer.parseInt(args[1]);
			System.out.println("Sum: " + (a + b) + "\n");
		} catch (NumberFormatException nfe) {
			System.out.println("Invalid input!\n");
		}
	}

}

class SubtractCmdLogic extends CommandLogic {

	@Override
	public void runCommand(String[] args) {
		try {
			int a = Integer.parseInt(args[0]);
			int b = Integer.parseInt(args[1]);
			System.out.println("Difference: " + (a - b) + "\n");
		} catch (NumberFormatException nfe) {
			System.out.println("Invalid input!\n");
		}
	}

}

// the class 'performance' serves to store data that can be referenced between
// the logic of multiple commands. This can be done with any data type as long
// as they have getters and setters

class Performance {

	private static int correct = 0;
	private static int incorrect = 0;

	public static int getCorrect() {
		return correct;
	}

	public static int getIncorrect() {
		return incorrect;
	}

	public static void addCorrect() {
		correct++;
	}

	public static void addIncorrect() {
		incorrect++;
	}

}
