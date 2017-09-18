import java.util.Random;

import com.jModule.def.BoundedCommand;
import com.jModule.def.Command;
import com.jModule.def.CommandLogic;
import com.jModule.def.Option;
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
	private static int attempts = 0;

	private static void add(String[] args) {
		try {
			int a = Integer.parseInt(args[0]);
			int b = Integer.parseInt(args[1]);
			System.out.println("Sum: " + (a + b) + "\n");
		} catch (NumberFormatException nfe) {
			System.out.println("Invalid input!\n");
		}
	}

	private static void subtract(String[] args) {
		try {
			int a = Integer.parseInt(args[0]);
			int b = Integer.parseInt(args[1]);
			System.out.println("Difference: " + (a - b) + "\n");
		} catch (NumberFormatException nfe) {
			System.out.println("Invalid input!\n");
		}
	}

	private static void multiply(String[] args) {
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

	private static void quizme() {

		boolean isAdd = new Random().nextBoolean();
		String operation = isAdd ? " + " : " - ";

		int a = new Random().nextInt(10);
		int b = new Random().nextInt(10);
		System.out.println("What is " + a + operation + b + "? ");

		while (true) {
			attempts++;
			try {
				
				int answer = isAdd ? a + b : a - b;
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
				System.out.println("What is " + a + operation + b + "? ");
			}
		}
	}

	public static void main(String[] args) {

		// ----------- JMODULE FUNCTIONS -------------

		// set up commands

		// 'add' - 2 parameters
		Command addCmd = new Command("add", "Adds 2 numbers together",
				new CommandLogic(new String[] {
						"First number",
						"Second number" }) {

					@Override
					public void execute(String[] args) {
						add(args);
					}

				});

		
		// 'subtract' - 2 parameters
		Command subCmd = new Command("subtract", "Subtracts 2 numbers",
				new CommandLogic(new String[] {
						"First number", 
						"Second number" }) {

					@Override
					public void execute(String[] args) {
						subtract(args);
					}

				});
		subCmd.addReference("sub"); // add alternate reference to this command
		// edit usage info with resetUsage() and appendUsage()
		subCmd.resetUsage("Usage: the same as above");
		subCmd.appendUsage("Call this command by typing either 'subtract' or alternatively, 'sub'");

		
		/* 
		 * 'multiply' - This is an example of an bounded command that can have any
		 * number of parameters within a specified range. We can either leave the
		 * range open by only specifying a minimum in the constructor as such:
		 * 
		 * BoundedCommmand(String name, String description, CommandLogic logic, int min)
		 * 
		 * or have the range closed by specifying both a minimum and a maximum:
		 * 
		 * BoundedCommmand(String name, String description, CommandLogic logic, int min, int max)
		 * 
		 * Note that in a bounded or indefinite command, the String[] params in the CommandLogic
		 * constructor does not affect how the command runs, but only what shows in the command's
		 * usage info.
		 * 
		 */
		Command multCmd = new BoundedCommand("multiply", "Multiplies 2 or more numbers",
				new CommandLogic(new String[] {
						"First number",
						"Factors..." }) {

			@Override
			public void execute(String[] args) {
				multiply(args);

			}

			// in this instance, we set the mininum number of parameters to 2 and
			// leave the maximum open
		}, 2);
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
				System.out.println("Correct answers: " + correct);
				System.out.println("Incorrect answers: " + incorrect);
				
				// define the behavior of the command when called with a specific option, signified by its flag
				if (onOption('v')) {
					System.out.println("Total attempts: " + attempts);
				}
				if (onOption('b')) {
					System.out.println("Cracking open a [B]old one with the [B]oys");
				}
				System.out.println();
			}

		}.addOption(new Option('v', "Prints the total attempts, including invalid input")
				.addReference("ver")
				.addReference("verbose"))
		 .addOption(new Option('b', "Memes")
				.addReference("boys"))
		);

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

		// Enable useful functions in the console - they are all set to false by default

		// enable history logging - toggle with up and down arrows
		client.enableHistoryLogging(true);
		
		// enable showing the history index in the prompt (history logging must be enabled)
		client.enableHistoryIndexDisplay(true);
		
		// enable tab completion
		client.enableTabCompletion(true);
		
		// enable alerts
		client.enableAlerts(true);

		// customize prompt
		client.setPromptDisplayName("ExampleApp-v1.0");
		client.setModuleSeparator("/");
		client.setPromptSeparator(">");

		// print a welcome message
		System.out.println("\nWelcome to the example app!\n");
		
		// add a shutdown hook
		client.addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Goodbye!\n");
			}
		});

		// run the console client
		client.runConsole();

	}
}