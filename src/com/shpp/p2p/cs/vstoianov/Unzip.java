package com.shpp.p2p.cs.vstoianov.assignment15;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.TreeMap;

public class Unzip {

    private static final int[] masks = { 0b10000000, 0b01000000, 0b00100000, 0b00010000, 0b00001000, 0b00000100,
            0b00000010, 0b00000001 };

    protected Unzip(String input, String output) {

        try (FileInputStream fis = new FileInputStream(input);
                BufferedInputStream bis = new BufferedInputStream(fis);
                FileOutputStream fos = new FileOutputStream(output)) {

            Date date = new Date();

            byte[] sizeTree = new byte[Short.SIZE / 8];
            bis.read(sizeTree);

            int size = convertInNum(sizeTree); // tree structure size
            int sizeStructureTree = (int) Math.ceil((double) size / 8);

            byte[] structureTree = new byte[sizeStructureTree];
            bis.read(structureTree);

            LinkedList<Boolean> treeShape = new LinkedList<>();
            // get the number of leaves and fill the treeShape
            int numberOfLeaf = getTreeShape(size, structureTree, treeShape);

            byte[] forTreeLeaves = new byte[numberOfLeaf];
            bis.read(forTreeLeaves);

            LinkedList<Byte> treeLeaves = new LinkedList<>();
            for (byte b : forTreeLeaves) {
                treeLeaves.add(b);
            }

            TreeStructure treeStructure = new TreeStructure(treeShape, treeLeaves);
            TreeMap<StringBuilder, Byte> table = treeStructure.getEncodedAndDecryptedTable();

            byte[] forSizeSourceFile = new byte[Long.SIZE / 8];
            bis.read(forSizeSourceFile);
            int sizeSourceFile = convertInNum(forSizeSourceFile); // source file size

            byte[] encodedData = new byte[bis.available()];
            bis.read(encodedData);

            byte[] newFile = getDecryption(table, sizeSourceFile, encodedData);
            fos.write(newFile);

            long zippedFileSize = sizeTree.length + structureTree.length + forTreeLeaves.length
                    + forSizeSourceFile.length + encodedData.length;
            System.out.println("Zipped file size        =  " + zippedFileSize + "  bytes");
            System.out.println("Source file size        =  " + newFile.length + "  bytes");
            double efficiencyZip = (double) (sizeSourceFile - zippedFileSize) / sizeSourceFile * 100;
            System.out.println("Compression efficiency  =  " + String.format("%.2f", efficiencyZip) + "  %");
            Date data1 = new Date();
            System.out.println("Unzip time  " + (double) (data1.getTime() - date.getTime()) / 1000 + "  seconds");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * decrypts encoded data
     *
     * @param table          table with bytes and their encoded values
     * @param sizeSourceFile size source file
     * @param encodedData    array with encoded data
     * @return decoded data
     */
    private byte[] getDecryption(TreeMap<StringBuilder, Byte> table, int sizeSourceFile, byte[] encodedData) {
        StringBuilder currentCode = new StringBuilder();

        byte[] result = new byte[sizeSourceFile];
        int iterationForBytes = 0;
        for (byte b : encodedData) {
            for (int i = 0; i < Byte.SIZE; i++) {
                if ((b & masks[i]) > 0)
                    currentCode.append(1);
                else
                    currentCode.append(0);
                if (table.containsKey(currentCode) && iterationForBytes < sizeSourceFile) {

                    result[iterationForBytes++] = table.get(currentCode);
                    currentCode.setLength(0);

                }
            }
        }
        return result;
    }

    /**
     * convert values from an array to a number
     *
     * @param sizeFile size file
     * @return long
     */
    private int convertInNum(byte[] sizeFile) {
        int mask = 0b11111111;
        int sizeBuffer = 0;
        for (int i = 0; i < sizeFile.length; i++) {
            sizeBuffer = sizeBuffer | sizeFile[i] & mask;
            if (i < sizeFile.length - 1)
                sizeBuffer <<= 8;
        }
        return sizeBuffer;
    }

    /**
     * get the number of leaves and fill the treeShape
     * 
     * @param size          tree structure size
     * @param structureTree tree structure in binary
     * @param treeShape     empty LinkedList for filling
     * @return number of leaves
     */
    private int getTreeShape(int size, byte[] structureTree, LinkedList<Boolean> treeShape) {
        int iterationTree = 0;
        boolean isLeaf;
        int countLeaf = 0;

        loop: for (byte b : structureTree) {
            for (int j = 0; j < Byte.SIZE; j++) {
                isLeaf = (masks[j] & b) < 1; // current bit
                if (isLeaf) {
                    treeShape.add(true);
                    countLeaf++;
                } else {
                    treeShape.add(false);
                }
                iterationTree++;
                if (iterationTree >= size) {
                    break loop;
                }
            }
        }
        return countLeaf;
    }
}
