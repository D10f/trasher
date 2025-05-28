package com.d10f.trasher.controllers;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NotDirectoryException;
import java.util.concurrent.Callable;

import com.d10f.trasher.entities.Trash;

@Command(name = "remove", description = "Deletes the given files by sending them to the trash can", mixinStandardHelpOptions = true)
public class RemoveController implements Callable<Integer> {

    private static final int ERR_TRASH_NOT_DIRECTORY = 1;
    private static final int ERR_ACCESS_DENIED = 2;

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
        int returnCode = 0;
        Exception err = null;

        try {
            Trash trash = new Trash();
            trash.deleteFiles(files);
        } catch (NotDirectoryException e) {
            err = e;
            returnCode = ERR_TRASH_NOT_DIRECTORY;
        } catch (AccessDeniedException e) {
            err = e;
            returnCode = ERR_ACCESS_DENIED;
        } catch (IOException e) {
            err = e;
            returnCode = -1;
        }

        if (err != null)
            System.err.println(err.getMessage());

        return returnCode;
    }
}
