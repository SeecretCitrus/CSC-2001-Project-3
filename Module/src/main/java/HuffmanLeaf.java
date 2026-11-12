public record HuffmanLeaf (int freq, char charAscii) implements HuffmanTree{

    @Override
    public boolean comesBefore(HuffmanTree other) {
        switch (other) {
            case HuffmanLeaf(int otherF, char otherChar):
                return this.freq < otherF || (this.freq == otherF && this.charAscii() < otherChar);
            case HuffmanNode(int otherChar, int otherF, HuffmanTree l, HuffmanTree r):
                return this.freq < otherF || (this.freq == otherF && this.charAscii() < otherChar);
            default:
                throw new IllegalArgumentException("comesBefore attempted on non HuffmanNode or HuffmanLeaf");
        }
    }

    @Override
    public HuffmanTree combine(HuffmanTree other) {
        switch (other) {
            case HuffmanLeaf(int otherF, char otherChar):
                if (comesBefore(other)) {
                    if (this.charAscii() < otherChar) {
                        return new HuffmanNode(this.charAscii(), (this.freq() + otherF), this, other);
                    } else {
                        return new HuffmanNode(otherChar, (this.freq() + otherF), this, other);
                    }
                } else {
                    if (this.charAscii() < otherChar) {
                        return new HuffmanNode(this.charAscii(), (this.freq() + otherF), other, this);
                    } else {
                        return new HuffmanNode(otherChar, (this.freq() + otherF), other, this);
                    }
                }

            case HuffmanNode(int otherChar, int otherF, HuffmanTree l, HuffmanTree r):
                if (comesBefore(other)) {
                    if (this.charAscii() < otherChar) {
                        return new HuffmanNode(this.charAscii(), (this.freq() + otherF), this, other);
                    } else {
                        return new HuffmanNode(otherChar, (this.freq() + otherF), this, other);
                    }
                } else {
                    if (this.charAscii() < otherChar) {
                        return new HuffmanNode(this.charAscii(), (this.freq() + otherF), other, this);
                    } else {
                        return new HuffmanNode(otherChar, (this.freq() + otherF), other, this);
                    }
                }

            default:
                throw new IllegalArgumentException("combine attempted on non HuffmanNode or HuffmanLeaf");
        }
    }

    @Override
    public int compareTo(HuffmanTree other) {
        if (this.comesBefore(other)) return -1;
        if (other.comesBefore(this)) return 1;
        return 0;
    }
}
