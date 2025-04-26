package com.d10f.trasher;

import picocli.CommandLine;
import com.d10f.trasher.args.ArgumentParser;

public class Trasher {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new ArgumentParser()).execute(args);
        System.exit(exitCode);
    }
}
