import java.io.*;
import java.util.*;

/**
 * Project 3: Huffman Encoding / Decoding.
 *
 * Implement the methods below to compress and decompress text files using
 * Huffman coding. See the assignment write-up for the full specification.
 *
 *  - Treat characters as 8-bit values in the range [0, 255].
 *  - When writing output files, write the newline character explicitly as
 *    '\n' (do not rely on System.lineSeparator() / println()), so your
 *    output is identical across Windows/Mac/Linux. A BufferedWriter over
 *    a FileWriter, writing "\n" yourself, works well for this.
 *  - Encoded files store the code as literal '0'/'1' characters (one byte
 *    each), not as packed bits, so the "encoded" file will actually be
 *    larger than the original -- that's expected for this assignment.
 *  - createHuffTree() is built on top of addNode()/removeMin(), which
 *    operate generically over a java.util.Collection<HuffmanNode>.
 *  - HuffmanNode is a record, so its components are read with
 *    node.charAscii(), node.freq(), node.left(), and node.right() -- no
 *    "get" prefix.
 */
public class Huffman {

    /** Number of possible 8-bit character values. */
    public static final int NUM_CHARS = 256;

    /**
     * Opens the text file named filename and counts the frequency of
     * occurrence of every character in it.
     * Returns an int array of size NUM_CHARS, indexed by ASCII value, with
     * the count of each character (0 for characters that do not occur).
     */
    public static int[] cntFreq(String filename) throws IOException {
        int[] freq = new int[NUM_CHARS];

        FileReader reader = new FileReader(filename);//opens file for reading

        int ascii = reader.read();//reads and returns ascii value of first character and moves onto the next
        //ascii will be -1 if there aren't any characters
        while (ascii != -1) {//only runs while there are characters left
            freq[ascii]++;
            ascii = reader.read();
        }

        reader.close();//closes the file after reading is complete

        return freq;
    }


    /**
     * Builds a Huffman tree from an array of character frequencies (as
     * produced by cntFreq()) and returns the root HuffmanNode of the tree.
     *
     * Use an HQueue to manage the collection of
     * not-yet-combined nodes as you build the tree.
     *
     * Special cases:
     *  - If freq contains no non-zero counts (empty input file), return null
     *  - If freq contains exactly one non-zero count (input file has only
     *    one unique character), return a single leaf HuffmanNode for that
     *    character (no combine() call needed).
     */
    public static HuffmanTree createHuffTree(int[] freq) {//edited from HuffmanNode to HuffmanTree return type
        HQueue queue = new OrderedQueue(new ArrayList<>());//will be used to make list to turn into tree

        for (int ascii = 0; ascii < NUM_CHARS; ascii++) {//loops through each character
            if (freq[ascii] > 0) {//only makes a leaf if the character is actually used
                queue.enqueue(new HuffmanLeaf(freq[ascii], (char) ascii));
            }
        }

        if (queue.isEmpty()) {//no characters were used
            return null;
        }

        while (queue.count() > 1) {//combines the two smallest trees until only 1 tree remains
            HuffmanTree first = queue.dequeue();
            HuffmanTree second = queue.dequeue();

            HuffmanTree combined = first.combine(second);

            queue.enqueue(combined);//puts the combined tree back into the list
        }

        return queue.dequeue();//returns the combined tree
    }

    /**
     * Traverses the Huffman tree rooted at root and builds the Huffman
     * code for every character that appears in it. Going left appends
     * '0' to the code, going right appends '1'.
     *
     * Returns a String array of size NUM_CHARS, indexed by ASCII value.
     * Characters that do not appear in the tree have an empty string ""
     * at their index. The same is true of the lone character in a
     * single-node tree (the only-one-unique-character case) - it has no
     * left/right branch to build a code out of; huffmanEncode/huffmanDecode
     * handle that case entirely through the header.
     */
    public static String[] createCode(HuffmanTree root) {
        String[] codes = new String[NUM_CHARS];
        Arrays.fill(codes, "");

        addCodes(root, "", codes);//adds all the codes to the characters

        return codes;
    }

    //adds huffman code for each leaf into appropriate array spot (index = Ascii)
    private static void addCodes(HuffmanTree tree, String code, String[] codeArray) {
        switch (tree) {
            case null:
                return;
            case HuffmanLeaf(int f, char c):
                codeArray[c] = code;//adds completed code to the character's index in array
                return;
            case HuffmanNode(int charASCII, int f, HuffmanTree l, HuffmanTree r):
                addCodes(l, code + "0", codeArray);//adds 0s to each letter on the left branch of tree
                addCodes(r, code + "1", codeArray);//adds 1s to each letter on the right branch of tree
                return;
            default:
                throw new IllegalArgumentException("tried to addCodes on non HuffmanTree");
        }
    }

