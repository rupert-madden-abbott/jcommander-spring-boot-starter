package com.maddenabbott.jcommander.controller;

import java.util.Map;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * A controller for commands.
 */
public class JCommanderController {
  private final JCommander jCommander;

  public JCommanderController(final JCommander jCommander) {
    this.jCommander = jCommander;
  }

  /**
   * Execute the command selected on the command line.
   *
   * This method will parse the parsed in command line args and expect them to select an already
   * configured command. It will also expect that command to implement the {@link Command} interface
   * and will call it's run method.
   *
   * @param args the command line args
   * @throws Exception if something went wrong during command execution
   */
  public void execute(final String[] args) throws Exception {
    getCommand(args).run();
  }

  private Command getCommand(final String[] args) {
    try {
      Map<String, JCommander> commands = jCommander.getCommands();
      if (commands.isEmpty()) {
        throw new ParameterException("No command objects were provided.");
      }

      if (args == null || args.length < 1) {
        throw new ParameterException("No command line arguments were provided.");
      }

      jCommander.parse(args);
      final String commandName = jCommander.getParsedCommand();

      Object command = commands.get(commandName).getObjects().get(0);

      if (!(command instanceof Command)) {
        throw new ParameterException("The selected command '" + commandName
          + "' does not implement the Command interface.");
      }
      return (Command) command;
    } catch (ParameterException e) {
      throw new RuntimeException("Invalid command line arguments.\n" + getUsage(), e);
    }
  }

  /**
   * Extracts the usage string from JCommander's parser.
   *
   * @return the usage string.
   */
  private String getUsage() {
    final StringBuilder usage = new StringBuilder();
    jCommander.getUsageFormatter().usage(usage);
    return usage.toString();
  }
}
