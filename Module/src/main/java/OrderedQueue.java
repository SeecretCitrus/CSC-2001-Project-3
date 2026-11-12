import java.util.ArrayList;
public record OrderedQueue(ArrayList<HuffmanTree> trees) implements HQueue{
    /**
     * return true if the queue is empty
     */
    public boolean isEmpty() {
        return trees.size() == 0;
    }

    /**
     * return a count of the number of elements in the queue
     */
    public int count() {
        return trees.size();
    }
    /**
     * enqueue a tree
     */
    public void enqueue(HuffmanTree tree) {
        int index = 0;

        while (index < trees.size() && trees.get(index).comesBefore(tree)) {
            index++;
        }//when first tree with lower priority is found, takes index of tree

        trees.add(index, tree);//inserts tree into appropriate spot in list and shifts other elements
    }

    /**
     * dequeue and return the highest-priority tree
     */
    public HuffmanTree dequeue() {
        if (isEmpty()) {
            throw new IllegalArgumentException("cannot dequeue an empty queue");
        }

        HuffmanTree front = trees.get(0);
        trees.remove(0);//removes the first element and scoots the rest forward
        return front;
    }

}
