import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileWriter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Starter test suite for Project 3 (Huffman Encoding/Decoding).
 * Write more tests!
 * These tests assume the test files are in the same directory as HuffmanTest.java
 */
public class HuffmanTest {

    String filePath = "src/test/java/";

    @Test
    public void testCntFreq() throws IOException {
        int[] freqList = Huffman.cntFreq(filePath + "file2.txt");
        int[] ansList = {2, 4, 8, 16, 0, 2, 0}; // counts for ascii 97..103
        for (int i = 0; i < ansList.length; i++) {
            assertEquals(ansList[i], freqList[97 + i], "mismatch at ascii " + (97 + i));
        }
    }

    @Test
    public void testComesBefore() {
        HuffmanLeaf d = new HuffmanLeaf(5, 'd');
        HuffmanLeaf k = new HuffmanLeaf(5, 'k');
        assertTrue(d.comesBefore(k));
        assertFalse(k.comesBefore(d));
    }

    @Test
    public void testCreateHeader() throws IOException {
        int[] freqList = Huffman.cntFreq(filePath + "file2.txt");
        assertEquals("97 2 98 4 99 8 100 16 102 2", Huffman.createHeader(freqList));
    }

    @Test
    public void test01Textfile() throws IOException {
        Huffman.huffmanEncode(filePath + "file1.txt", filePath + "file1_out.txt");
        assertTrue(compareFiles(filePath + "file1_out.txt", filePath + "file1_soln.txt"));
    }

    /**
     * Compares two files line by line, trimming trailing CR/LF so the
     * comparison works the same on Windows and Unix-style line endings.
     */
    private static boolean compareFiles(String file1, String file2) throws IOException {
        try (BufferedReader r1 = new BufferedReader(new FileReader(file1));
             BufferedReader r2 = new BufferedReader(new FileReader(file2))) {
            String line1, line2;
            while (true) {
                line1 = r1.readLine();
                line2 = r2.readLine();
                if (line1 == null && line2 == null) {
                    return true;
                }
                if (line1 == null || line2 == null) {
                    return false;
                }
                if (!line1.trim().equals(line2.trim())) {
                    return false;
                }
            }
        }
    }

////////////////////
    @Test
    void comesBefore() {
        HuffmanTree z = new HuffmanLeaf(1, 'z');
        HuffmanTree a = new HuffmanLeaf(2, 'a');

        assertTrue(z.comesBefore(a));
        assertFalse(a.comesBefore(z));

        HuffmanTree sameFreqA = new HuffmanLeaf(2, 'a');
        HuffmanTree sameFreqZ = new HuffmanLeaf(2, 'z');

        assertTrue(sameFreqA.comesBefore(sameFreqZ));
        assertFalse(sameFreqZ.comesBefore(sameFreqA));
    }

    @Test
    void combine() {
        HuffmanTree z = new HuffmanLeaf(1, 'z');
        HuffmanTree a = new HuffmanLeaf(2, 'a');

        assertEquals(
                new HuffmanNode('a', 3, z, a),
                z.combine(a)
        );

        assertEquals(
                new HuffmanNode('a', 3, z, a),
                a.combine(z)
        );
    }

    @Test
    void compareTo() {
        HuffmanTree low = new HuffmanLeaf(1, 'z');
        HuffmanTree high = new HuffmanLeaf(2, 'a');

        assertTrue(low.compareTo(high) < 0);
        assertTrue(high.compareTo(low) > 0);
        assertEquals(0, low.compareTo(low));
    }

    @Test
    void orderedQueueEnqueueDequeue() {
        OrderedQueue queue = new OrderedQueue(new ArrayList<>());

        HuffmanTree c = new HuffmanLeaf(3, 'c');
        HuffmanTree a = new HuffmanLeaf(1, 'a');
        HuffmanTree b = new HuffmanLeaf(2, 'b');

        queue.enqueue(c);
        queue.enqueue(a);
        queue.enqueue(b);

        assertEquals(3, queue.count());

        assertEquals(a, queue.dequeue());
        assertEquals(b, queue.dequeue());
        assertEquals(c, queue.dequeue());

        assertTrue(queue.isEmpty());
    }

    @Test
    void orderedQueueAsciiTies() {
        OrderedQueue queue = new OrderedQueue(new ArrayList<>());

        HuffmanTree z = new HuffmanLeaf(2, 'z');
        HuffmanTree a = new HuffmanLeaf(2, 'a');

        queue.enqueue(z);
        queue.enqueue(a);

        assertEquals(a, queue.dequeue());
        assertEquals(z, queue.dequeue());
    }

