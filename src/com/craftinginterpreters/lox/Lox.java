package com.craftinginterpreters.lox;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Lox {
    
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            // Using the UNIX sysexits.h convention for exit codes
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    /**
     * Give jlox a path to a file, it'll read and execute it
     * @param path
     * @throws IOException
     */
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code
        if (hadError) System.exit(65);
    }

    /**
     * Fire up Jlox without any arguments and it drops you into a prompt where you can enter and execute code one line at a time
     * @throws IOException
     */
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        // What the hell does ;; do
        // It means just loop forever until we break
        for (;;) {
            System.out.println("> ");
            String line = reader.readLine(); // If we want to kill app, type Control-D
            // Then, readLine returns null, so we break
            if (line == null) break;
            run(line);
            hadError = false;
        }
    }

    /**
     * Runs code
     * @param source
     */
    private static void run(String source) {
        Scanner sc = new Scanner(source);
        List<Token> tokens = new sc.scanTokens();

        // For now, just print the tokens
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    /**
     * Calls report
     * @param line
     * @param message
     */
    static void error(int line, String message) {
        report(line, "", message);
    }

    /**
     * Reports an error
     * @param line
     * @param where
     * @param message
     */
    private static void report(int line, String where, String message) {
        System.err.println(
            "[line " + line + "] Error" + where + ": " + message
        );
        hadError = true;
    }

    /**
     * Here are all my member variables
     */
    static boolean hadError = false; // What do you think this means?

}