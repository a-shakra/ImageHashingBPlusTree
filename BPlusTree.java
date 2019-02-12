import java.util.*;
import java.util.Map.Entry;


/**
 * BPlusTree Class Order D: D<=number of keys in a node <=2*D
 * Assumption: All keys are non-negative
 * Algorithms based on the book Database Management Systems
 * By Ramakrishnan and Gehrke
 * Contains the root node and the order of the tree
 * Page 344
 */
public class BPlusTree<K extends Comparable<K>, T> {

    public Node<K,T> root;
    public static final int D = 2;

    /**
     * Function returns all entries with the specified key. It handles duplicates but can't do range queries. Check function "range" below for range queries
     *  @param key
     *  @return LinkedList<T>
     */

     public LinkedList<T> search2(K key)
    { // search function of linked lists
        if(key == null || root == null) {
            return null;
        }
        // Look for leaf node that key is pointing to
        LeafNode<K,T> leaf = (LeafNode<K,T>)treeSearch(root, key); // leaf found at this point
        // Linear search for the proper entry
        for(int i=0; i<leaf.keys.size(); i++) {
            if(key.compareTo(leaf.keys.get(i)) == 0) {
                return leaf.storedData.get(i);
            }
        }
        return null;
    }

    /**
     * Counts nb of 1's in hashcode of file. This used to determine where to store the file in the B+ tree
     * @param x,y
     * @return int
     */

    /**
     * Range function that will return a List<LinkedList<T>>
     * @param min
     * @param max
     * @return LinkedList<T>
     */

    public List<LinkedList<T>>  range(K min,K max)
    { // search function of linked lists using the min value in a range function

        if(min == null || root == null) { // if tree is empty, return null
            return null;
        }
        // Look for leaf node that key is pointing to
        LeafNode<K,T> leaf = (LeafNode<K,T>)treeSearch(root, min); // found the min range
        List<LinkedList<T>> result = new ArrayList<>();

        // now sequentially go through leafs on key to find the max. if you reach end of keys and can't find max, move to next key
        // continue until max key is found or leaf.nextLeaf == null
        while (leaf.nextLeaf != null)
        {
            ListIterator<K> iterator = leaf.keys.listIterator();
            while (iterator.hasNext())
            {
                K compare = iterator.next();
                if (compare.compareTo(min) >= 0 && compare.compareTo(max) < 1)
                {
                    int index = iterator.previousIndex();
                    result.add(leaf.storedData.get(index));
                }
                if (compare.compareTo(max) > 1)
                {
                    return result;
                }
            }
            leaf = leaf.nextLeaf;
        }
        return result;
    }

    /**
     * Search function that will start at root and search recursively down the tree until it reaches leafnode with desired key
     * @param node
     * @param key
     * @return LinkedList<T>
     */

    private Node<K,T> treeSearch(Node<K,T> node, K key) {
        if(node.isLeafNode) {
            return node;
        }
        // else the node is index node
        else {
            IndexNode<K,T> index = (IndexNode<K,T>)node;
            // Two special cases; key < first key entry stored on node, and key > last key entry stored on node.
            // If not one or other, find proper location using linear search of all keys on node.
            // K < K1, return treeSearch(P0, K)
            if(key.compareTo(index.keys.get(0)) < 0) {
                return treeSearch(index.children.get(0), key);
            }
            // K >= Km, return treeSearch(Pm, K), m = #entries
            else if(key.compareTo(index.keys.get(node.keys.size()-1)) >= 0) {
                return treeSearch(index.children.get(index.children.size()-1), key);
            }
            // Find i such that Ki <= K < K(i+1), return treeSearch(Pi,K)
            else {
                for(int i=0; i<index.keys.size()-1; i++) {
                    if(key.compareTo(index.keys.get(i)) >= 0 && key.compareTo(index.keys.get(i+1)) < 0) {
                        return treeSearch(index.children.get(i+1), key);
                    }
                }
            }
            return null;
        }
    }

    /**
     * public interface function that inserts a node into the the tree
     *
     * @param key
     * @param value
     */

    public void insert(K key, T value) {
        LeafNode<K,T> newLeaf = new LeafNode<>(key, value);
        Entry<K, Node<K,T>> entry = new AbstractMap.SimpleEntry<>(key, newLeaf); // For convenience, created an entry that includes key and its corresponding node

        // Insert entry into subtree with root node pointer
        if(root == null || root.keys.size() == 0) { // This is necessary to avoid nullpointerexception
            root = entry.getValue();
        }

        // newChildEntry null initially, and null on return unless child is split
        Entry<K, Node<K,T>> newChildEntry = getChildEntry(root, entry, null); // will enter the node in the tree. will return a value if a split occurred

        if(newChildEntry == null) {
            return;
        } else { // in case root needs to be split
            IndexNode<K,T> newRoot = new IndexNode<K,T>(newChildEntry.getKey(), root,
                    newChildEntry.getValue());
            root = newRoot;
            return;
        }
    }

    /**
     * private interface function that inserts a node into the the tree
     * returns an entry corresponding to a newchildEntry that can be produced when a split occurs somewhere
     * along the tree
     * @param node
     * @param entry
     * @param newChildEntry
     * @return Entry<K,Node<K,T>>
     */


