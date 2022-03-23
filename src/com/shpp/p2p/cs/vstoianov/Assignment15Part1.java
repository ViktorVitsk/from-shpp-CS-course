package com.shpp.p2p.cs.vstoianov.assignment15;

import java.io.IOException;

/**
 * Archiver using the Huffman algorithm for data compression
 */
public class Assignment15Part1 {

    private static final String defaultInput = "assets/zip/1.txt";
    private static final String par = ".par";

    public static void main(String[] args) throws IOException {

        if (args.length == 1) { // for one argument

            processingOneArg(args[0]);

        } else if (args.length > 1) { // for two and three arguments

            String output = (args.length == 2) ? args[1] + par : args[2];

            moreOneArgs(args, output);

        } else { // if there are no arguments

            processingOneArg(defaultInput);
        }
    }

    /**
     * for 2 and more args
     *
     * @param args   args array
     * @param output name output file
     */
    private static void moreOneArgs(String[] args, String output) {
        if (args[0].equals("-a")) {

            zip(args[1], output);

        } else if (args[0].equals("-u")) {

            unZip(args[1], output);

        } else {

            if (chekToEnd(args[0], par)) unZip(args[0], args[1]);
            else zip(args[0], args[1]);
        }
    }


    /**
     * for one args
     *
     * @param arg args array
     */
    private static void processingOneArg(String arg) {
        String nameWithoutLastWords = (arg.length() > 4) ? arg.substring(0, arg.length() - 4) : arg;

        if (chekToEnd(arg, par)) {
            unZip(arg, nameWithoutLastWords + ".uar");
        } else {
            zip(arg, arg + par);
        }
    }

    /**
     * archiving
     *
     * @param input  name
     * @param output name
     */
    private static void zip(String input, String output) {
        new Zip(input, output);
    }

    /**
     * unzip
     *
     * @param input  name
     * @param output name
     */
    private static void unZip(String input, String output) {
        new Unzip(input, output);
    }

    /**
     * Check last characters
     *
     * @param str       input file name
     * @param endString what should be at the end of the name
     * @return true if the string ends with these characters
     */
    private static boolean chekToEnd(String str, String endString) {
        String s = "";
        int iteration = str.length() - 4;
        if (iteration < 0) return false;
        for (int i = iteration; i < str.length(); i++) {
            s += str.charAt(i);
        }
        return s.equals(endString);
    }

}
