package com.d10f.trasher;

import picocli.CommandLine;
import picocli.CommandLine.MutuallyExclusiveArgsException;
import com.d10f.trasher.args.ArgumentParser;

public class Trasher {
    public static void main(String[] args) {
        int exitCode = 0;
        try {
            exitCode = new CommandLine(new ArgumentParser()).execute(args);
        } catch (MutuallyExclusiveArgsException ex) {
            System.out.println(ex.getMessage());
        } finally {
            System.exit(exitCode);
        }
    }
}
