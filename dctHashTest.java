import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Class that implements the Discrete Cosine Transform (DCT) hashing algorithm
 * This implementation is based on the algorithm description outlined by Dr. Neal Krawetz
 * Can be found at : http://www.hackerfactor.com/blog/?/archives/432-Looks-Like-It.html
 * Contains a list of objects of type photo (user-defined class)
 * These are the objects that will be stored on the B+ tree along with their corresponding key
 * Class includes basic get and add methods that can insert and retrieve objects of type photo from the "listofPhotos" array.
 * @author Anas Shakra
 */

public class dctHashTest{

    List<photo> listofPhotos = new ArrayList<>();
    private int size = 32;
    private int smallerSize = 8;
    private double[] c;

    dctHashTest(){initCoefficients();} // default empty constructor

    /**
     * intializes coefficient needed for calculating DCT
     */

    private void initCoefficients() {
        c = new double[size];

        for (int i=1;i<size;i++) {
            c[i]=1;
        }
        c[0]=1/Math.sqrt(2.0);
    }

    /**
     * adds objects of type photo to listofPhotos array
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
     * hashes images according to DCT hashing algorithm
     */

    public void hashIT(String filename) throws Exception
    {
        File[] files = insertFiles(filename);
        for (File file : files)
        {
            //System.out.println("File currently being displayed: " + file.getName());
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
            double[][] im = new double[height][width];

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
            double[][] dctVals = applyDCT(im); // calculates DCT of 32x32 image
            double total = 0;

            // Now, the DCT of the left corner 8x8 pixels is calculated and the average is found (without using the first pixel because its value is wildly different
            // from the other pixels
            for (int x = 0; x < smallerSize; x++) {
                for (int y = 0; y < smallerSize; y++) {
                    total += dctVals[x][y];
                }
            }
            total -= dctVals[0][0];
            double avg = total / (double) ((smallerSize * smallerSize) - 1);
            // Now bit is set to 1 if greater than avg DCT and 0 for less than or equal
            List<Integer> hashCodes = new ArrayList<>();
            for (int x = 0; x < smallerSize; x++) {
                for (int y = 0; y < smallerSize; y++) {
                        hashCodes.add(dctVals[x][y] > avg?1:0);
                }
            }
            int[] array = hashCodes.stream().mapToInt(i->i).toArray(); // List<Integer> converted to int []: new feature of JAVA 8
            Integer[] photohashCode = Arrays.stream( array ).boxed().toArray( Integer[]::new ); // converted to Integer [] for storage
            addPhoto(file,photohashCode);
        }

    }

    /**
     * Method that calculates the DCT.
     * Based on the formula for the DCT found in the book:
     * The Programmer's Guide to Compressed Image Files: Jpeg, Png, Gif, Xbm, Bmp by John Miano
     * @author Anas Shakra
     */

    private double[][] applyDCT(double[][] f) {
        int N = size;

        double[][] F = new double[N][N];
        for (int u=0;u<N;u++) {
            for (int v=0;v<N;v++) {
                double sum = 0.0;
                for (int i=0;i<N;i++) {
                    for (int j=0;j<N;j++) {
                        sum+=Math.cos(((2*i+1)/(2.0*N))*u*Math.PI)*Math.cos(((2*j+1)/(2.0*N))*v*Math.PI)*(f[i][j]);
                    }
                }
                sum*=((c[u]*c[v])/4.0);
                F[u][v] = sum;
            }
        }
        return F;
    }
}