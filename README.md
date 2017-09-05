# JModule
JModule is a simple, lightweight API written to help people easily write clean, organized, and highly customizable command-line applications in Java.
JModule works by running a console client containing multiple *modules*, each containing their own commands. The application
user can switch between modules to access their commands and can view a customizable help page for each module. This design allows the developer to organize commands by functionality, leading to an cleaner flow and easier overall user
experience. 

## Getting started
### Setting up
In order to use JModule, download the latest version of `JModule.jar` from the [releases page](https://github.com/pkelaita/JModule/releases) and add the jar to your preferred classpath. JModule contains
4 classes and 2 packages that are needed to use the API, which you'll import into your application and logic classes.
```java
import com.jModule.def.Command;
import com.jModule.def.CommandLogic;
import com.jModule.exec.ConsoleClient;
import com.jModule.exec.Module;
```
### Example App
All the code examples in this README are taken from `ExampleApp.java`, which is a simple arithmetic program and can be found in
the 'examples' folder. To get a feel for the flow of a JModule application, compile the java file using  
`~$ javac ExampleApp.java` and run it with `~$ java ExampleApp`. JModule applications use the system console to take in user
input, so you'll have to run them from the command line. Feel free to use the example code as a reference for writing your
own JModule applications.
&nbsp;

&nbsp;

## Writing a JModule application
### Defining your commands
Each JModule command runs based on its own command logic. So before you can create a command, you'll have to create a class
extending the abstract class CommandLogic, specific to your command. Override the abstract method `runCommand(String[] args)` to define how the
command proccesses its parameters. Let's use a simple subtraction command as an example.  
```java
class SubtractCmdLogic extends CommandLogic {

	@Override
	public void runCommand(String[] args) {
		try {
			int a = Integer.parseInt(args[0]);
			int b = Integer.parseInt(args[1]);
			System.out.println("Difference: " + (a - b) + "\n");
		...
	}
}
```
Since this command's logic takes in two parameters, you'll need to define them in an ArrayList and add them with
`setParams(ArrayList<String> params)` to your instance of your CommandLogic subclass.
```java
ArrayList<String> subtractParams = new ArrayList<>();
subtractParams.add("first number");
subtractParams.add("second number");

SubtractCmdLogic subtractLogic = new SubtractCmdLogic();
subtractLogic.setParams(subtractParams);
```
Now that we've set up our command logic, we can instantiate a command with a name, description, and our custom logic.
```java
Command subCmd = new Command("subtract", "Subtracts 2 numbers", subtractLogic);
```
The command's name is treated as its default reference to call it from the console. However, we can add additional references
that can point to the same command if needed.
```java
subCmd.addReference("sub");
```
Now, our `subtract` command can be called from the console by either typing `subtract` or `sub`.
```
~$ subtract 5 3
Difference: 2

~$ sub 5 3
Difference: 2
```

When commands are executed with an incorrect number of parameters or the module help page is accessed, the command's usage
information will print to the console. Since we added a few parameters and an alternate reference to our `subtract` command, the
generated usage statement will be as follows.
```
Usage: ~$ subtract <first number> <second number>
       OR sub ~
```
If we want to change or add to the default usage statement, the method `yourCommand.resetUsage(String reset)` can be used to
replace the default usage statement with your own statement, and the method `yourCommand.appendUsage(String append)` can be used to add
a new line to the existing usage statement. To see examples of these implementations, consult the file `ExampleApp.java`.

### Creating a custom control flow
Useful command-line applications will have a variety of possible commands. Let's assume that in addition to our subtraction
command, we've defined an addition command, a 'quizme' command taking no parameters that asks you simple math questions, and
an 'info' command that reports how well you've done on past quizzes. The implementations for these commands can all be found
in `ExampleApp.java`. Now that we have a decent amount of commands, we can start organizing them into modules. We'll put 'add'
and 'subtract' in one module, and 'quizme' and 'info' in a separate one, so that the commands in each module have similar
functionality.
```java
Module math = new Module("math");
math.addCommand(addCmd);
math.addCommand(subCmd);

Module quiz = new Module("quiz");
quiz.addCommand(quizCmd);
quiz.addCommand(infoCmd);
```

Each command has a name and description defined on instantiation, and a usage statement defined by its parameters and/or user
customization. These statements are all helpful, and can be viewed by using the `help` command in the console.
```
~ math $ help

MATH -- POSSIBLE COMMANDS
'add'
	Adds 2 numbers together
	Usage: ~$ add <first number> <second number>
'subtract'
	Subtracts 2 numbers
	Usage: ~$ subtract <first number> <second number>
	       OR sub ~
'help'
	Displays the help page for the current module.
	Usage: ~$ help

Type the name of another module to switch to that module:
	- 'quiz'

Type 'exit' at any time to exit the program
```
The `help` and `exit` commands are defined by default and do not need to be defined in your app. Similarly to command usage
staments, module help pages are generated as a standard help page of the style shown above, and can be edited with
`yourModule.resetHelpPage(String reset)` and `yourModule.appendHelpPage(String append)`.

Now that we've defined our modules, the last step is to implement these modules into a console. This can be accomplished with
the ConsoleClient class. This class is very easy to instantiate; all you need to do is give it a name and home module that
the application can use as a starting point. From there, we can add additional modules and, finally, run the console
application.
```java
ConsoleClient client = new ConsoleClient("ExampleEducationApp", math);
client.addModule(quiz);
client.runConsole();
```
To take an in-depth look at the fully implemented example application, `ExampleApp.java` is outfitted with helpful comments
and defines all its logic classes in the same file for readability.
&nbsp;

&nbsp;

## Planned future updates
* Ability to share data between commands without writing a separate class to store data
* Accessible command history
* More flexible parameter options
  * Separate class for parameters
  * Support for indefinite parameters
  * Different types of parameters, such as toggles, flags, etc.
* Ability to hide input for certain commands, such as passwords
&nbsp;

&nbsp;

Thanks for checking out JModule! Feel free to contact me at <pierce@kelaita.com> with any questions or suggestions.  
<sub>UW CS accept me tho</sub>