package com.shpp.p2p.cs.vstoianov.assignment15;

import java.util.LinkedList;
import java.util.TreeMap;

public class TreeStructure {

    private static LinkedList<Boolean> treeShape;
    private static LinkedList<Byte> treeLeaves;
    private static EncodingTreeNode tree;
    private static TreeMap<StringBuilder, Byte> encodedAndDecryptedTable;
    private static TreeMap<Byte, StringBuilder> decryptedAndEncodedTable;

    /**
     * constructor
     *
     * @param tree Huffman tree
     */
    public TreeStructure(EncodingTreeNode tree) {
        TreeStructure.tree = tree;
        LinkedList<Boolean> treeShape = new LinkedList<>();
        LinkedList<Byte> treeLeaves = new LinkedList<>();
        getQueuesForStructure(tree, treeShape, treeLeaves);
        TreeStructure.treeShape = treeShape;
        TreeStructure.treeLeaves = treeLeaves;
        TreeMap<StringBuilder, Byte> encodedAndDecrypted = new TreeMap<>();
        TreeMap<Byte, StringBuilder> decryptedAndEncoded = new TreeMap<>();
        createTable(encodedAndDecrypted, decryptedAndEncoded);
        encodedAndDecryptedTable = encodedAndDecrypted;
        decryptedAndEncodedTable = decryptedAndEncoded;
    }


    /**
     * constructor
     *
     * @param treeShape tree structure
     * @param treeLeaves tree leaves
     */
    public TreeStructure(LinkedList<Boolean> treeShape, LinkedList<Byte> treeLeaves) {
        TreeStructure.treeShape = new LinkedList<>(treeShape);
        TreeStructure.treeLeaves = new LinkedList<>(treeLeaves);
        treeShape.poll();
        tree = unflattenTree(new EncodingTreeNode(), treeShape, treeLeaves);
        TreeMap<StringBuilder, Byte> encodedAndDecrypted = new TreeMap<>();
        TreeMap<Byte, StringBuilder> decryptedAndEncoded = new TreeMap<>();
        createTable(encodedAndDecrypted, decryptedAndEncoded);
        encodedAndDecryptedTable = encodedAndDecrypted;
        decryptedAndEncodedTable = decryptedAndEncoded;
    }

    /**
     * creating a tree structure
     *
     * @param tree       - tree
     * @param treeShape  - tree structure
     * @param treeLeaves - tree leaves
     */
    private void getQueuesForStructure(EncodingTreeNode tree, LinkedList<Boolean> treeShape, LinkedList<Byte> treeLeaves) {
        if (tree.getValue() == null) {
            treeShape.add(false);
        } else {
            treeShape.add(true);
            treeLeaves.add(tree.getValue());
        }

        if (tree.getValue() == null) {
            getQueuesForStructure(tree.getLeft(), treeShape, treeLeaves);
            getQueuesForStructure(tree.getRight(), treeShape, treeLeaves);
        }
    }

    /**
     * restoring a tree from its structure
     *
     * @param tree       empty tree
     * @param treeShape  tree structure
     * @param treeLeaves tree leaves
     * @return Huffman tree
     */
    EncodingTreeNode unflattenTree(EncodingTreeNode tree, LinkedList<Boolean> treeShape, LinkedList<Byte> treeLeaves) {

        while (!treeShape.isEmpty()) {

            boolean leaf = treeShape.poll();
            if (!leaf) {
                if (tree.getLeft() == null) {
                    tree.setLeft(new EncodingTreeNode());
                    unflattenTree(tree.getLeft(), treeShape, treeLeaves);

                }
                if (tree.getRight() == null) {
                    tree.setRight(new EncodingTreeNode());
                    unflattenTree(tree.getRight(), treeShape, treeLeaves);

                }
                if (tree.getLeft() != null && tree.getRight() != null) {
                    return tree;
                }

            } else {
                if (tree.getLeft() == null) {
                    tree.setLeft(new EncodingTreeNode(treeLeaves.poll()));

                } else if (tree.getLeft() != null && tree.getRight() == null) {
                    tree.setRight(new EncodingTreeNode(treeLeaves.poll()));
                }
            }
        }
        return tree;
    }

    /**
     * populates TreeMaps with a table of encrypted data and their values
     * @param encodedAndDecrypted encrypted data and their meanings
     * @param decryptedAndEncoded values and encrypted data
     */
    private void createTable(TreeMap<StringBuilder, Byte> encodedAndDecrypted, TreeMap<Byte, StringBuilder> decryptedAndEncoded) {
        LinkedList<Byte> values = new LinkedList<>(treeLeaves);
        while (!values.isEmpty()) {
            byte value = values.poll();

            StringBuilder key = new StringBuilder(getKey(tree, value, ""));
            encodedAndDecrypted.put(key, value);
            decryptedAndEncoded.put(value, key);
        }
    }

    /**
     * looks for a value in the tree and returns its path
     * @param tree Huffman tree
     * @param value original byte
     * @param stringValue string to write the path
     * @return path to value in tree
     */
    private String getKey(EncodingTreeNode tree, byte value, String stringValue) {

        if (tree.getValue() != null && tree.getValue() == value) {
            return stringValue;
        } else {
            if (tree.getLeft() != null) {
                String zero = getKey(tree.getLeft(), value, stringValue + '0');
                if (zero != null) return zero;
            }
            if (tree.getRight() != null) {
                return getKey(tree.getRight(), value, stringValue + '1');
            }
        }
            return null;
    }


    /**
     * Getters
     */

    public static TreeMap<StringBuilder, Byte> getEncodedAndDecryptedTable() {
        return encodedAndDecryptedTable;
    }

    public static TreeMap<Byte, StringBuilder> getDecryptedAndEncodedTable() {
        return decryptedAndEncodedTable;
    }

    public LinkedList<Boolean> getTreeShape() {
        return treeShape;
    }

    public LinkedList<Byte> getTreeLeaves() {
        return treeLeaves;
    }

    public EncodingTreeNode getTree() {
        return tree;
    }


}
