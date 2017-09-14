import java.util.Random;

import com.jModule.def.BoundedCommand;
import com.jModule.def.Command;
import com.jModule.def.CommandLogic;
import com.jModule.exec.ConsoleClient;
import com.jModule.exec.Module;

/*
 * The classes in this file serve as an example of how to use the JModule API.
 * In this case, we are writing a simple arithmetic program that has a module
 * to perform arithmetic quiestions, and a module to quiz yourself.
 */

public class ExampleApp {

	// --------- REGULAR FUNCTIONS ----------

	private static int correct = 0;
	private static int incorrect = 0;

	public static void add(String[] args) {
		try {
			int a = Integer.parseInt(args[0]);
			int b = Integer.parseInt(args[1]);
			System.out.println("Sum: " + (a + b) + "\n");
		} catch (NumberFormatException nfe) {
			System.out.println("Invalid input!\n");
		}
	}

	public static void subtract(String[] args) {
		try {
			int a = Integer.parseInt(args[0]);
			int b = Integer.parseInt(args[1]);
			System.out.println("Difference: " + (a - b) + "\n");
		} catch (NumberFormatException nfe) {
			System.out.println("Invalid input!\n");
		}
	}

	public static void multiply(String[] args) {
		try {
			int prod;
			if (args.length == 0) {
				prod = 0;
			} else {
				prod = 1;
				for (String str : args) {
					int fac = Integer.parseInt(str);
					prod *= fac;
				}
			}
			System.out.println("Product: " + prod + "\n");
		} catch (NumberFormatException nfe) {
			System.out.println("Invalid input!\n");
		}
	}

	public static void quizme() {
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

		while (true) {
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
					correct++;
				} else {
					System.out.println("Incorrect - correct answer was " + answer + ".\n");
					incorrect++;
				}
				break;
			} catch (NumberFormatException nfe) {
				System.out.println("Invalid input!");
			}
		}
	}

	public static void printPerformance() {
		System.out.println("Correct answers: " + correct);
		System.out.println("Incorrect answers: " + incorrect + "\n");
	}

	public static void main(String[] args) {

		// ----------- JMODULE FUNCTIONS -------------

		// set up commands

		// 'add' - 2 parameters
		Command addCmd = new Command("add", "Adds 2 numbers together",
				new CommandLogic(new String[] { "First number", "Second number" }) {

					@Override
					public void execute(String[] args) {
						add(args);
					}

				});

		// 'subtract' - 2 parameters
		Command subCmd = new Command("subtract", "Subtracts 2 numbers",
				new CommandLogic(new String[] { "First number", "Second number" }) {

					@Override
					public void execute(String[] args) {
						subtract(args);
					}

				});
		subCmd.addReference("sub"); // add alternate reference to this command
		subCmd.resetUsage("Usage: the same as above"); // edit usage info with resetUsage() and appendUsage()
		subCmd.appendUsage("Call this command by typing either 'subtract' or alternatively, 'sub'");

		// 'multiply' - this is an example of an bounded command that can have any number of parameters
		// within a specified range
		Command multCmd = new BoundedCommand("multiply", "Multiplies 2 or more numbers", new CommandLogic() {

			@Override
			public void execute(String[] args) {
				multiply(args);

			}

			// in this instance, we set the mininum number of parameters to 2 and
			// leave the maximum open
		}, 2).setParamSummary(" <First number> <Factors> ...");
		multCmd.addReference("mult");
		multCmd.addReference("mul");

		// 'quizme' - no parameters
		Command quizCmd = new Command("quizme", "Tests your knowledge of math", new CommandLogic() {

			@Override
			public void execute(String[] args) {
				quizme();
			}
		});
		quizCmd.addReference("qm"); // add an alternative reference to this command

		// 'info' - no parameters
		Command infoCmd = new Command("info", "Tells you your quiz performance record", new CommandLogic() {

			@Override
			public void execute(String[] args) {
				printPerformance();
			}

		});

		// set up modules - useful for grouping commands of similar type together
		Module math = new Module("math");
		math.addCommand(addCmd);
		math.addCommand(subCmd);
		math.addCommand(multCmd);

		Module quiz = new Module("quiz");
		quiz.addCommand(quizCmd);
		quiz.addCommand(infoCmd);

		// append help page - this message will now show at the bottom of the help page
		// for the 'quiz' module
		quiz.appendHelpPage(
				"\nWarning: the math quiz problems in this module are very advanced, so proceed with caution");

		// set up console with home module 'math.' This means when the user runs the
		// app, the 'math' module will be running.
		ConsoleClient client = new ConsoleClient("Example Education App", math);

		// add the module 'quiz'
		client.addModule(quiz);

		// enable arrow (history) and tab (possible command) toggling
		client.setHistoryLoggingEnabled(true);
		client.setTabToggleEnabled(true);

		// customize prompt
		client.setHistoryIndexDisplayEnabled(true);
		client.setPromptDisplayName("ExampleApp-v1.0");
		client.setModuleSeparator("/");
		client.setPromptSeparator(">");

		// print a welcome message
		System.out.println("Welcome to the example app!\n\n");

		// run the console client
		client.runConsole();

	}
}