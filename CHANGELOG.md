# Changelog

## Version 1.2
### 1.2.0 - 2017-09-11
- **Added**
  - Customizable prompt display
    - Enable/disable history index
    - Ability to change app name display
    - Ability to change module separator
    - Ability to change prompt/input separator
  - Ability to toggle through possible commands using the `tab` key
  - Enable/disable toggle function
  - Help page now shows the app name if the app has only 1 module
- **Removed**
  - Help message when user starts up program, planning to add more customization in future updates
  - If the app only has 1 module, the module name is no longer shown in the prompt
- **Fixed**
  - Bug where extra characters got printed and not processed
  - Input is processed *a lot* faster
- **Issues**
  - New character replacement algorithm is untested on command prompts that produced reverse wrapping bug in 1.1.0
## Version 1.1
### 1.1.1 - 2017-09-09
- **Fixed**
  - SIGNT no longer locks terminal in raw input mode
  - Toggling through history no longer causes reverse wrapping with prompt
- **Issues**
  - Extra characters printed to CLI when multiple charaters are input in rapid succession (does not affect working command)
  - Rapid repitition of the delete key causes input stream to block until enter key is pressed
### 1.1.0 - 2017-09-06
- **Added**
  - Ability to set command logging
  - Ability to toggle between history commands
- **Fixed**
  - CLI no longer freezes when toggling through history commands
- **Issues**
  - SIGNT locks terminal in raw input mode
  - Prompt string gets reverse wrapped when toggling through history on some command prompts

## Version 1.0
### 1.0.3 - 2017-09-05
- **Fixed**
  - Bug when using arrow keys
- **Issues**
  - Extra characters printed to CLI when multiple charaters are input in rapid succession (does not affect working command)
  - Rapid repitition of the delete key causes input stream to block until enter key is pressed

### 1.0.2 - 2017-09-04
- **Added**
  - Ability to use backspace key
  - Reads direct user input rather than through System.console()
  - Storing commands in history (deprecated - to be fully implemented in later version)
- **Removed**
  - Support for Windows command line (runnable but untested) - Will be reimplemented in later version
- **Issues**
  - Bug when using arrow keys
  - Bug when taking user input inside an IDE

### 1.0.1 - 2017-08-31
- **Fixed**
  - Bug involving zero-parameter usage
  - Extra lines in CLI