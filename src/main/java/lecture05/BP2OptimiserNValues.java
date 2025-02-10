package lecture05;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

/*
 * A class for solving a basic 1D bin packing problem, minimising the number of bins used
 * Data values are hard coded into the class
 * Uses the global bin packing constraint, and nValues to count the number of binds used
 */

public class BP2OptimiserNValues {

   public static void main(String[] args) {

      /*
		//the sizes of the individual objects
		int[] sizes = {4,7,2,9,5,8,4};
		int nObjects = sizes.length;
		int nBins = 3;
		int binSize = 15; 
       */

      //the sizes of the individual objects
      int[] sizes = {42, 63, 67, 57, 93, 90, 38, 36, 45, 42};
      int nObjects = sizes.length;  //the number of objects
      int nBins = 5;                //the number of bins
      int binSize = 150;            //the (uniform) bin capacity

      //MODEL
      Model model = new Model("Simple BinPacking: minimise bins, uses binPacking and nValues");

      //VARIABLES

      //an array of intVars, for the total size added to each bin
      IntVar[] binLoad = model.intVarArray("loads", nBins, 0, binSize);

      //an array of intVars, stating which bin each object has been added to
      IntVar[] binForObject = model.intVarArray("binForObject",  nObjects,  0,  nBins-1);

      //a count of how many bins were used
      IntVar numberBinsUsed = model.intVar("numberBinsUsed", 0, nBins);

      //CONSTRAINTS

      //a global bin packing constraint
      model.binPacking(binForObject, sizes, binLoad,0).post();

      //the number of different **values** in the IntVarArray binForObject
      //is the total number of bins used
      model.nValues(binForObject,numberBinsUsed).post();

      //symmetry breaker constraint
      //for each bin, make sure that its binLoad is not larger than the next bin
      /* */
      for (int bin = 0; bin < nBins-1; bin++) {
         model.arithm(binLoad[bin], "<=", binLoad[bin+1]).post();
      }
      /* */

      //SOLVE
      Solver solver = model.getSolver();
      model.setObjective(Model.MINIMIZE, numberBinsUsed);

      //      if (solver.solve()) {
      while (solver.solve()) { //print the solution
         System.out.println("Solution " + solver.getSolutionCount() + ":");

         //print out the solution
         //Bin i: [used?] obj j (size of j)* [bin load]
         //*

         for (int bin = 0; bin < nBins; bin++) {
            System.out.print("Bin " + bin + ": ");
            for (int object = 0; object < nObjects; object++) {
               if (binForObject[object].getValue() == bin) {
                  System.out.print(object + "(" + sizes[object] + ") ");
               }
            }
            System.out.println("[" + binLoad[bin].getValue() + "]");
         }
      }
      solver.printStatistics();
   }

}