    @Test
    void orderedQueueDequeueEmptyQueue() {
        OrderedQueue queue = new OrderedQueue(new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> queue.dequeue());
    }

    @Test
    void createHuffTreeEmpty() {
        int[] freq = new int[Huffman.NUM_CHARS];

        assertNull(Huffman.createHuffTree(freq));
    }

    @Test
    void createHuffTreeOneChar() {
        int[] freq = new int[Huffman.NUM_CHARS];
        freq['a'] = 5;

        assertEquals(
                new HuffmanLeaf(5, 'a'),
                Huffman.createHuffTree(freq)
        );
    }

    @Test
    void createHuffTreeMultipleChars() {
        int[] freq = new int[Huffman.NUM_CHARS];
        freq['a'] = 5;
        freq['b'] = 2;
        freq['c'] = 1;

        assertEquals(
                new HuffmanNode(
                        'a',
                        8,
                        new HuffmanNode(
                                'b',
                                3,
                                new HuffmanLeaf(1, 'c'),
                                new HuffmanLeaf(2, 'b')
                        ),
                        new HuffmanLeaf(5, 'a')
                ),
                Huffman.createHuffTree(freq)
        );
    }

    @Test
    void createCodeMultipleChars() {
        int[] freq = new int[Huffman.NUM_CHARS];
        freq['a'] = 5;
        freq['b'] = 2;
        freq['c'] = 1;

        HuffmanTree tree = Huffman.createHuffTree(freq);
        String[] codes = Huffman.createCode(tree);

        assertEquals("1", codes['a']);
        assertEquals("01", codes['b']);
        assertEquals("00", codes['c']);
        assertEquals("", codes['z']);
    }

    @Test
    void createCodeOneChar() {
        HuffmanTree tree = new HuffmanLeaf(5, 'a');
        String[] codes = Huffman.createCode(tree);

        assertEquals("", codes['a']);
    }

    @Test
    void createCodeEmptyTree() {
        String[] codes = Huffman.createCode(null);

        assertEquals("", codes['a']);
        assertEquals("", codes['z']);
    }

    @Test
    void cntFreq() throws IOException {
        int[] freq = Huffman.cntFreq(filePath + "file1.txt");

        assertEquals(4, freq['a']);
        assertEquals(3, freq[' ']);
        assertEquals(3, freq['b']);
        assertEquals(2, freq['c']);
        assertEquals(1, freq['d']);
    }

    @Test
    void parseHeader() {
        int[] freq = Huffman.parseHeader("97 3 98 4 99 2");

        assertEquals(3, freq['a']);
        assertEquals(4, freq['b']);
        assertEquals(2, freq['c']);
        assertEquals(0, freq['z']);
    }

    @Test
    void parseHeaderEmpty() {
        int[] freq = Huffman.parseHeader("");

        assertEquals(0, freq['a']);
        assertEquals(0, freq['z']);
    }

    @Test
    public void testDecodeFile1() throws IOException {
        Huffman.huffmanDecode(
                filePath + "file1_soln.txt",
                filePath + "file1_decoded.txt"
        );

        assertTrue(compareFiles(
                filePath + "file1.txt",
                filePath + "file1_decoded.txt"
        ));
    }

    @Test
    void leafRecordAccessors() {
        HuffmanLeaf leaf = new HuffmanLeaf(7, 'q');

        assertEquals(7, leaf.freq());
        assertEquals('q', leaf.charAscii());
    }

    @Test
    void nodeRecordAccessors() {
        HuffmanTree left = new HuffmanLeaf(1, 'a');
        HuffmanTree right = new HuffmanLeaf(2, 'b');

        HuffmanNode node = new HuffmanNode('a', 3, left, right);

        assertEquals('a', node.charASCII());
        assertEquals(3, node.freq());
        assertEquals(left, node.left());
        assertEquals(right, node.right());
    }

    @Test
    void leafAndNodeOperations() {
        HuffmanTree leaf = new HuffmanLeaf(1, 'z');

        HuffmanTree node = new HuffmanNode(
                'a',
                2,
                new HuffmanLeaf(1, 'a'),
                new HuffmanLeaf(1, 'b')
        );

        assertTrue(leaf.comesBefore(node));
        assertFalse(node.comesBefore(leaf));

        assertEquals(
                new HuffmanNode('a', 3, leaf, node),
                leaf.combine(node)
        );

        assertEquals(
                new HuffmanNode('a', 3, leaf, node),
                node.combine(leaf)
        );
    }

