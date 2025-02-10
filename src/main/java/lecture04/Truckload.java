package lecture04;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

/*
 *  Simple truck loading problem (or bounded knapsack)
 *  Given an inventory of items, where each item has a weight and a profit,
 *  find a selection of the items to load into a truck, where there is a limit
 *  on the total weight carried by the truck, and
 *  (i) where at least minimum profit is achieved, or
 *  (ii) where the maximum possible profit is achieved
 */

public class Truckload {

	public static void main(String[] args) {
		// Create a Model
		Model model = new Model("Truck Loading");

		// Create variables

		/* Problem 1: 
		int[] weights = {9, 4}; //the weights of a group of item types 
		int[] profits = {3, 5}; //the profit obtained from each type - must be same length as weights
		int[] stock =   {5, 3}; //the maximum number of each item 
		int capacity = 50;      //maximum load weight in the truck
        int cost = 24;          //the profit threshold to be achieved (e.g. the cost of transport)
		 */

		/* Problem 2: */

		int[] weights = {15, 12, 17, 20, 39, 24, 13}; //the weights of a group of item types 
		int[] profits = {4,   2,  6,  4,  9,  8,  5}; //the profit obtained from each type - must be same length as weights
		int[] stock =   {6,   8,  5,  5,  2,  4,  7}; //the maximum number of each item 
		int capacity = 300;       //maximum load weight in the truck
		int cost = 100;            //the profit threshold to be achieved (e.g. the cost of transport)


		int numTypes = weights.length;    //number of object types

		//calculate the max profit, for setting up the variables
		//compute as though every piece of stock could be loaded and sold
		int maxprofit = 0;
		for (int type = 0; type<numTypes; type++) {
			maxprofit += stock[type]*profits[type];
		}


		//Create variables

		//create an array of IntVar references, then loop through the array creating the IntVar variables
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
		model.arithm(totalProfit, ">", cost).post();


		// Solve the problem
		Solver solver = model.getSolver();

		// state which variable is to be maximised (or minimised)
		model.setObjective(Model.MAXIMIZE, totalProfit);

		//if (solver.solve()) {
		while (solver.solve()) { //print the solution
			System.out.println("Solution " + solver.getSolutionCount() + ":  --------------------------------------");

			//next code block interrogates the variables and gets the current solution
			System.out.print("types:   ");
			for (int type = 0; type<numTypes; type++) {
				System.out.print("\t" + type);
			}
			System.out.println();
			System.out.print("weights: ");
            for (int weight : weights) {
                System.out.print("\t" + weight);
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
			System.out.print("loads:   ");
			for (int type = 0; type<numTypes; type++) {
				System.out.print("\t" + load[type].getValue());
			}
			System.out.println();
			System.out.println("totalWgt=" + totalWgt.getValue());
			System.out.println("totalProfit=" + totalProfit.getValue());
		}
		//Note - last solution generated is the optimal one

		solver.printStatistics();

	}

}
