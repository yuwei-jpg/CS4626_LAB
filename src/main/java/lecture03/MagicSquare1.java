package lecture03;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

/*
 * First version of the Magic  Square problem -
 *   fixed to a 3x3 grid, with the row totals being 15
 */
public class MagicSquare1 {

   public static void main(String[] args) {

      // Create a Model
      Model model = new Model("Magic Square");

      // Create variables

      IntVar x00 = model.intVar("[0,0]", 1, 9);
      IntVar x01 = model.intVar("[0,1]", 1, 9);
      IntVar x02 = model.intVar("[0,2]", 1, 9);
      IntVar x10 = model.intVar("[1,0]", 1, 9);
      IntVar x11 = model.intVar("[1,1]", 1, 9);
      IntVar x12 = model.intVar("[1,2]", 1, 9);
      IntVar x20 = model.intVar("[2,0]", 1, 9);
      IntVar x21 = model.intVar("[2,1]", 1, 9);
      IntVar x22 = model.intVar("[2,0]", 1, 9);

      IntVar M = model.intVar("target", 15);

      //now create and post the constraints
      //for each row (or col or diag) we need an arithmetic expression involving 3 variables and a constant
      //the arithm constraint limits us to 2 vars plus constant, or 3 vars only
      //Instead, we use the global constraint 'sum' which takes an array of vars of any length
      //and constraints the sum of the values of the vars in the array relative to another variable.
      //So we have the turned the target sum 15 into an integer variableM  with 15 as the only value
      //in its domain, and then we constrain the sum to be equal to M
      //Since we only use this array of variables in this constraint, we don't need to refer to it again, 
      //so we create an anonymous array (which contains references to our variables) as input
      //We don't have to do it that way - we could instead do:
      //IntVar[] row1 = {x00,x01,x02};
      //model.sum(row1, "=", M).post();
      //etc
      model.sum(new IntVar[]{x00,x01,x02}, "=", M).post();
      model.sum(new IntVar[]{x10,x11,x12}, "=", M).post();
      model.sum(new IntVar[]{x20,x21,x22}, "=", M).post();
      model.sum(new IntVar[]{x00,x10,x20}, "=", M).post();
      model.sum(new IntVar[]{x01,x11,x21}, "=", M).post();
      model.sum(new IntVar[]{x02,x12,x22}, "=", M).post();
      model.sum(new IntVar[]{x00,x11,x22}, "=", M).post();
      model.sum(new IntVar[]{x20,x11,x02}, "=", M).post();

      //We need an overall constraint to say that I can only use each number 1 to 9 at most
      //once (and since there are 9 variables, that means I must use each value exactly once)
      //Again, we are using an anonymous array
      model.allDifferent(x00,x01,x02,x10,x11,x12,x20,x21,x22).post();

      // Solve the problem
      Solver solver = model.getSolver();

      //if (solver.solve()) {
      while (solver.solve()) { //print the solution
         System.out.println("Solution " + solver.getSolutionCount() + ":");
         System.out.println(x00 + " " + x01 + " " + x02);
         System.out.println(x10 + " " + x11 + " " + x12);
         System.out.println(x20 + " " + x21 + " " + x22);
         System.out.println();
      }
      System.out.println("No more solutions");
      solver.printStatistics();
   }

}
