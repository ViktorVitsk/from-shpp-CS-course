package com.shpp.p2p.cs.vstoianov.assignment15;

import java.util.HashMap;
import java.util.PriorityQueue;

public class EncodingTreeNode implements Comparable<EncodingTreeNode> {

    private Byte value;
    private int weight;
    private EncodingTreeNode left;
    private EncodingTreeNode right;

    public EncodingTreeNode() {
        this.value = null;
        this.left = null;
        this.right = null;
    }

    /**
     * constructor
     *
     * @param value byte
     */
    public EncodingTreeNode(Byte value) {
        this.value = value;
    }

    /**
     * constructor
     *
     * @param value  byte
     * @param weight node weight
     */
    public EncodingTreeNode(Byte value, int weight) {
        this.value = value;
        this.weight = weight;
    }

    /**
     * constructor
     *
     * @param value  byte
     * @param weight node weight
     * @param left   left subtree
     * @param right  right subtree
     */
    public EncodingTreeNode(Byte value, int weight, EncodingTreeNode left, EncodingTreeNode right) {
        this.value = value;
        this.weight = weight;
        this.left = left;
        this.right = right;
    }

    /**
     * Create a Huffman tree.
     * We get a queue in which the EncodingTreeNodes are sorted by their weight in ascending order,
     * we take out 2 EncodingTreeNodes from the queue and write them to the new EncodingTreeNode, as left and right,
     * and write the sum of their weight in the weight of the created EncodingTreeNode
     *
     * @param bytes byte array files
     * @return constructed Huffman tree
     */
    public static EncodingTreeNode buildHuffmanTree(byte[] bytes) {

        HashMap<Byte, Integer> frequencyBytes = getFrequencyBytes(bytes); // calculate the byte frequency
        PriorityQueue<EncodingTreeNode> priorityQueue = getPriorityQueue(frequencyBytes); // create a priority queue for the leaves of the tree

        if (priorityQueue.size() < 2) {
            System.err.println("invalid file size. The file must contain at least 2 different bytes");
            System.exit(-1);
        }

        while (priorityQueue.size() > 1) {

            EncodingTreeNode left = priorityQueue.poll();
            EncodingTreeNode right = priorityQueue.poll();

            if (left.value == null && right.value != null) {
                EncodingTreeNode temp = left;
                left = right;
                right = temp;
            }

            EncodingTreeNode node = new EncodingTreeNode(null, left.weight + right.weight, left, right);
            priorityQueue.add(node);
        }
        return priorityQueue.poll();
    }

    /**
     * calculate how many times each byte in the array is repeated
     *
     * @param bytes array of bytes from the input file
     * @return Hashmap with bytes and their number of repetitions
     */
    private static HashMap<Byte, Integer> getFrequencyBytes(byte[] bytes) {
        HashMap<Byte, Integer> result = new HashMap<>();

        for (byte b : bytes) {
            if (result.containsKey(b)) result.put(b, result.get(b) + 1);
            else result.put(b, 1);
        }
        return result;
    }

    /**
     * create an EncodingTreeNode, where value is a byte, and weight is the number of times this byte is repeated in the file
     *and put each created EncodingTreeNode into the priority queue
     *
     * @param frequencyBytes hashmap in which the key is a byte, and the value is the number of times this byte is repeated in the file
     * @return a queue of bytes in ascending order by weight
     */
    private static PriorityQueue<EncodingTreeNode> getPriorityQueue(HashMap<Byte, Integer> frequencyBytes) {
        PriorityQueue<EncodingTreeNode> result = new PriorityQueue<>();

        for (byte b : frequencyBytes.keySet()) {
            EncodingTreeNode current = new EncodingTreeNode(b, frequencyBytes.get(b));
            result.add(current);
        }
        return result;
    }

    /**
     * Getters
     */
    public Byte getValue() {
        return value;
    }

    public EncodingTreeNode getLeft() {
        return left;
    }

    public EncodingTreeNode getRight() {
        return right;
    }

    /**
     * Setters
     */
    public void setValue(Byte value) {
        this.value = value;
    }

    public void setLeft(EncodingTreeNode left) {
        this.left = left;
    }

    public void setRight(EncodingTreeNode right) {
        this.right = right;
    }

    /**
     * to string
     */
    @Override
    public String toString() {
        return "{\n" +
                " value = " + value +
                ", weight = " + weight +
                ", left = " + left +
                ", right = " + right +
                "}\n";
    }

    /**
     * priority queue sorting
     *
     * @param o - wood
     * @return queue in ascending order
     */
    @Override
    public int compareTo(EncodingTreeNode o) {
        return Integer.compare(this.weight, o.weight);
    }
}
