# JModule
JModule is a simple, lightweight API written to help people easily write clean, organized, and highly customizable command-line applications in Java.
JModule works by running a console client containing multiple *modules*, each containing their own commands. The application
user can switch between modules to access their commands and can view a customizable help page for each module. This design allows the developer to organize commands by functionality, leading to an cleaner flow and easier overall user experience.

## Why JModule?
JModule can be thought of a much more lightweight, automated alternative to other Java CLI design libraries. JModule's functions were designed with both the user and developer in mind, and it is the only API of its kind that writes apps with a modular design. JModule automates not only the proccess of making sure the console parses input correctly but the process of ensuring a clean user experience as well, leaving the developer with much more freedom to focus on the functions of their app.
### Features
  - Commands can be organized into modules
  - A range of customizations that can be accessed through simple boolean functions
  - JModule implements its own keylistener (not Java.awt), allowing for it to detect individual bytes passed through the command line.
  - Tab completion, history toggling, and insert mode are all supported.
  - Fully-fledged [example application](#example-app), [documentation](#documentation), and [usage guide](#writing-a-jmodule-application).

As of the current version, JModule is optimized to run on *nix systems and does not yet support the windows command prompt. However, in future versions I plan to add Windows compatiblity.


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
All the code examples in this README are taken from [ExampleApp.java](https://github.com/pkelaita/JModule/blob/master/examples/ExampleApp.java), which is a simple arithmetic program and can be found in
the 'examples' folder. To get a feel for the flow of a JModule application, compile the java file using  
`~$ javac ExampleApp.java` and run it with `~$ java ExampleApp`. JModule applications use the system console to take in user
input, so you'll have to run them from the command line. Feel free to use the example code as a reference for writing your
own JModule applications.
&nbsp;

&nbsp;

## What's New in Version 1.2
### Command toggling
JModule now supports writing much more customizable applications. First off, I've added the ability to enabled toggling through logged history
and possible commands using the arrow and tab keys. These abilities are set to `false` by default, but you can enable them by using the following
commands:
```java
yourClient.setHistoryLoggingEnabled(true);
yourClient.setTabToggleEnabled(true);
````
When the user runs your app, they will now be able to toggle through their command history by using the &uarr; and &darr; keys and toggle through
possible commands within the current module by using the 'tab' key.

### Customizable prompt
JModule 1.2 also has a much more [customizable CLI prompt](#customizable-cli-prompt). These commands are also outlined in the [Example app](https://github.com/pkelaita/JModule/blob/master/examples/ExampleApp.java#L74) and in the documentation [here](https://cdn.rawgit.com/pkelaita/JModule/30d8966a/documentation/1.2/com/jModule/exec/ConsoleClient.html).

To see the full list of changes implemented in version 1.2, consult the [Changelog](https://github.com/pkelaita/JModule/blob/master/CHANGELOG.md#version-12).
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
a new line to the existing usage statement. To see examples of these implementations, consult the file [ExampleApp.java](https://github.com/pkelaita/JModule/blob/master/examples/ExampleApp.java).

### Creating a custom control flow
Useful command-line applications will have a variety of possible commands. Let's assume that in addition to our subtraction
command, we've defined an addition command, a 'quizme' command taking no parameters that asks you simple math questions, and
an 'info' command that reports how well you've done on past quizzes. The implementations for these commands can all be found
in [ExampleApp.java](https://github.com/pkelaita/JModule/blob/master/examples/ExampleApp.java). Now that we have a decent amount of commands, we can start organizing them into modules. We'll put 'add'
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
the ConsoleClient class. This class is very easy to instantiate; all you need to do is give it a name and a home module that
the application can use as a starting point. From there, we can add additional modules, enable history logging and command toggling,
and finally, run the console application.
```java
ConsoleClient client = new ConsoleClient("ExampleEducationApp", math);
client.addModule(quiz);
client.setHistoryLoggingEnabled(true);
client.setTabToggleEnabled(true);

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
     This will print the number of previous commands, preceded by a space, after the appname and module and before the prompt separator. Adding this line to our code, out prompt will now print as `ExampleApp-v1.0/math 0> `.

To take an in-depth look at the fully implemented example application, [ExampleApp.java](https://github.com/pkelaita/JModule/blob/master/examples/ExampleApp.java) is outfitted with helpful comments
and defines all its logic classes in the same file for readability.
&nbsp;

&nbsp;

## Planned future updates
* Ability to share data between commands without writing a separate class to store data
* More flexible parameter options
  * Separate class for parameters
  * Support for indefinite parameters
  * Different types of parameters, such as toggles, flags, etc.
* Ability to hide input for certain commands, such as passwords
* CommandLogic as an instance of java.lang.Thread
&nbsp;

&nbsp;

## Documentation
The source code Javadoc for JModule (as of version 1.2.0) can be found [here](https://cdn.rawgit.com/pkelaita/JModule/30d8966a/documentation/1.2/overview-summary.html). I update the Javadoc with every major version release.
#### Older versions
  - [1.1](https://cdn.rawgit.com/pkelaita/JModule/30d8966a/documentation/1.1/overview-summary.html)
  - [1.0](https://cdn.rawgit.com/pkelaita/JModule/30d8966a/documentation/1.0/overview-summary.html)
&nbsp;

&nbsp;

## Contact
Thanks for checking out JModule! Feel free to contact me at <pierce@kelaita.com> with any questions or suggestions, or if you want to contribute. <br>
- [My Github](https://github.com/pkelaita)<br>
- [My LinkedIn](https://www.linkedin.com/in/pierce-kelaita-073187142/)
&nbsp;

&nbsp;

<sub>UW CS accept me tho</sub>