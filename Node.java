import java.util.ArrayList;
/**
 * Base class to IndexNode and LeafNode
 * Contains variables common to both of these two node types.
 * Contains method isOverflowed which is needed to test if leaf should be split
 * Method isUnderflowed is never used because tree implementation doesn't support deletion
 * @author Anas Shakra
 */
public class Node<K extends Comparable<K>, T> { // Parent class that IndexNode and LeafNode inherit from
    protected boolean isLeafNode;
    protected ArrayList<K> keys;

    /**
     * Function that checks whether a node is overflowed.
     * This is critical for knowing whether a node needs to be split.
     */

    public boolean isOverflowed()
    {
        return keys.size() > 2 * BPlusTree.D;
    }
}