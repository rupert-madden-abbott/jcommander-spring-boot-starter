package com.maddenabbott.jcommander.controller;

/**
 * A command that will be added via {@link com.beust.jcommander.JCommander#addCommand(Object)}.
 * <p>
 * Implementing classes must be annotated with {@link com.beust.jcommander.Parameters} with a
 * commandName specified.
 * <p>
 * Very similar to {@link Runnable} but allows for checked exceptions to be thrown. Additionally,
 * it makes dependency injection by type less brittle as this interface's purpose is more explicit.
 */
public interface Command {
  /**
   * Run a command.
   * @throws Exception if something went wrong.
   */
  void run() throws Exception;
}