    private Entry<K, Node<K,T>> getChildEntry(Node<K,T> node, Entry<K, Node<K,T>> entry,
                                              Entry<K, Node<K,T>> newChildEntry) {
        if(!node.isLeafNode) {
            // Choose subtree, find i such that Ki <= entry's key value < J(i+1)
            IndexNode<K,T> index = (IndexNode<K,T>) node;
            int i = 0;
            while(i < index.keys.size()) {
                if(entry.getKey().compareTo(index.keys.get(i)) < 0) {
                    break;
                }
                i++;
            }
            // Recursively, insert entry
            newChildEntry = getChildEntry((Node<K,T>) index.children.get(i), entry, newChildEntry);

            // Usual case, didn't split child
            if(newChildEntry == null) {
                return null;
            }
            // Split child case, must insert newChildEntry in node
            else {
                int j = 0;
                while (j < index.keys.size()) {
                    if(newChildEntry.getKey().compareTo(index.keys.get(j)) < 0) {
                        break;
                    }
                    j++;
                }

                index.insertSorted(newChildEntry, j);

                // Usual case, put newChildEntry on it, set newChildEntry to null, return
                if(!index.isOverflowed()) {
                    return null;
                }
                else{
                    newChildEntry = splitIndexNode(index);

                    // Root was just split
                    if(index == root) {
                        // Create new node and make tree's root-node pointer point to newRoot
                        IndexNode<K,T> newRoot = new IndexNode<K,T>(newChildEntry.getKey(), root,
                                newChildEntry.getValue());
                        root = newRoot;
                        return null;
                    }
                    return newChildEntry;
                }
            }
        }
        // Node pointer is a leaf node
        else {
            LeafNode<K,T> leaf = (LeafNode<K,T>)node;
            LeafNode<K,T> newLeaf = (LeafNode<K,T>)entry.getValue();
            leaf.insertSorted(entry.getKey(), newLeaf.values.get(0));
            if(!leaf.isOverflowed()) {
                return null;
            }
            // Once in a while, the leaf is full
            else {
                newChildEntry = splitLeafNode(leaf);
                if(leaf == root) {
                    IndexNode<K,T> newRoot = new IndexNode<K,T>(newChildEntry.getKey(), leaf,
                            newChildEntry.getValue());
                    root = newRoot;
                    return null;
                }
                return newChildEntry;
            }
        }
    }

    /**
     * Splits a leaf node and return the new right node and the splitting
     * key as an Entry<splittingKey, RightNode>
     * @param leaf, any other relevant data
     * @return the key/node pair as an Entry
     */

    public Entry<K, Node<K,T>> splitLeafNode(LeafNode<K,T> leaf) {
        ArrayList<K> newKeys = new ArrayList<>();
        ArrayList<T> newValues = new ArrayList<>();
        ArrayList<LinkedList<T>> newLists = new ArrayList<>();

        // The rest D entries move to brand new node
        for(int i=D; i<=2*D; i++) {
            newKeys.add(leaf.keys.get(i));
            newValues.add(leaf.values.get(i));
            newLists.add(leaf.storedData.get(i));
        }

        // First D entries stay
        for(int i=D; i<=2*D; i++) {
            leaf.keys.remove(leaf.keys.size()-1);
            leaf.values.remove(leaf.values.size()-1);
            leaf.storedData.remove(leaf.storedData.size()-1);
        }

        K splitKey = newKeys.get(0);
        LeafNode<K,T> rightNode = new LeafNode<K,T>(newKeys, newValues,newLists); // Trouble will happen with the new keys for the linked list values... I think

        // Set sibling pointers
        LeafNode<K,T> tmp = leaf.nextLeaf;
        leaf.nextLeaf = rightNode;
        leaf.nextLeaf.previousLeaf = rightNode;
        rightNode.previousLeaf = leaf;
        rightNode.nextLeaf = tmp;

        Entry<K, Node<K,T>> newChildEntry = new AbstractMap.SimpleEntry<K, Node<K,T>>(splitKey, rightNode);

        return newChildEntry;
    }

    /**
     * Splits an indexNode and return the new right node and the splitting
     * key as an Entry<splittingKey, RightNode>
     *
     * @param index
     * @return new key/node pair as an Entry
     */

    public Entry<K, Node<K,T>> splitIndexNode(IndexNode<K,T> index) {
        ArrayList<K> newKeys = new ArrayList<K>();
        ArrayList<Node<K,T>> newChildren = new ArrayList<Node<K,T>>();

        // Note difference with splitting leaf page, 2D+1 key values and 2D+2 node pointers
        K splitKey = index.keys.get(D);
        index.keys.remove(D);

        // First D key values and D+1 node pointers stay
        // Last D keys and D+1 pointers move to new node
        newChildren.add(index.children.get(D+1));
        index.children.remove(D+1);

        while(index.keys.size() > D) {
            newKeys.add(index.keys.get(D));
            index.keys.remove(D);
            newChildren.add(index.children.get(D+1));
            index.children.remove(D+1);
        }

        IndexNode<K,T> rightNode = new IndexNode<K,T>(newKeys, newChildren);
        Entry<K, Node<K,T>> newChildEntry = new AbstractMap.SimpleEntry<K, Node<K,T>>(splitKey, rightNode);

        return newChildEntry;
    }
}
	