    /**
     * Given a frequency array (as produced by cntFreq()), returns a
     * header string listing the ASCII values and their associated
     * frequencies, separated by single spaces, in ascending order of
     * ASCII value. Only characters with a non-zero count are included.
     *
     * Example: for the text "aaabbbbcc" this returns "97 3 98 4 99 2".
     */
    public static String createHeader(int[] freq) {
        String header = "";

        for (int i = 0; i < NUM_CHARS; i++) {//loops through the frequency array
            if (freq[i] > 0) {//for every charachter actually used
                header += " " + i + " " + freq[i];// adds " [Ascii] [freq]" to array
            }
        }

        if (header.equals("")) {//if .txt is empty, returns an empty header
            return "";
        }

        return header.substring(1);//returns header (minus the beginning space)
    }

    /**
     * Reads the input text file inFile and writes to outFile:
     *   1. A header line (see createHeader), followed by a newline
     *   2. The Huffman-encoded contents of inFile, using the codes from
     *      createCode() -- UNLESS inFile has only one unique character,
     *      see below.
     * If outFile already exists, its old contents are overwritten.
     *
     * Special cases:
     *  - Empty input file: outFile should end up empty too
     *  - Input file with only one unique character: should contain only the
     *    header line (e.g. "97 5") and nothing else - no second line of code bits.
     */
    public static void huffmanEncode(String inFile, String outFile) throws IOException {
        int[] freq = cntFreq(inFile);//makes a frequency array from inFile
        HuffmanTree root = createHuffTree(freq);//makes a huffman tree from the array

        FileWriter writer = new FileWriter(outFile);//opens output file to be written to

        if (root == null) {//if input file is empty, don't write anything
            writer.close();
            return;
        }

        writer.write(createHeader(freq) + "\n");//makes a header and puts it into the write file

        if (root instanceof HuffmanLeaf) {//if input file only uses one character, ends without huffman code
            writer.close();
            return;
        }

        String[] codes = createCode(root); //makes an array with the codes for characters

        FileReader reader = new FileReader(inFile);
        int ascii = reader.read();//reads first character and moves onto the next

        while (ascii != -1) {//runs until there are no characters left
            writer.write(codes[ascii]);//appends each character's huffman code to txt
            ascii = reader.read();//reads next character
        }

        reader.close();
        writer.close();
    }

    /**
     * Parses a header string (in the format produced by createHeader) and
     * returns the corresponding frequency array of size NUM_CHARS, indexed
     * by ASCII value, i.e. the same format returned by cntFreq().
     *
     * Example: for the header "97 3 98 4 99 2", freq[97, 98, 99] == {3, 4, 2}.
     */
    public static int[] parseHeader(String headerString) {
        int[] freq = new int[NUM_CHARS];

        if (headerString.trim().equals("")) {//removes the spaces and checks if header [and input file] is empty
            return freq;
        }

        String[] parts = headerString.trim().split("\\s+");// Splits numbers in header into array
        //parts[even] = ascii value
        //parts[odd] = frequency

        for (int i = 0; i < parts.length; i += 2) {
            int ascii = Integer.parseInt(parts[i]);//turns the current index (ascii) into int from String
            int count = Integer.parseInt(parts[i + 1]);//frequency turned into int from String

            freq[ascii] = count;
        }

        return freq;
    }

    /**
     * Reads an encoded file (in the format written by huffmanEncode) and
     * writes the decoded text to decodeFile.
     *
     * Recreate the frequency array from the header with parseHeader(),
     * rebuild the Huffman tree with createHuffTree(), then walk the tree
     * for each bit of the second line: '0' -> left child, '1' -> right
     * child. When a leaf is reached, emit its character and restart at
     * the root.
     *
     * Special case: if the recreated tree is a single leaf (the
     * only-one-unique-character case), there is no second line to read
     * The header's count alone tells you how many times that character occurred,
     * so write it out that many times directly.
     */
    public static void huffmanDecode(String encodedFile, String decodeFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(encodedFile));//reads the whole header line at a time
        FileWriter writer = new FileWriter(decodeFile);//sets the decodeFile as to be written to
        String header = reader.readLine();//gets the first line (header)

        if (header == null) {//if first line is empty
            reader.close();
            writer.close();
            return;
        }

        int[] freq = parseHeader(header);//remakes a frequency array
        HuffmanTree root = createHuffTree(freq);//makes a huffman tree out of the array

        if (root == null) {//if header was empty
            reader.close();
            writer.close();
            return;
        }

        if(root instanceof HuffmanLeaf) {//file only has one type of character
            HuffmanLeaf leaf = (HuffmanLeaf) root;

            for (int i = 0; i < leaf.freq(); i++) {
                writer.write(leaf.charAscii());
            }

            reader.close();
            writer.close();
            return;
        }

        String bits = reader.readLine();//reads the bit (encoded) line
        HuffmanTree current = root;

        for (int i = 0; i < bits.length(); i++) {
            char bit = bits.charAt(i);

            HuffmanNode node = (HuffmanNode) current;

            if (bit == '0') {
                current = node.left();
            } else if (bit == '1') {
                current = node.right();
            } else {
                throw new IllegalArgumentException("encoded file contains a non-bit character");
            }

            if (current instanceof HuffmanLeaf) {
                HuffmanLeaf leaf = (HuffmanLeaf) current;

                writer.write(leaf.charAscii());

                current = root;//start back at the root to decode next character
            }
        }

        reader.close();
        writer.close();
    }
}
