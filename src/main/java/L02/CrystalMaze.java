package L02;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class CrystalMaze {
	public static void main(String[] args) {

	   //Create the Model
	   Model model = new Model("Crystal maze");

	   //create the variables and domains
	   IntVar[] places = model.intVarArray("places", 8, 1, 8);

	   //now create and post the constraints

	   //use the built-in global constraint to say that they are all different
	   model.allDifferent(places).post();
	   
	   /*
	   //alternate version, with explicit disequality constraints instead of alldifferent
	   for (int first = 0; first < 7; first++) {
	      for (int second = first+1; second < 8; second++) {
	         model.arithm(places[first], "!=", places[second]).post();
	      }
	   }
	   */

	   //then add the edge constraints
	   model.distance(places[0], places[1], ">", 1).post();
	   model.distance(places[0], places[2], ">", 1).post();
	   model.distance(places[0], places[3], ">", 1).post();
	   model.distance(places[1], places[2], ">", 1).post();
	   model.distance(places[1], places[4], ">", 1).post();
	   model.distance(places[1], places[5], ">", 1).post();
	   model.distance(places[2], places[3], ">", 1).post();
	   model.distance(places[2], places[4], ">", 1).post();
	   model.distance(places[2], places[5], ">", 1).post();
	   model.distance(places[2], places[6], ">", 1).post();
	   model.distance(places[3], places[5], ">", 1).post();
	   model.distance(places[3], places[6], ">", 1).post();
	   model.distance(places[4], places[5], ">", 1).post();
	   model.distance(places[4], places[7], ">", 1).post();
	   model.distance(places[5], places[6], ">", 1).post();
	   model.distance(places[5], places[7], ">", 1).post();
	   model.distance(places[6], places[7], ">", 1).post();
	
	   Solver  solver = model.getSolver();

	   /*
	   if (solver.solve()) { //Print the Solution
	      System.out.println("Solution :");
	      for (int place = 0; place < places.length; place++) {
	         System.out.println(places[place]);
	      }
	      System.out.println();
	   }
      */
	   /**/
	    while (solver.solve()) { //Print the Solution
	        System.out.println("Solution " + solver.getSolutionCount() + ":");
            for (IntVar integers : places) {
                System.out.println(integers);
            }
	        System.out.println();
	    }
      /**/
	   solver.printStatistics();
	}
}
