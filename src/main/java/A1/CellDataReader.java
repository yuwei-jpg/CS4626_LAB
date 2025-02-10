package A1;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class CellDataReader {
	
	private final int n;            //number of cells
	private final int high;         //upper bound of range; lower bound is assumed to be 0
	private final int[][] separations; //a matrix of cells (must be size n x n);
	                          //if separations[i][j] == k, then cells i and j frequencies must be separated by at least k
	
    public CellDataReader(String filename) throws IOException {
    	/*
    	 * Assumes data is in file in the following format:
    	 *    number-of-cells  high-freq
    	 * and then a sequence of number-of-cells lines, where each line is
    	 *    a sequence of integers of length  number-of-cells, representing minimum separation distances
    	 */
	    Scanner scanner = new Scanner(new File(filename));
	    n = scanner.nextInt();
	    high = scanner.nextInt();
	    separations = new int[n][n];
	    int temp;
	    for (int i=0;i<n;i++){
	    	for (int j = 0; j<n; j++) {
	    		temp = scanner.nextInt();
	    		//System.out.println(temp);
	    		separations[i][j] = temp;
	    	}
	    }
	    scanner.close();
	    System.out.println(n + " cells");
	    System.out.println(high + " is the highest frequency");
	    System.out.println("Separations: ");
	    System.out.print("  ");
	    for (int i =0; i < n; i++) {
	    	System.out.printf("%3d", i);
	    }
	    System.out.println();
	    for (int i = 0; i < n; i++) {
	    	System.out.printf("%2d: ", i);
	    	for (int j = 0; j < n; j++) {
	    		System.out.printf("%3d",  separations[i][j]);
	    	}
	    	System.out.println();
	    }
    }
    
    public int getNumCells() {
    	return n;
    }
    
    public int getFrequencyRange() {
    	return high;
    }
    
   public int[][] getSeparations() {
    	return separations;
    }
    
}
