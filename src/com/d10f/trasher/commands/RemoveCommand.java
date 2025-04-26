package com.d10f.trasher.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "remove", description = "Deletes the given files by sending them to the trash can", mixinStandardHelpOptions = true)
public class RemoveCommand implements Callable<Integer> {

    @Parameters(description = "The files to be removed.")
    private File[] files;

    @Option(names = {"-a", "--atomic"}, description = "Perform the operation atomically; exits immediately without making any changes if any error is encountered. This is safer but will take longer to run.")
    private boolean atomic;

    @Option(names = {"-d", "--dry-run"}, description = "Prints a report of the result of running this script, without actually making any changes.")
    private boolean dryRun;

    @Option(names = {"-f", "--force"}, description = "Removes directories and their contents recursively.")
    private boolean force;

    @Option(names = {"-i", "--interactive"}, description = "Prompt for confirmation on each file deletion.")
    private boolean interactive;

    @Override
    public Integer call() {
        for (File file : files) {
            // TODO: Check file system:
            //       - If local file, check if a trash directory already exists at $XDG_DATA_HOME/Trash.
            //       - If remote file, check if a trash directory already exists at the root of the file system.
        }

        return 0;
    }
}
