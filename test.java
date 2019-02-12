import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class test {

    /**
     * Calculates difference between two files by XORing their hashcodes
     * @param x,y
     * @return int
     */

    public static int difference(Integer [] x, Integer [] y,String hashType)
    {
        int nbofelements = 0;
        if (hashType.compareTo("avghash") == 0) {nbofelements = 100;}
        else{nbofelements = 64;}// for DCT hash
            int count = 0;
            for (int i = 0; i < nbofelements;i++)
            {
                if ((x[i] ^ y[i]) == 1) count++;
            }
            return count;
    }

    /**
     * Counts nb of 1's in hashcode of file. This used to determine where to store the file in the B+ tree
     * @param x,y
     * @return int
     */

    public static Integer counter(Integer [] x)
    {
        Integer count = 0;
        for (int i =0; i < x.length;i++ )
        {
            if (x[i] == 1) count++;
        }
        return count;
    }

    /**
     * Receives two photos, retrieves their bit string and calls the difference method to detect how trueDiff they are to one another
     * outputs the result as an int with numbers closer to 0 indicating greater trueDifference between the images
     * @param x,y,hashType
     * @return int
     */

    public static int trueDiff(photo x,photo y,String hashType)
    {
        Integer [] one = x.getCode();
        Integer [] two = y.getCode();
        int trueDiff = difference(one,two,hashType); // actual difference between the two images
        return trueDiff;
    }

    public static void main(String [] args) throws Exception
    {
        //Initializing the files and setting up the hash
        String fileName = "C:\\Users\\a.shakra\\Desktop\\AnasShakra_40004648_Project\\ImageFolder\\smalldhash"; // Change this to wherever you stored the files
        String hashType = "dhash"; // avghash for average hash, dcthash for dct hash and dhash for difference hash
        dhashTest item = new dhashTest(); // MAKE SURE TO INITIALIZE PROPER CLASS TYPE
        //hash complete
        item.hashIT(fileName);
        // Printing 10 hashcodes to show that hashing is working

        for (int i = 0; i < 10; i++)
        {
            System.out.println("The hash code for: " + item.getPhotos().get(i).getFile().getName() + " is: ");
            Integer [] codetoPrint = item.getPhotos().get(i).getCode();
            for (int y = 0; y < codetoPrint.length;y++)
            {
                System.out.print(codetoPrint[y] + " ");
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("Now that the example hashcodes are complete, let's get down to the real stuff!!!!");
        System.out.println();
        //Insertion of photos into the B+ tree
        List<photo> imagestoInsert = item.getPhotos(); // Now, all photos stored in this variable
        ListIterator<photo> iterator = imagestoInsert.listIterator();
        BPlusTree<Integer,photo> bplusTree=  new BPlusTree<>();

        while (iterator.hasNext())
        { // This function will insert all object photos and their corresponding keys into the bplustree
            photo toInsert = iterator.next();
            Integer key = counter(toInsert.hashCode);
            bplusTree.insert(key,toInsert);
        }

        //Selecting an input image and specifying tolerance.
        int imageNB = 10;
        double tolerance = 1; // If this is zero, that explains why the program doesn't look like it's working!
        photo sample = new photo(imagestoInsert.get(imageNB).getFile(),imagestoInsert.get(imageNB).getCode()); // sample that I use to compare them with trueDiff images
        Integer [] code = sample.getCode();
        int key = counter(code);
        int threshold = (int) (key * tolerance);
        Integer min = (int) (key * (1-tolerance));
        Integer max = (int) (key * (1+tolerance));

        List<LinkedList<photo>> listofPhotos = new LinkedList<>(); //Will contain all trueDiff images

        listofPhotos = bplusTree.range(min,max); //range of 0-64 will list all photos if dhash or dct. 0-100 will list all photos for avghash

        ListIterator<LinkedList<photo>> photoIterator =  listofPhotos.listIterator();

        while (photoIterator.hasNext())
        {
            int index = photoIterator.nextIndex();
            photoIterator.next();
            for (int i =0; i < listofPhotos.get(index).size();i++)
            {
                photo testSimilar = new photo(listofPhotos.get(index).get(i).getFile(),listofPhotos.get(index).get(i).getCode());
                int trueDifference = trueDiff(sample,testSimilar,hashType);
                if (trueDifference > threshold){ } // if tolerance isn't met, don't output those photos.
                else { // if tolerance met, output name of photo and degree of trueDifference.
                    System.out.println("Testing whether " + sample.getFile().getName() + " is similar to: " + listofPhotos.get(index).get(i).getFile().getName());
                    System.out.println("Result of XOR operation is: " + trueDifference);
                }
            }
        }
    }
}
