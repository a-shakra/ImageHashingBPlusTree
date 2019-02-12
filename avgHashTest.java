import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * Class that implements the avgerage hashing algorithm
 * This implementation is based on the algorithm description outlined in the Yan,Gu,and Niu paper
 * Contains a list of objects of type photo (user-defined class)
 * These are the objects that will be stored on the B+ tree along with their corresponding key
 * Class includes basic get and add methods that can insert and retrieve objects of type photo from the "listofPhotos" array.
 * @author Anas Shakra
 */

public class avgHashTest {

    /**
     * Default constructor
     */

    public avgHashTest(){}

    List<photo> listofPhotos = new ArrayList<>();

    /**
     * adds objects of type photo to listofPhotos
     */

    public void addPhoto(File file, Integer[] code) {
        photo temp = new photo(file, code);
        listofPhotos.add(temp);
    }

    /**
     * Returns list of photos
     */

    public List<photo> getPhotos()
    {
        return listofPhotos;
    }

    /**
     * Function used to calculate mean of patches
     */

    static int sumMatrix(byte[][] x, int jSize, int iSize) {
        int total = 0;
        for (int i = iSize; i < iSize + 10; i++) // if i instead of iSize, error (out of bounds exception). why?
        {
            for (int j = jSize; j < jSize + 10; j++) // same case as what happened with i.
            {
                total += x[i][j];
            }
        }
        return total;
    }

    /**
     * Imports files into Java
     */

    private static File[] insertFiles(String filename) {
        File[] result = new File(filename).listFiles(); // insert path to retrieve all images
        return result; // located on that path
    }

    /**
     * Generates hashcodes based on the images imported.
     * @param filename
     * @return List<Integer []> that represent the hashcodes for each photo
     */

    public void hashIT(String filename) throws Exception {
        //System.out.println("This is avgHash");
        File[] files = insertFiles(filename);
        for (File file : files) { // Loop that will perform this process for every file in the specified path
            // System.out.println("File currently being displayed: " + file.getName());
            FileInputStream f = new FileInputStream(file);
            BufferedReader d = new BufferedReader(new InputStreamReader(f));
            String magic = d.readLine();    // first line contains P2 or P5
            String line = d.readLine();     // second line contains height and width
            while (line.startsWith("#")) {
                line = d.readLine();
            }
            Scanner s = new Scanner(line);
            int width = s.nextInt();
            int height = s.nextInt();
            line = d.readLine();// third line contains maxVal
            s = new Scanner(line);
            int maxVal = s.nextInt();
            byte[][] im = new byte[height][width];

            int count = 0;
            int b = 0;
            while (count < height * width) {
                b = d.read();
                if (b < 0)
                    break;

                if (b == '\n') { // do nothing if new line encountered
                } else {
                    if ("P5".equals(magic)) { // Binary format
                        im[count / width][count % width] = (byte) ((b >> 8) & 0xFF);
                        count++;
                        im[count / width][count % width] = (byte) (b & 0xFF);
                        count++;
                    } else {  // ASCII format
                        im[count / width][count % width] = (byte) b;
                        count++;
                    }
                }
            }
            int nbofelements = 100; // 100x100 image will be divided into 10x10 patches and so there will be 100 patches = 100 sized bit string
            List<Integer> meanA = new ArrayList<Integer>(); // Will store means of each 10x10 square at first then it will stored the hashcode of the image being processed
            List<Integer> medianA = new ArrayList<Integer>();// Will store all the elements of the 100x100 image which will be used to calculate the median
            int total = 0; // variable that will hold the mean of one 10x10 patch. Used in this coming for loop
            for (int i = 0; i < nbofelements; i += 10) { // These two for loops go through each patch in the image. They incremement by 10x10 starting from left of image and moving to right
                for (int j = 0; j < nbofelements; j += 10) // increments by 10 on each run to move to next patch
                {
                    total = sumMatrix(im, i, j); // calls function that will calculate mean of a 10x10 patch
                    total = total / nbofelements; // total now contains the mean
                    meanA.add(total); // mean for that square matrix is added to the mean array.
                }
            }
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) { // goes through each element in the 100x100 image and stored its value into the medianA array
                    int toAdd = im[i][j];
                    medianA.add(toAdd);  // medianA now contains all elements in the 2D array. operation will be performed to find median of all values.
                }
            }

            Collections.sort(medianA); // Upcoming median formula only works if array is sorted

            int median; // will contain media
            int nbof1s = 0;// counts number of 1's in each hashcode. Mostly just used to test if the results make sense
            if (medianA.size() % 2 == 0) {
                median = (medianA.get(medianA.size() / 2) + medianA.get((medianA.size() - 1) / 2)) / 2;
            } else {
                median = medianA.get(medianA.size() / 2);
            }
            for (int i = 0; i < meanA.size(); i++) { // if the mean of the 10x10 patch is less than median, set bit to 0. else, set bit to 1 and increment nbof1's
                if (meanA.get(i).compareTo(median) <= 0) meanA.set(i, 0);
                else {
                    meanA.set(i, 1);
                    nbof1s++;
                }
            }
            //two statements below used to test what the codes are
            //System.out.println(meanA);
            //System.out.println(nbof1s);
            // Transfer hash codes to the Integer array created in the beginning of the code.
            // To do that, need to convert Integer to int [] and then int [] to Integer []
            int[] array = meanA.stream().mapToInt(i -> i).toArray(); // List<Integer> converted to int []: new feature of JAVA 8
            Integer[] something = Arrays.stream(array).boxed().toArray(Integer[]::new); // converted to Integer [] for storage
            addPhoto(file,something);
        }
    }
}











