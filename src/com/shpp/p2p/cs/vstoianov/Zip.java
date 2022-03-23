package com.shpp.p2p.cs.vstoianov.assignment15;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class Zip {
    private static final int[] masks = { 0b10000000, 0b01000000, 0b00100000, 0b00010000, 0b00001000, 0b00000100,
            0b00000010, 0b00000001 };

    protected Zip(String input, String output) {

        Date date = new Date();

        try (FileInputStream fis = new FileInputStream(input);
                BufferedInputStream bis = new BufferedInputStream(fis);
                FileOutputStream fos = new FileOutputStream(output)) {

            try {

                byte[] buffer = new byte[bis.available()]; // create a byte buffer
                bis.read(buffer);

                int sizeSourceFile = buffer.length; // source file length
                System.out.println("Source file size        =  " + sizeSourceFile + "  bytes");

                EncodingTreeNode tree = EncodingTreeNode.buildHuffmanTree(buffer); // get the coded tree

                TreeStructure treeStructure = new TreeStructure(tree); // building a tree structure

                LinkedList<Boolean> treeShape = treeStructure.getTreeShape(); // tree structure
                LinkedList<Byte> treeLeaves = treeStructure.getTreeLeaves(); // tree leaves
                int sizeTree = treeShape.size(); // size of the tree structure in bits

                LinkedList<Byte> sizeTreeInBytes = getSizeTreeInBytes(sizeTree, Short.SIZE); // size of the tree
                                                                                             // structure in bytes
                LinkedList<Byte> sizeSourceFileInBytes = getSizeTreeInBytes(sizeSourceFile, Long.SIZE); // source file
                                                                                                        // size

                // to calculate the size of the new file
                int newFileSize = sizeTreeInBytes.size() + sizeSourceFileInBytes.size();

                while (!sizeTreeInBytes.isEmpty()) { // write the size of the tree structure to a file
                    fos.write(sizeTreeInBytes.poll());
                }

                byte[] bytesTreeStructure = getTreeStructure(treeShape); // tree structure in bytes to write
                fos.write(bytesTreeStructure);

                byte[] bytesTreeLeaves = new byte[treeLeaves.size()];
                int iteration = 0;
                while (!treeLeaves.isEmpty()) { // array of leaves to write
                    bytesTreeLeaves[iteration++] = treeLeaves.poll();
                }
                fos.write(bytesTreeLeaves); // write the leaves of the tree to a file

                newFileSize += bytesTreeLeaves.length + bytesTreeStructure.length;

                while (!sizeSourceFileInBytes.isEmpty()) { // write down the size of the source file
                    fos.write(sizeSourceFileInBytes.poll());
                }

                // table of bytes and their encrypted values
                TreeMap<Byte, StringBuilder> table = treeStructure.getDecryptedAndEncodedTable();
                byte[] encodedDate = getNewBytesArray(buffer, table);
                fos.write(encodedDate);

                newFileSize += encodedDate.length;
                System.out.println("Zipped file size        =  " + newFileSize + "  bytes");
                double efficiencyZip = (double) (sizeSourceFile - newFileSize) / sizeSourceFile * 100;
                System.out.println("Compression efficiency  =  " + String.format("%.2f", efficiencyZip) + "  %");
                // displaying the time of the archiver
                Date date1 = new Date();
                System.out.println("Archiving time          =  " + (double) (date1.getTime() - date.getTime()) / 1000
                        + "  seconds");
            } catch (OutOfMemoryError error) {
                System.err.println(error + "\nthis program does not handle files of this size");
            }

        } catch (IOException error) {
            System.err.println(error + "");
        }

    }

    /**
     * converts numbers to binary
     *
     * @param treeSize   size tree structure
     * @param numbersBit numbers bits
     * @return Queue with size in binary
     */
    private static LinkedList<Byte> getSizeTreeInBytes(int treeSize, int numbersBit) {

        StringBuilder size = new StringBuilder(Integer.toBinaryString(treeSize));
        LinkedList<Byte> bufferStack = new LinkedList<>(); // queue for bytes
        while (size.length() < numbersBit) { // add missing zeros to short
            size.insert(0, 0);
        }
        containQueue(size, bufferStack, numbersBit);

        return bufferStack;
    }

    /**
     * fills the queue
     *
     * @param sizeBuffer bit string
     * @param byteQueue  byte queue
     * @param size       number of bits
     */
    private static void containQueue(StringBuilder sizeBuffer, Queue<Byte> byteQueue, int size) {
        char ch; // current character
        int currentNewByte = 0b00000000; // to pack a new byte
        int bitIndex = 0; // to understand when a byte is full

        for (int i = 0; i < size; i++) {
            ch = sizeBuffer.charAt(i);
            if (ch == '1') { // if 1, then put 1 in the byte by index using a mask
                currentNewByte = (currentNewByte | masks[bitIndex]);
            }
            bitIndex++; // go to the next index
            if (bitIndex == Byte.SIZE) { // if the index is 8, then we zero and add the byte to the array
                byteQueue.add((byte) currentNewByte);
                bitIndex = 0; // set the bit index to zero
                currentNewByte = 0b00000000; // zero the value of the current byte
            }
        }
    }

    /**
     * converts tree structure to binary
     *
     * @param treeShape tree structure in boolean
     * @return tree structure in bytes
     */
    private byte[] getTreeStructure(LinkedList<Boolean> treeShape) {
        double sizeCurrentArr = treeShape.size();
        byte[] result = new byte[(int) Math.ceil(sizeCurrentArr / 8)];
        int bitIndex = 0;
        int byteIndex = 0;
        int currentByte = 0b00000000;
        while (!treeShape.isEmpty()) {
            boolean bit = treeShape.poll();
            if (!bit) {
                currentByte = (currentByte | masks[bitIndex]);
            }
            bitIndex++; // go to the next index
            if (bitIndex == Byte.SIZE) { // if the index is 8, then we zero and add the byte to the array
                result[byteIndex] = (byte) currentByte;
                byteIndex++; // increasing the iteration for the array
                bitIndex = 0; // set the bit index to zero
                currentByte = 0b00000000; // zero the value of the current byte
            }
        }
        if (byteIndex < result.length) {
            result[byteIndex] = (byte) currentByte;
        }
        return result;
    }

    /**
     * we go through the original byte array and find the corresponding encrypted
     * value in the HashMap.
     * We go through this value and put the corresponding bit in the byte using the
     * array of masks
     *
     * @param buffer and source byte array
     * @param table  encrypted value table
     * @return byte array for the new file
     */
    private static byte[] getNewBytesArray(byte[] buffer, TreeMap<Byte, StringBuilder> table) {
        // create a new array
        ArrayList<Byte> newBytesArr = new ArrayList<>();

        StringBuilder currentEncryptedByte; // current encrypted byte
        int bitIndex = 0; // to understand when a byte is full
        int currentNewByte = 0b00000000; // to pack a new byte

        for (byte b : buffer) { // loop through the array of original bytes
            currentEncryptedByte = table.get(b); // current encrypted byte

            for (int i = 0; i < currentEncryptedByte.length(); i++) { // we go through the line
                char ch = currentEncryptedByte.charAt(i); // current character

                if (ch == '1') { // if 1, then put 1 in the byte by index using a mask
                    currentNewByte = (currentNewByte | masks[bitIndex]);
                }

                bitIndex++; // go to the next index

                if (bitIndex == Byte.SIZE) { // if the index is 8, then we zero and add the byte to the array
                    try {
                        newBytesArr.add((byte) currentNewByte);
                    } catch (OutOfMemoryError error) {
                        System.err.println(error + "\nincrease the heap size to solve this problem" +
                                "\n Go to 'Edit Configuration' -> 'Modify options', " +
                                "check the box next to 'Add VM options'\n" +
                                "and in the field that appears, enter, for example: '-Xmx4096m -Xms1024m'" +
                                " or '-Xmx8192m -Xms2048m' or '-Xmx16284m -Xms4096m'");
                        System.exit(-1);
                    }
                    bitIndex = 0; // set the bit index to zero
                    currentNewByte = 0b00000000; // zero the value of the current byte
                }

            }

        }
        if (bitIndex < Byte.SIZE && bitIndex > 0) { // if there is an incomplete byte left, then we supplement it with
                                                    // ones and put it in the array
            while (bitIndex < Byte.SIZE) {
                currentNewByte = (currentNewByte | masks[bitIndex]);
                bitIndex++;
            }
            newBytesArr.add((byte) currentNewByte);
        }
        byte[] result = new byte[newBytesArr.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = newBytesArr.get(i);
        }

        return result;
    }
}