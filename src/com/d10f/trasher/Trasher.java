package com.d10f.trasher;

import com.d10f.trasher.controllers.RemoveController;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.MutuallyExclusiveArgsException;

public class Trasher {

    private static final String APP_NAME = "Trasher";
    private static final String APP_VERSION = "0.1.0-alpha";
    private static final String APP_DESCRIPTION = """
            Soft-delete files and manage files sent to the trash.
            """;

    public static void main(String[] args) {
        int exitCode = 0;
        try {
            exitCode = new CommandLine(new Entrypoint()).execute(args);
        } catch (MutuallyExclusiveArgsException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.exit(exitCode);
        }
    }

    @Command(
            name = APP_NAME,
            description = APP_DESCRIPTION,
            version = APP_VERSION,
            mixinStandardHelpOptions = true,
            subcommands = {RemoveController.class}
    )
    private static class Entrypoint {
    }
}
