package lecture05;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

/*
 * A class for solving a basic 1D bin packing problem, to minimise the number of bins
 * Data values are hard coded into the class
 * Uses sums, <= and 0/1 to count the bins
 */

public class BP1BasicOptimiser {

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
      Model model = new Model("Simple BinPacking: minimise number of bins");

      //VARIABLES

      //an array of intVars, for the total size added to each bin
      IntVar[] binLoad = model.intVarArray("loads", nBins, 0, binSize);

      //a 2D array of 0/1, so that binPacking[i][j]==1 means that object j was placed in bin i
      IntVar[][] binPacking = model.intVarMatrix("solution", nBins, nObjects, 0 , 1);

      //an array of 0/1 IntVars, to record whether a bin was used
      IntVar[] binUsed = model.intVarArray("binUsed", nBins, 0, 1);

      //a count of how many bins were used
      IntVar numberBinsUsed = model.intVar("numberBinsUsed", 0, nBins);

      //CONSTRAINTS

      //for each object in a bin, add the sizes to the get the bin load
      for (int bin = 0; bin<nBins; bin++) {
         model.scalar(binPacking[bin], sizes, "=", binLoad[bin]).post();        	
      }

      //for each object, make sure it is in exactly 1 bin
      for (int object = 0; object < nObjects; object++) {
         model.sum(ArrayUtils.getColumn(binPacking, object), "=", 1).post();
      }

      //for each bin, for each object, make sure the pair has value 1 only if binUsed is 1
      for (int bin = 0; bin < nBins; bin++) {
         for (int object = 0; object < nObjects; object++) {
            model.arithm(binPacking[bin][object], "<=", binUsed[bin]).post();
         }
      }

      //summing the individual bin used 0/1 vars gives the number of bins used
      model.sum(binUsed,"=", numberBinsUsed).post();

      //SOLVE

      Solver solver = model.getSolver();
      model.setObjective(Model.MINIMIZE, numberBinsUsed);
      
      //flatten the matrix into an array for searching
      //IntVar[] flattened = ArrayUtils.flatten(binPacking);
      
      //set a search strategy
      //solver.setSearch(new ImpactBased(flattened, 2,3,10, 0, false));
      //solver.setSearch(Search.activityBasedSearch(flattened));       

      
      //      if (solver.solve()) {
      while (solver.solve()) { //print the solution
         System.out.println("Solution " + solver.getSolutionCount() + ":");

         //print out the solution
         //Bin i: [used?] obj j (size of j)* [bin load]
         //*

         for (int bin = 0; bin < nBins; bin++) {
            System.out.print("Bin " + bin + ": [" + binUsed[bin].getValue() + "] ");
            for (int object = 0; object < nObjects; object++) {
               if (binPacking[bin][object].getValue() == 1) {
                  System.out.print(object + "(" + sizes[object] + ") ");
               }
            }
            System.out.println("[" + binLoad[bin].getValue() + "]");
         }
      }
      solver.printStatistics();

   }

}
