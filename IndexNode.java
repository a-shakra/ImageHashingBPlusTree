import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * IndexNode is a class that is modeled after the index node in a B+ tree
 * Contains an array of objects of type Node that serve as children to a index node
 *
 * Method insertSorted is used to ensure that an entry and its children are in their proper place on index node
 * @author Anas Shakra
 */

public class IndexNode<K extends Comparable<K>, T> extends Node<K,T> {

    // m nodes
    protected ArrayList<Node<K,T>> children; // m+1 children

    /**
     * Default Constructor
     */

    public IndexNode(K key, Node<K,T> child0, Node<K,T> child1) {
        isLeafNode = false;
        keys = new ArrayList<K>();
        keys.add(key);
        children = new ArrayList<Node<K,T>>();
        children.add(child0);
        children.add(child1);
    }

    /**
     * Constructor: function is used when index node is split. Used to move lists of keys and children in bulk
     */

    public IndexNode(List<K> newKeys, List<Node<K,T>> newChildren) {
        isLeafNode = false;

        keys = new ArrayList<>(newKeys);
        children = new ArrayList<>(newChildren);

    }

    /**
     * insert the entry into this node at the specified index so that it still
     * remains sorted
     *
     * @param e
     * @param index
     */

    public void insertSorted(Entry<K, Node<K,T>> e, int index) {
        K key = e.getKey();
        Node<K,T> child = e.getValue();
        if (index >= keys.size()) {
            keys.add(key);
            children.add(child);
        } else {
            keys.add(index, key);
            children.add(index+1, child);
        }
    }

}