package lecture05;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

/*
 * A class for solving a basic 1D bin packing problem
 * Data values are hard coded into the class
 * Uses sums and 0/1 to count the bins
 */

public class BP1 {

   public static void main(String[] args) {

      /*
		//the sizes of the individual objects
		int[] sizes = {4,7,2,9,5,8,4};
		int nObjects = sizes.length;
		int nBins = 3;
		int binSize = 15; 
       */

      /**/
      //the sizes of the individual objects
      int[] sizes = {42, 63, 67, 57, 93, 90, 38, 36, 45, 42};
      int nObjects = sizes.length;  //the number of objects
      int nBins = 5;                //the number of bins
      int binSize = 150;            //the (uniform) bin capacity
      /**/

      //MODEL
      Model model = new Model("Simple BinPacking");

      //VARIABLES

      //an array of intVars, for the total size added to each bin
      IntVar[] binLoad = model.intVarArray("loads", nBins, 0, binSize);

      //a 2D array of 0/1, so that binPacking[i][j]==1 means that object j was placed in bin i
      IntVar[][] binPacking = model.intVarMatrix("solution", nBins, nObjects, 0 , 1);

      //CONSTRAINTS

      //for each bin: add the sizes of its objects to the get the bin load
      for (int bin = 0; bin<nBins; bin++) {
         model.scalar(binPacking[bin], sizes, "=", binLoad[bin]).post();        	
      }

      //for each object, make sure it is in exactly 1 bin
      for (int object = 0; object < nObjects; object++) {
         model.sum(ArrayUtils.getColumn(binPacking, object), "=", 1).post();
      }

      //symmetry breaker constraint
      //for each bin, make sure that its binLoad is not larger than the next bin
      for (int bin = 0; bin < nBins-1; bin++) {
         model.arithm(binLoad[bin], "<=", binLoad[bin+1]).post();
      }


      //SOLVE

      Solver solver = model.getSolver();
      
      int leastBinsUsed = nBins;
      int binsUsed;

      //        if (solver.solve()) {
      while (solver.solve()) { //print the solution
         System.out.println("Solution " + solver.getSolutionCount() + ":");

         //print out the solution
         //Bin i: [used?] obj j (size of j)* [bin load]

         /**/

         binsUsed = 0;
         for (int bin = 0; bin < nBins; bin++) {
            
            System.out.print("Bin " + bin + ": ");
            for (int object = 0; object < nObjects; object++) {
               if (binPacking[bin][object].getValue() == 1) {
                  System.out.print(object + "(" + sizes[object] + ") ");
               }
            }
            System.out.println("[" + binLoad[bin].getValue() + "]");
            if (binLoad[bin].getValue() > 0) {
               binsUsed++;
            }
            
         }
         leastBinsUsed = Math.min(leastBinsUsed, binsUsed);
         /**/
      }
      System.out.println("Fewest bins used: " + leastBinsUsed);
      solver.printStatistics();        
   }
}
