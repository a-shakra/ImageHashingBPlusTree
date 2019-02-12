import java.io.File;

/**
 * Class that contains the file and its corresponding hashcode
 * Objects of this type are inserted into the B+ tree.
 *
 *
 * @author Anas Shakra
 */

public class photo
{
    File image;
    Integer [] hashCode;

    /**
     * Defined default constructor
     *
     *
     */

    public photo(File image, Integer [] hashCode)
    {
        this.image = image;
        this.hashCode = hashCode;
    }

    /**
     * Function that returns the hashcode associated with object of type photo
     */

    public Integer [] getCode()
    {
        return hashCode;
    }

    /**
     * Function that returns the file associated with object of type photo
     */

    public File getFile()
    {
        return image;
    }
}
