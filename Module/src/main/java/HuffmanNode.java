/**
 * A single node in a Huffman tree, implemented as a record.
 *
 * A HuffmanNode represents either a leaf (an actual character together
 * with its occurrence count) or an internal node created by combining two
 * subtrees during tree construction.
 *
 * Records are immutable and give you charAscii(), freq(), left(), and
 * right() accessor methods automatically, along with
 * equals()/hashCode()/toString(). Do not rename the components below.
 */
public record HuffmanNode(int charASCII, int freq, HuffmanTree left, HuffmanTree right)
        implements HuffmanTree{

    //returns true if tree a comes before tree b
     public boolean comesBefore(HuffmanTree other) {
         switch (other) {
             case HuffmanLeaf(int otherF, char otherChar):
                 return this.freq < otherF || (this.freq == otherF && this.charASCII() < otherChar);
             case HuffmanNode(int otherChar, int otherF, HuffmanTree l, HuffmanTree r):
                 return this.freq < otherF || (this.freq == otherF && this.charASCII() < otherChar);
             default:
                 throw new IllegalArgumentException("comesBefore attempted on non HuffmanNode or HuffmanLeaf");
         }
     }

     //creates and returns a new internal Huffman node with a and b as its two children
    public HuffmanTree combine(HuffmanTree other) {
        switch (other) {
            case HuffmanLeaf(int otherF, char otherChar):
                if (comesBefore(other)) {
                    if (this.charASCII() < otherChar) {
                        return new HuffmanNode(this.charASCII(), (this.freq() + otherF), this, other);
                    } else {
                        return new HuffmanNode(otherChar, (this.freq() + otherF), this, other);
                    }
                } else {
                    if (this.charASCII() < otherChar) {
                        return new HuffmanNode(this.charASCII(), (this.freq() + otherF), other, this);
                    } else {
                        return new HuffmanNode(otherChar, (this.freq() + otherF), other, this);
                    }
                }

            case HuffmanNode(int otherChar, int otherF, HuffmanTree l, HuffmanTree r):
                if (comesBefore(other)) {
                    if (this.charASCII() < otherChar) {
                        return new HuffmanNode(this.charASCII(), (this.freq() + otherF), this, other);
                    } else {
                        return new HuffmanNode(otherChar, (this.freq() + otherF), this, other);
                    }
                } else {
                    if (this.charASCII() < otherChar) {
                        return new HuffmanNode(this.charASCII(), (this.freq() + otherF), other, this);
                    } else {
                        return new HuffmanNode(otherChar, (this.freq() + otherF), other, this);
                    }
                }

            default:
                throw new IllegalArgumentException("combine attempted on non HuffmanNode or HuffmanLeaf");
        }
    }

    /**
     * Natural ordering consistent with comesBefore(), so that HuffmanNode
     * objects can be stored directly in a sorted structure (e.g. a
     * PriorityQueue or a sorted List) while building the tree.
     */
    @Override
    public int compareTo(HuffmanTree other) {
        if (this.comesBefore(other)) return -1;
        if (other.comesBefore(this)) return 1;
        return 0;
    }
}
