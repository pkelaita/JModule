# JModule
JModule is a simple, lightweight Java library written to help people easily write clean, organized, and highly customizable command-line applications.
JModule works by running a console client containing multiple *modules*, each containing their own commands. The application
user can switch between modules to access their commands and can view a customizable help page for each module. This design allows the developer to organize commands by functionality, leading to an cleaner flow and easier overall user experience.

## Features
  - Commands can be organized into modules
  - A range of customizations that can be accessed through simple functions
  - Tab completion
  - History toggling with &uarr; and &darr;
  - Insert mode using &larr; and &rarr;
  - Chained commands using `;`
  - JModule implements its own keylistener (not Java.awt), allowing for it to detect individual bytes passed through the command line.
  - Fully-fledged [example application](#example-app), [documentation](#documentation), and [usage guide](#writing-a-jmodule-application).

As of the current version, JModule is optimized to run on *nix systems and does not yet support the windows command prompt. However, in future versions I plan to add Windows compatiblity.


## Getting started
### Setting up
In order to use JModule, download the latest version of `JModule.jar` from the [releases page](https://github.com/pkelaita/JModule/releases) and add the jar to your preferred classpath. JModule's API is contained in two packages: [com.jModule.def](https://cdn.rawgit.com/pkelaita/JModule/c2f3b390/documentation/1.3/index.html) (defining commands and parameters) and [com.jModule.exec](https://cdn.rawgit.com/pkelaita/JModule/c2f3b390/documentation/1.3/index.html) (organization and execution of commands). The following classes are essential to use JModule
```java
import com.jModule.def.Command;
import com.jModule.def.CommandLogic;
import com.jModule.exec.ConsoleClient;
import com.jModule.exec.Module;
```
The remaining classes are optional and add deeper functionality into the JModule API
```java
import com.jModule.def.BoundedCommand;
import com.jModule.def.IndefiniteCommand;
import com.jModule.def.Option;
```
### Example App
JModule includes an [Example Application](https://github.com/pkelaita/JModule/blob/master/examples/ExampleApp.java), which is a simple arithmetic program that utilizes most of the functionality in JModule's API. To get a feel for the flow of a JModule application, make sure you have JModule in your classpath, clone or download the file, compile the java file using `~$ javac ExampleApp.java` and run it with `~$ java ExampleApp`. The code is full of comments to help you understand JModule's functionality. Feel free to modify or use the example code as a reference for writing your own JModule applications.
&nbsp;

&nbsp;

## Writing a JModule application
### Commands
Each JModule command runs based on its own command logic, an abstract class that must override the method execute(String[] args) to define the command's execution. A command is instantiated with a name, a description and logic. The simplest command will just have logic and no parameters
```java
Command helloCmd = new Command("hello", "Says hello", new CommandLogic() {

	@Override
	public void execute(String[] args) {
		System.out.println("Hello world!");
	}

});
```

The command's name (converted to lowercase and with spaces removed) acts as its default command-line reference. We can also add alternate references to any command. This can be done with the following method
```java
helloCmd.addReference("greet");
```
Our `hello` command can now be called from the console by either typing `hello` or `greet`.
```
~$ hello
Hello world!

~$ greet
Hello world!
```

### Parameters
We can also add some parameters to our command by defining them as a `String[]` in the CommandLogic constructor.
```java
Command helloCmd = new Command("hello", "Says hello to the user", new CommandLogic(
	new String[] {
		"first name",
		"last name"
	}) {

	@Override
	public void execute(String[] args) {
		System.out.println("Hello, " + args[0] + " " + args[1] + "!");
	}

});
helloCmd.addReference("greet");
```
Parameters in regular command are required by user input for the command to run. When commands are executed with an incorrect number of parameters or the module help page is accessed, the command's usage information will print to the console. Since we added a few parameters and an alternate reference to our `hello` command, the generated usage statement will be as follows.
```
Usage: ~$ hello <first name> <last name>
       OR greet ~
```
If we want to change or add to the default usage statement, the method `yourCommand.resetUsage(String reset)` can be used to
replace the default usage statement with your own statement, and the method `yourCommand.appendUsage(String append)` can be used to add
a new line to the existing usage statement. To see examples of these implementations, consult the [Example App](https://github.com/pkelaita/JModule/blob/master/examples/ExampleApp.java).

### Options
Sometimes we may want to give the user the choice to change the functionality of a command without having to worry about creating new commands or parameters. This can be done through the use of options. Options are arguments passed to the command that do not count as parameters and can be used anywhere in the command's parameters. They are, by nature, optional. Each option is initialized with a one-character flag, denoted by a single dash (for example, `-v`) and a description that shows up in the enclosing command's usage statement. Additionally, just like Commands, Options can have any number of references added to it. References can be multiple characters, are typically full words or word fragments, and are denoted by a double dash (for example, `--ver` or `--verbose`). An option can have any number of references added to it, and references are reccommended but not required.
Options are added to CommandLogic with the function `addOption(Option o)` and their behavior can be defined with in `CommandLogic.execute(String[] args)` by using the boolean `if (onOption(char flag)) { /*logic here */ }` that will return true if the option exists in the command and is called by the user. As an example, let's write a 'goodbye' command with a few options.
```java
Command goodbyeCmd = new Command("goodbye", "Says goodbye", new CommandLogic() {

	@Override
	public void execute(String[] args) {
		if (onOption('p')) {
			System.out.print("Farewell, ");
		} else {
			System.out.print("Goodbye, ");
		}
		if (onOption('d')) {
			System.out.print("cruel ");
		}
		System.out.print("world!\n\n");
	}

}.addOption(new Option('p', "Makes the message polite")
		.addReference("polite"))
 .addOption(new Option('d', "Makes the message depressing")
 		.addReference("dep")
		.addReference("depressing"))
);
```
Our 'goodbye' command can be tested from the command line as so:
```
~$ goodbye
Goodbye, world!

~$ goodbye -p
Farewell, world!

~$ goodbye --polite
Farewell, world!

~$ goodbye --depressing
Goodbye, cruel world!

~$ goodbye -d -p
Farewell, cruel world!
```
JModule also supports calling multiple options quickly by combining flags as such
```
~$ goodbye -dp
Farewell, cruel world!
```
Just like parameters, options added to commands will show up in the command's usage statement.
```
'goodbye'
	Says goodbye
	Usage: ~$ goodbye
	Options:
		-p, --polite: Makes the message polite
		-b, --dep, --depressing: Makes the message depressing
```

### Indefinite and Bounded Commands
Sometimes we may want our commands to have an undefined number or a range of numbers of possible parameters. In order to do this, we can either use an Indefinite Command, Bounded Command with an open range, or a Bounded Command with a closed range. These commands should have their parameters defined in their logic's constructor, but they will have no effect on the execution of the command; they will only affect the command's usage statement.
#### Indefinite Commands
Indefinite commands may have any number of parameters passed to it by the user. An example implementation of this would be a command to list names
```java
Command listCmd = new IndefiniteCommand("list", "Lists given names, separated by commas", new CommandLogic(
	new String[] {
		"names..."
	}) {

	@Override
	public void execute(String[] args) {
		for (int i = 0; i < args.length; i++) {
			System.out.print(args[i]);

			if (i != args.length - 1) {
				System.out.print(", ");
			}
		}
	}

});
```
#### Bounded Commands with open parameters
Bounded commands with open parameters are instantiated the same way as Indefinite or regular Commands, but a minimum and maximum number of parameters is specified. To have a bounded command with open parameters, specify only the minimum at the end of the BoundedCommand constructor. For example, a function to list the names of people all with the same last name, where the last name is taken as the first parameter, would have a minimum of two parameters and could look something like this.
```java
Command famCmd = new BoundedCommand("famlist", "Lists the full names of family members who all have the same last name", new CommandLogic(
	new String[] {
		"family name",
		"first names..."
	}) {

	@Override
	public void execute(String[] args) {
		String familyName = args[0];

		for (int i = 1; i < args.length; i++) {
			System.out.print(args[i] + " " + familyName);

			if (i != args.length - 1) {
				System.out.print(", ");
			}
		}
	}

}, 2); // set minimum number of parameters to 2
```
#### Bounded Commands with closed parameters
Bounded commands can also have closed parameters, meaning both a minimum and maximum number of parameters is specified at the end of the constructor. An example would be a function that multiplies up to 4 numbers.
```java
Command multCmd = new BoundedCommand("multiply", "Multiplies up to 4 numbers", new CommandLogic(
	new String[] {
		"factors..."
	}) {

	@Override
	public void execute(String[] args) {
		int product = 0;
		try {
			for (String str : args) {
				int factor = Integer.parseInt(str);
				product *= factor;
			}
			System.out.print("Product: " + product);
		...
	}

}, 0, 4); // set minimum number of parameters to 0 and maximum to 4
```

### Creating a custom control flow
#### Organizing Commands into Modules
Useful command-line applications will have a variety of possible commands. Let's assume that we've created a few arithmetic commands, a 'quizme' command taking no parameters that asks you simple math questions, and
an 'info' command that reports how well you've done on past quizzes. The implementations for these commands can all be found
in [ExampleApp.java](https://github.com/pkelaita/JModule/blob/master/examples/ExampleApp.java). To ease the user experience, we can organize commands with similar functionality into their own modules. In this instance, let's put the arithmetic commands in one module called `math` and the 'info' and 'quizme' commands in another module called `quiz`.
```java
Module math = new Module("math");
math.addCommand(addCmd);
math.addCommand(subCmd);
math.addCommand(multCmd);

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
'multiply'
	Multiplies 2 or more numbers
	Usage: ~$ multiply <First number> <Factors...>
	       OR mult ~
	       OR mul ~
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

#### Organizing modules into a Console Client
We can organize our modules into a client by specifying the name of the app and the home module in the constructor, add other modules with `addModule()`, and run the console app with `runConsole()`.
```java
ConsoleClient client = new ConsoleClient("ExampleEducationApp", math);
client.addModule(quiz);

client.runConsole();

```
JModule also supports the ability to write non-modular apps. To do this, just throw all your commands into a single module and set it up as the client's
home module. The name of the module will not show up in the prompt and the help page will show the app name rather than the home module name.

### Customizable CLI prompt
JModule supports the ability to customize the prompt that will be printed to the CLI for each command. As of the current version, there are four possible prompt customization functions.
  - **Prompt display name** <br>
     By default, the prompt will begin with the specified app name, with its spaces removed. However, we can change this if we'd like. For example, we can change the app name of our example application to shorten the name and include the version.
     ```java
     client.setPromptDisplayName("ExampleApp-v1.0");
     ```
     Our prompt in its home module will now print as `ExampleApp-v1.0: math$`. <br>
     This is useful to add version information that is not specified in the app name, or to shorten the name that is printed to the CLI.
  - **Separators** <br>
     By default, JModule apps separate the app name from the module name with `": "`, a colon followed by a space. We can change this from our app. For example, let's change the separator to a slash with no space in our app.
     ```java
     client.setModuleSeparator("/");
     ```
     Our prompt in its home module will now display as `ExampleApp-v1.0/math$`. <br>
     We can also change the prompt/user input separator. By default, JModule sets this to `"$"`. We can change this in our example application.
     ```java
     client.setPromptSeparator(">");
     ```
     Our prompt in its home module will now print as `ExampleApp-v1.0/math> `. The app will automatically print a space after the prompt to separate it from user input.<br>

  - **History index display** <br>
     JModule apps with history logging enabled can also display the history index to the CLI, much like the bash command prompt. Enable this by using
     ```java
     client.setHistoryIndexDisplayEnabled(true);
     ```
     This will print the number of previous commands, preceded by a space, after the appname and module and before the prompt separator. Adding this line to our code, out prompt will now print as `ExampleApp-v1.0/math 0> `<br>
     Note: in order to enable this function, history logging must be enabled (see below).


### Further customizations
JModule apps can be customized to include a number of functionalities that could be useful to the user of your application. They are all set to `false` by default, but can be enabled with simple functions.
  - **History Logging**<br>
     ```java
     client.enableHistoryLogging(true);
     ```
     Enable history logging to allow the user to cycle through their previous commands using the &uarr; and &darr; arrows. Whatever characters they have typed before toggling back through their history will be preserved if they toggle back to their current location. Enabling this funciton also allows you to enable history index display on the prompt.
  - **Tab Completion**<br>
     ```java
     client.enableTabCompletion(true);
     ```
     Enabling tab completion allows the user to use the `tab` key to cycle through possible commands in their current module that start with what they already have typed on the command line. If the user hasn't typed anything, `tab` will cycle through all of the possible commands in the current module.
  - **Alerts**
     ```java
     client.enableAlerts(true);
     ```
     Enabling alerts allows the app to trigger the system's default alert (typically a sound such as [this one](https://www.youtube.com/watch?v=8rz44o_gGpE)). These alerts are triggered any time the user uses a special key that is unable to have any effect on the CLI. For example, an alert could trigger when the user presses `delete` with no characters typed in, or using `tab` toggling when no commands match what they've currently typed.

To take an in-depth look at the fully implemented example application, [ExampleApp.java](https://github.com/pkelaita/JModule/blob/master/examples/ExampleApp.java) is outfitted with helpful comments
and defines all its logic classes in the same file for readability.
&nbsp;

&nbsp;

## Planned future updates
* More flexible parameter options
  * Separate class for parameters
  * Mandatory and optional toggles for parameters
* Ability for the developer to implement custom keylisteners using the JModule API
* Ability to hide input for certain commands, such as passwords
&nbsp;

&nbsp;

## Documentation
The source code Javadoc for JModule (as of version 1.3.0) can be found [here](https://cdn.rawgit.com/pkelaita/JModule/c2f3b390/documentation/1.3/index.html). I update the Javadoc with every major version release.
#### Older versions
  - [1.2](https://cdn.rawgit.com/pkelaita/JModule/c2f3b390/documentation/1.2/index.html)
  - [1.1](https://cdn.rawgit.com/pkelaita/JModule/c2f3b390/documentation/1.1/index.html)
  - [1.0](https://cdn.rawgit.com/pkelaita/JModule/c2f3b390/documentation/1.0/index.html)
&nbsp;

&nbsp;

## Contact
Thanks for checking out JModule! Feel free to contact me at <pierce@kelaita.com> with any questions or suggestions, or if you want to contribute. <br>
- [My Github](https://github.com/pkelaita)<br>
- [My LinkedIn](https://www.linkedin.com/in/pierce-kelaita-073187142/)
&nbsp;

&nbsp;

<sub>UW CS accept me tho</sub>