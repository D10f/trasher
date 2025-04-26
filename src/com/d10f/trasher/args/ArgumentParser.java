package com.d10f.trasher.args;

import com.d10f.trasher.commands.RemoveCommand;

import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(
        name = "Trasher",
        description = "Soft-delete files and manage files sent to the trash.",
        version = "0.1.0-alpha",
        mixinStandardHelpOptions = true,
        subcommands = { RemoveCommand.class }
)
public class ArgumentParser implements Callable<Integer> {

    @Override
    public Integer call() {
        return 0;
    }
}
