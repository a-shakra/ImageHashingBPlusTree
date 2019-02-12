import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
/**
 * LeafNode class is modeled after the leaf node in a B+ tree, including a pointer to its adjacent leaves.
 * Contains its own array of type T object and Linked lists of type T objects
 * The array of type T object known as a the values array can be removed
 * However, it is a legacy code that is deeply ingrained in the program so removing it at this stage will produce a lot of buys
 * As a result, this has been avoided.
 *
 * Method insertSorted is used to ensure that data entries are stored in proper place on leaf
 * @author Anas Shakra
 */
public class LeafNode<K extends Comparable<K>, T> extends Node<K, T> {
    protected ArrayList<T> values; // never used, relic of previous iteration but it is deeply ingrained in code so difficult to change
    protected LeafNode<K,T> nextLeaf;
    protected LeafNode<K,T> previousLeaf;
    public ArrayList<LinkedList<T>> storedData;

    /**
     * Constructor: Used once for the root. It's there so that that nullexception doesn't occur when the insert function in BPlusTree is called
     * @param firstKey,firstValue
     */

    public LeafNode(K firstKey, T firstValue) {
        isLeafNode = true;
        keys = new ArrayList<>();
        values = new ArrayList<>();
        storedData = new ArrayList<>();
        keys.add(firstKey);
        values.add(firstValue);
        LinkedList<T> tmp = new LinkedList<>();
        tmp.add(firstValue);
        this.storedData.add(tmp);
    }

    /**
     * Constructor: Used when the LeafNode is split
     */

    public LeafNode(List<K> newKeys, List<T> newValues, List<LinkedList<T>> linkedNewValues) {
        isLeafNode = true;
        keys = new ArrayList<>(newKeys);
        values = new ArrayList<>(newValues);
        storedData = new ArrayList<>(linkedNewValues);
    }

    /**
     * insert key/value into this node so that it still remains sorted
     * @param key
     * @param value
     */

    public void insertSorted(K key, T value) {
        if (key.compareTo(keys.get(0)) < 0) { // if key is less than smallest key, insert key at beginning of array
            keys.add(0, key);
            values.add(0, value);
            LinkedList<T> tmp = new LinkedList<>();
            tmp.add(value);
            this.storedData.add(0,tmp);
        }

        else if (key.compareTo(keys.get(keys.size() - 1)) > 0) { // if key is larger than largest key, insert key at end of array
            keys.add(key);
            values.add(value);
            LinkedList<T> tmp = new LinkedList<>();
            tmp.add(value);
            this.storedData.add(tmp);
        } else { // else, find place where key belongs along leaf node.
            ListIterator<K> iterator = keys.listIterator();
            while (iterator.hasNext()) {
                int index = iterator.nextIndex();
                K compare = iterator.next();
                int position = iterator.previousIndex();
                if (compare.compareTo(key) > 0) {
                    keys.add(position, key);
                    values.add(position, value);
                    LinkedList<T> tmp = new LinkedList<>();
                    tmp.add(value);
                    this.storedData.add(position,tmp);
                    break;
                }

                if (compare.compareTo(key) == 0) // if key is equal to another key, handle this duplicate
                {
                    if (keys.size() < 2){

                    }
                    else{
                        LinkedList<T> tmp = new LinkedList<>();
                        LinkedList<T> tmp2 = storedData.get(index);
                        tmp.add(value);
                        tmp2.addAll(tmp);
                        this.storedData.remove(index);
                        this.storedData.add(index, tmp2);
                        break;
                    }
                }
            }

        }
    }
}