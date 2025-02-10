package lecture05;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

/*
 * A class for solving a basic 1D bin packing problem, to minimise the number of bins
 * Data values are hard coded into the class
 * Achieves If-and-only-if by forcing two reified constraints to be equal,
 * or by using logical ifOnlyIf constraint
 */

public class BP1ReifyOptimiser {

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
      Model model = new Model("Simple BinPacking");

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

      /**/
      //for each bin, ensure that it is used **if and only if** its load is bigger than 0
      //done using an ifOnlyIf constraint (which does **not** need to be posted)
      //This is an alternative to the explicit reification constraints below
      for (int bin=0; bin<nBins; bin++) {
         model.ifOnlyIf(model.arithm(binUsed[bin], ">", 0), model.arithm(binLoad[bin], ">", 0));
      }
      /**/

      /*
        //for each bin, ensure that it is used **if and only if** its load is bigger than 0
        //done using 'reified' variables for the two constraints, and then saying that
        //the two reified variables must have equal value
        //This is an alternative to the ifOnlyIf constraint above
        BoolVar[] binUsedReif = model.boolVarArray("binUsedReif", nBins);
        BoolVar[] binLoadReif = model.boolVarArray("binLoadReif", nBins);
        for (int bin=0; bin<nBins; bin++) {
      	    model.arithm(binUsed[bin], ">", 0).reifyWith(binUsedReif[bin]);
      	    model.arithm(binLoad[bin], ">", 0).reifyWith(binLoadReif[bin]);
      	    model.arithm(binUsedReif[bin], "=", binLoadReif[bin]).post();
        }
       */

      //summing the individual bin used 0/1 vars gives the number of bins used
      model.sum(binUsed,"=",numberBinsUsed).post();

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