    @Test
    void nodeAndNodeOperations() {
        HuffmanTree nodeA = new HuffmanNode(
                'a',
                2,
                new HuffmanLeaf(1, 'a'),
                new HuffmanLeaf(1, 'b')
        );

        HuffmanTree nodeZ = new HuffmanNode(
                'z',
                2,
                new HuffmanLeaf(1, 'y'),
                new HuffmanLeaf(1, 'z')
        );

        assertTrue(nodeA.comesBefore(nodeZ));
        assertFalse(nodeZ.comesBefore(nodeA));

        assertTrue(nodeA.compareTo(nodeZ) < 0);
        assertTrue(nodeZ.compareTo(nodeA) > 0);
        assertEquals(0, nodeA.compareTo(nodeA));

        assertEquals(
                new HuffmanNode('a', 4, nodeA, nodeZ),
                nodeA.combine(nodeZ)
        );

        assertEquals(
                new HuffmanNode('a', 4, nodeA, nodeZ),
                nodeZ.combine(nodeA)
        );
    }

    @Test
    void createHeaderEmpty() {
        int[] freq = new int[Huffman.NUM_CHARS];

        assertEquals("", Huffman.createHeader(freq));
    }

    @Test
    void encodeDecodeEmptyFile() throws IOException {
        File input = File.createTempFile("empty-input", ".txt");
        File encoded = File.createTempFile("empty-encoded", ".txt");
        File decoded = File.createTempFile("empty-decoded", ".txt");

        input.deleteOnExit();
        encoded.deleteOnExit();
        decoded.deleteOnExit();

        // input starts empty because createTempFile makes an empty file
        Huffman.huffmanEncode(input.getPath(), encoded.getPath());

        // Empty input should produce an empty encoded file.
        assertTrue(compareFiles(input.getPath(), encoded.getPath()));

        Huffman.huffmanDecode(encoded.getPath(), decoded.getPath());

        // Decoding an empty encoded file should produce an empty output file.
        assertTrue(compareFiles(input.getPath(), decoded.getPath()));
    }

    @Test
    void encodeDecodeOneCharacterFile() throws IOException {
        File input = File.createTempFile("single-input", ".txt");
        File encoded = File.createTempFile("single-encoded", ".txt");
        File decoded = File.createTempFile("single-decoded", ".txt");

        input.deleteOnExit();
        encoded.deleteOnExit();
        decoded.deleteOnExit();

        FileWriter writer = new FileWriter(input);
        writer.write("aaaaa");
        writer.close();

        Huffman.huffmanEncode(input.getPath(), encoded.getPath());

        // A single-character input has only a header, not a bit-code line.
        BufferedReader reader = new BufferedReader(new FileReader(encoded));

        assertEquals("97 5", reader.readLine());
        assertNull(reader.readLine());

        reader.close();

        Huffman.huffmanDecode(encoded.getPath(), decoded.getPath());

        // The decoded result must match the original five a characters.
        assertTrue(compareFiles(input.getPath(), decoded.getPath()));
    }

    @Test
    void decodeBlankHeader() throws IOException {
        File encoded = File.createTempFile("blank-header", ".txt");
        File decoded = File.createTempFile("blank-decoded", ".txt");

        encoded.deleteOnExit();
        decoded.deleteOnExit();

        // One newline means the first line exists, but the header is empty.
        FileWriter writer = new FileWriter(encoded);
        writer.write("\n");
        writer.close();

        Huffman.huffmanDecode(encoded.getPath(), decoded.getPath());

        BufferedReader reader = new BufferedReader(new FileReader(decoded));

        assertNull(reader.readLine());

        reader.close();
    }
    
    @Test
    public void testEncodeFile2() throws IOException {
        Huffman.huffmanEncode(
                filePath + "file2.txt",
                filePath + "file2_out.txt"
        );

        assertTrue(compareFiles(
                filePath + "file2_out.txt",
                filePath + "file2_soln.txt"
        ));
    }

    @Test
    public void testDecodeFile2() throws IOException {
        Huffman.huffmanDecode(
                filePath + "file2_soln.txt",
                filePath + "file2_decoded.txt"
        );

        assertTrue(compareFiles(
                filePath + "file2.txt",
                filePath + "file2_decoded.txt"
        ));
    }

    @Test
    public void testEncodeMultiline() throws IOException {
        Huffman.huffmanEncode(
                filePath + "multiline.txt",
                filePath + "multiline_out.txt"
        );

        assertTrue(compareFiles(
                filePath + "multiline_out.txt",
                filePath + "multiline_soln.txt"
        ));
    }

    @Test
    public void testDecodeMultiline() throws IOException {
        Huffman.huffmanDecode(
                filePath + "multiline_soln.txt",
                filePath + "multiline_decoded.txt"
        );

        assertTrue(compareFiles(
                filePath + "multiline.txt",
                filePath + "multiline_decoded.txt"
        ));
    }













}
