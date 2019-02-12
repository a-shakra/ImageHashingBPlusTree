import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Class that implements the dhash algorithm
 * This implementation is based on the algorithm description outlined by Dr. Neal Krawetz
 * Can be found at : http://www.hackerfactor.com/blog/index.php?/archives/529-Kind-of-Like-That.html
 * Contains a list of objects of type photo (user-defined class)
 * These are the objects that will be stored on the B+ tree along with their corresponding key
 * Class includes basic get and add methods that can insert and retrieve objects of type photo from the "listofPhotos" array.
 * @author Anas Shakra
 */

public class dhashTest {

    List<photo> listofPhotos = new ArrayList<>();

    /**
     * Default constructor
     */
    dhashTest(){}

    /**
     * adds object of type photo to listofPhotos variable
     */

    public void addPhoto(File file, Integer[] code) {
        photo temp = new photo(file, code);
        listofPhotos.add(temp);
    }

    /**
     * returns list of photos
     */

    public List<photo> getPhotos()
    {
        return listofPhotos;
    }

    /**
     * imports files into java
     */

    private static File[] insertFiles(String filename) {
        File[] result = new File(filename).listFiles(); // insert path to retrieve all images
        return result; // located on that path
    }

    /**
     * Generates hashcodes based on the images imported.
     * First portion of the code is equivalent to that found in dctHashTest and avgHashTest
     * It reads through a file and stored pixel values in 2D array
     * Second portion of the code is the actual dhash algorithm.
     * Algorithm does the following:
     * Calculates gradient of the picture (i.e. difference in pixel intensity from one pixel to another.)
     * I start from left to right so if (right pixel - left pixel) =  gradient which is stored in gradients array
     * Sets bit to 1 if (right gradient > left gradient) or 0 if (right gradient < left gradient)
     * Will produce a 64 bit hash code that is then stored along with file in the array listofPhotos
     * @param filename
     * @return List<Integer []> that represent the hashcodes for each photo
     */

    public void hashIT(String filename) throws Exception
    {
        //System.out.println("This is dHash");
        File[] files = insertFiles(filename); // filename is the folder that contains all the files
        for (File file : files) { // Loop that will perform this process for every file in the specified path
           // System.out.println("File currently being displayed: " + file.getName());
            FileInputStream f = new FileInputStream(file);
            BufferedReader d = new BufferedReader(new InputStreamReader(f));
            String magic = d.readLine();    // first line contains P2 or P5, this will be used to distinguish Binary or ASCII .pgm format
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
                }
                else {
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
            /*
            // Test commands that check whether the expected dimensions have been which indicate if the code is working properly
            System.out.println("Height=" + height);
            System.out.println("Width=" + width);
            System.out.println("Required elements=" + (height * width));
            System.out.println("Obtained elements=" + count);*/
            int nbofelements = 64; // Because each hashcode will consist of 64 integers
            List<Integer> gradients = new ArrayList<>();
            List<Integer> code = new ArrayList<>();
            for (int i = 0; i < height;i++)
            {
                for (int j =0; j < width;j++)
                {
                    if (j+1 == width) continue; // condition to prevent nullpointerexception
                    gradients.add(im[i][j+1] - im[i][j]);
                }
            }
            int nbof1s = 0;
            for (int i = 0; i < nbofelements; i++)
            {
                if (i == 0) code.add(0,0); // 64th bit set to zero. Haven't decided what it could be used for.
                if (i+1 == nbofelements) continue;
                if (gradients.get(i+1).compareTo(gradients.get(i)) > 0)
                {code.add(i,1);nbof1s++;}
                else {
                    code.add(i,0);
                }
            }

          //  System.out.println(code);
            //System.out.println(nbof1s);
            // Following code converts arraylist to int [] then int [] to Integer []
            // This is done because the addphoto accepts Integer [] only
            int[] array = code.stream().mapToInt(i->i).toArray(); // List<Integer> converted to int []: new feature of JAVA 8
            Integer[] something = Arrays.stream( array ).boxed().toArray( Integer[]::new ); // converted to Integer [] for storage
            addPhoto(file,something);

        }
    }
}

