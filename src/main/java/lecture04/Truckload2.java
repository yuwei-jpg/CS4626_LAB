package lecture04;

import java.io.IOException;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;

/*
 *  Simple truck loading problem (or bounded knapsack)
 *  Given an inventory of items, where each item has a weight and a profit,
 *  find a selection of the items to load into a truck, where there is a limit
 *  on the total weight carried by the truck, and
 *  where the maximum possible profit is achieved.
 *  
 *  Data is read in from file, where the file must contain
 *  size capacity 
 *  weight1 profit1 stock1
 *  weight2 profit2 stock2
 *  etc 
 */

public class Truckload2 {

	public static void main(String[] args)  throws IOException {
		// Create a Model
		Model model = new Model("Truck Loading");

		TruckData data = new TruckData("data/trucks3.txt");

		int numTypes = data.getSize(); //the number of item types
		int capacity = data.getCapacity();
		int[] weights = data.getWeights(); //the weights of a group of item types 
		int[] profits = data.getProfits(); //the profit obtained from each type - must be same length as weights
		int[] stock =   data.getStock(); //the maximum number of each item 		
		int maxprofit = data.getMaxProfit();
		int maxweight = data.getMaxWeight();

		System.out.println("Size: " + numTypes + "; capacity: " + capacity);
		System.out.println("Max profit: " + maxprofit + "; maxweight: " + maxweight);


		//Create variables
		IntVar[] load = new IntVar[numTypes];
		for (int type = 0; type<numTypes; type++) {
			load[type] = model.intVar("load"+type, 0,  stock[type]); //how many of each type
		}
		//Note - we could also have used the intVarArray method, to create an array of IntVars, 
		//all with the same domain, and then iterated through the array and posted a constraint 
		//to reduce the upper bound for each one
		IntVar totalWgt = model.intVar("total wgt", 0,  capacity);    //the total weight of selected items
		IntVar totalProfit = model.intVar("profit", 0, maxprofit); //the total profit of selected items
		//System.out.println("Maxprofit*numTypes = " + maxprofit*numTypes);
		System.out.println("Maxprofit = " + maxprofit);

		//CONSTRAINTS

		//constrain the total weight of the chosen items to be scalar of loads and weights
		//for each type, multiply the number of items by the unit weight, and sum the total
		//i.e. compute the scalar product of the load and weights arrays
		model.scalar(load, weights, "=", totalWgt).post();

		//constrain the total weight to be less than the capacity
		model.arithm(totalWgt, "<=", capacity).post();

		//constrain the total profit of the chosen items to be scalar product of load and profits
		model.scalar(load, profits,  "=", totalProfit).post();

		//constraint the total profit to be above the cost
		//model.arithm(totalProfit, ">", cost).post();



		// Solve the problem
		Solver solver = model.getSolver();

		//varying search strategy to speed things up

		//solver.setSearch(Search.domOverWDegSearch(load));
		//solver.setSearch(Search.inputOrderLBSearch(load));
		solver.setSearch(Search.activityBasedSearch(load)); 		 
		//solver.setSearch(new ImpactBased(load, 2,3,10, 0, false));


		// state which variable is to be maximised (or minimised)
		model.setObjective(Model.MAXIMIZE, totalProfit);

		//      if (solver.solve()) {
		while (solver.solve()) { //print the solution
			System.out.println("Solution " + solver.getSolutionCount() + " (" + solver.getTimeCount() + "s):  --------------------------------------");

			//next code block interrogates the variables and gets the current solution
			System.out.print("types:   ");
			for (int type = 0; type<numTypes; type++) {
				System.out.print("\t" + type);
			}
			System.out.println();
			/*
			System.out.print("weights: ");
			for (int type = 0; type<numTypes; type++) {
				System.out.print("\t" + weights[type]);
			}
			System.out.println();
			System.out.print("stock  : ");
			for (int type = 0; type<numTypes; type++) {
				System.out.print("\t" + stock[type]);
			}
			System.out.println();
			System.out.print("profits: ");
			for (int type = 0; type<numTypes; type++) {
				System.out.print("\t" + profits[type]);
			}
			System.out.println();
			 */
			System.out.print("loads:   ");
			for (int type = 0; type<numTypes; type++) {
				System.out.print("\t" + load[type].getValue());
			}
			System.out.println();
			System.out.println("totalWgt=" + totalWgt.getValue());
			System.out.println("totalProfit=" + totalProfit.getValue());
		}
		//Note - last solution generated is the optimal one

		/**/
		solver.printStatistics();

	}

}
