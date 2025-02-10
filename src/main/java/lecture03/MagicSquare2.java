package lecture03;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

/*
 * A matrix version of the magic Square problem
 * We use a matrix (or 2D array) of variables
 */
public class MagicSquare2 {

   public static void main(String[] args) {

      // Create a Model
      Model model = new Model("Magic Square Matrix");


      // Create variables

      //create a 2D array of IntVars for the square
      IntVar[][] square = model.intVarMatrix("square",  3,  3,  1,  9);

      //Getting the rows of this matrix (each row is an array) is a standard Java pattern.
      //Choco provides a set of helper methods in chocosolver.util.tools.ArrayUtils, which
      //we can use to get the columns as arrays.
      //We  create an array containing all variables
      //We create references to the diagonals as arrays.
      //Note: none of this is creating new CSP variables, just new references to the original vars

      IntVar[] flatVars = ArrayUtils.flatten(square); 

      IntVar[] diagdown = new IntVar[3];
      IntVar[] diagup = new IntVar[3];
      for (int row = 0; row<3; row++) {
         diagdown[row] = square[row][row];
         diagup[row] = square[2-row][row];
      }

      //now post the constraints on the rows, columns and diagonals
      for (int index = 0; index<3; index++) {
         model.sum(square[index], "=", 15).post();  // i.e. sum over one row
         model.sum(ArrayUtils.getColumn(square,index), "=", 15).post(); // sum over one column
      }
      model.sum(diagdown, "=", 15).post();
      model.sum(diagup, "=", 15).post();

      //now make sure that all values are different
      model.allDifferent(flatVars).post();

      //breaking symmetries, to reduce search
      
      model.arithm(square[0][0], "<", square[2][2]).post();
      model.arithm(square[0][0], "<", square[0][2]).post();
      model.arithm(square[0][0], "<", square[2][0]).post();
      
      model.arithm(square[0][2], "<", square[2][0]).post();
      

      
      // Solve the problem
      Solver solver = model.getSolver();


      //if (solver.solve()) {
      while (solver.solve()) { //print the solution
         System.out.println("Solution " + solver.getSolutionCount() + ":");
         for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
               System.out.print(square[row][col] + " ");
            }
            System.out.println();
         }
         System.out.println();
      }
      System.out.println("No more solutions");

      //else {
         //System.out.println("NO SOLUTION");
      //}
      
      //MeasuresRecorder measures = solver.getMeasures();
      //float rTime = measures.getTimeCount();
      //System.out.printf("time since start: %s\n", rTime);
      //float bTime = measures.getReadingTimeCount();
      //System.out.printf("Building time is %s\n", bTime);
      //System.out.printf("%s\n", measures.toString());
      solver.printStatistics();
      //measures = solver.getMeasures();
      //bTime = measures.getReadingTimeCount();
      //System.out.printf("Building time is %s\n", bTime);
      //System.out.printf("%s\n", measures.toString());
   }

}
