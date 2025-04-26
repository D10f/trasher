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

    @Option(names = {"-a", "--atomic"}, description = "Perform the operation atomically; exits immediately without making any changes if any error is encountered.")
    private boolean atomic;

    @Option(names = {"-d", "--dry-run"}, description = "Prints a report of the result of running this script, without actually making any changes.")
    private boolean dryRun;

    @Option(names = {"-f", "--force"}, description = "Removes directories recursively, instead of throwing an error.")
    private boolean force;

    @Override
    public Integer call() {
        for (File file : files) {
            System.out.println(file.getAbsoluteFile());
        }

        return 0;
    }
}
