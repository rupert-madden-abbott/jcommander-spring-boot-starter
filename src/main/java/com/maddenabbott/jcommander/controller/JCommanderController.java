package com.maddenabbott.jcommander.controller;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import java.util.Map;

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
     * <p>
     * This method will parse the parsed in command line args and expect them to select an already
     * configured command. It will also expect that command to implement the {@link Command} interface
     * and will call it's run method.
     *
     * @param args the command line args
     * @throws Exception if something went wrong during command execution
     */
    public void execute(final String[] args) throws Exception {
        Map<String, JCommander> commands = getCommands();
        String commandName = getCommandName(args);
        Command command = toCommand(commandName, commands);
        command.run();
    }

    private String getCommandName(final String[] args) {
        if (args == null || args.length < 1) {
            throw new InvalidUseException("No command line arguments were provided.", getUsage());
        }

        try {
            jCommander.parse(args);
            return jCommander.getParsedCommand();

        } catch (ParameterException e) {
            throw new InvalidUseException(e.getMessage(), getUsage(), e);
        }

    }

    private Map<String, JCommander> getCommands() {
        Map<String, JCommander> commands = jCommander.getCommands();
        if (commands.isEmpty()) {
            throw new IllegalStateException("No Command implementing beans have been defined.");
        }
        return commands;
    }

    private Command toCommand(String commandName,  Map<String, JCommander> commands) {
        Object command = commands.get(commandName).getObjects().get(0);

        if (!(command instanceof Command)) {
            throw new IllegalStateException("The selected command '" + commandName
                    + "' does not implement the Command interface.");
        }
        return (Command) command;
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
