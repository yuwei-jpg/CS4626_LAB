package lecture06;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Task;


/*
 * A simple solver for a job shop scheduling problem
 * 
 * Best results (2025) for jssp-ft10:
 * obtained with ActivitySearch searching over all IntVars, using disjunctive constraints
 * Default (ie no search strategy) using disjunctive also finds and proves optimal in under 5 min
 * Using cumulative slows it down badly
 * 
 */
public class SimpleJSS {

   public static void main(String[] args) {

      //read the problem in from file in standard format
      JSSPReader reader = new JSSPReader("data/jssp-ft10.txt");

      int numJobs = reader.getNumJobs();        //number of jobs
      int numRes = reader.getNumResources();    //number of resources
      int[][][] problem = reader.getProblem();  //3D array of jobs, tasks, {resource, duration}
      int sumDurations = reader.getSumDurations();  //the makespan if we just sequenced all the tasks in one line

      //a maximum value for the makespan
      //int deadline = 20;
      //int deadline = sumDurations;

      //an array for recording the next index for each restask array
      int[] resIndices = new int[numRes];

      //SOLVER
      Model model = new Model("Simple JSS");

      //VARIABLES

      //2D array of task resource consumption for cumulative - every entry will be "1" for JSSP
      IntVar[][] heights = new IntVar[numRes][numJobs];
      //array of capacities for the resources for cumulative - every entry will be "1" for JSSP
      IntVar[] capacity = new IntVar[numRes];
      //array of all task start times
      IntVar[] allTaskStarts = new IntVar[numJobs*numRes];
      //a 2d array for the Task variables [job][task]
      Task[][] jobtask = new Task[numJobs][numRes];
      //a 2d array for the tasks assigned to each machine [res][job]
      Task[][] restask = new Task[numRes][numJobs];

      //for each job, for each task, create a Task variable, and add it to the jobtask and restask arrays
      int resource;
      int tasks = 0;
      for (int j = 0; j<numJobs; j++) {
         System.out.println("Job " + j);
         for (int t = 0; t < numRes; t++) {
            System.out.print("task " + t + " ");
            resource = problem[j][t][0]; //which resource is this job,task using
            System.out.println("is on resource " + resource +"; duration " + problem[j][t][1]);
            //create the Task object containing the start, duration and end variables
            jobtask[j][t] = new Task(model.intVar("j" + j + "t" + t + "s", 0, sumDurations),
                                     model.intVar("j" + j + "t" + t + "d", problem[j][t][1]),
                                     model.intVar("j" + j + "t" + t + "e", 0, sumDurations));
            //now create a reference to this task and put in the array of tasks per resource
            restask[resource][resIndices[resource]] = jobtask[j][t];
            //prepare the parameters for the cumulative constraint
            heights[resource][resIndices[resource]] = model.intVar(1);
            capacity[resource] = model.intVar(1);
            resIndices[resource]++;
            //finally add the task.start variable to the allTaskStarts array
            allTaskStarts[tasks++] = jobtask[j][t].getStart();
         }
      }

      //the finish time of the last task - the usual objective in JSSP
      IntVar makespan = model.intVar("makespan",  0,  sumDurations);

      //CONSTRAINTS

      //for each resource, create a cumulative constraint, for the relevant tasks, all with height 1, for capacity 1
      
      /* 
      for (int res = 0; res < numRes; res++) {
         model.cumulative(restask[res], heights[res], capacity[res]).post();
      }
       */
      

      //for each resource, create "no overlap" constraints between the tasks
      //these constraints are not needed if we are using cumulative

      /* */
		for (int res = 0; res < numRes; res++) {
			for (int task1 = 0; task1 < numJobs-1; task1++) {
				for (int task2 = task1+1; task2 < numJobs; task2++) {
			        model.or(model.arithm(restask[res][task1].getEnd(), 
			                      "<=", restask[res][task2].getStart()),
			        		     model.arithm(restask[res][task2].getEnd(), 
			        		          "<=", restask[res][task1].getStart())).post();	
				}
			}
		}
       /* */



      //for each job, create precedence constraints between its tasks
      for (int j = 0; j < numJobs; j++) {
         for (int before = 0; before < numRes-1; before++) {
            model.arithm(jobtask[j][before].getEnd(), 
                  "<=", 
                  jobtask[j][before+1].getStart()).post();
         }
         //and post that the last task finishes before or on the makespan
         model.arithm(jobtask[j][numRes-1].getEnd(), "<=", makespan).post();
      }

      //for decision problems, constrain the makespan to be less than a threshold
      //model.arithm(makespan, "<=", deadline).post();

      //SOLVING
      
      
      Solver solver = model.getSolver();
      
      //set up the variables to search over
      //IntVar[] searchVars = ArrayUtils.concat(allTaskStarts,  makespan);
      //IntVar[] searchVars = allTaskStarts;
      //IntVar[] searchVars = model.retrieveIntVars(true);

      //Search Strategy
      //solver.setSearch(Search.domOverWDegSearch(searchVars));
      //solver.setSearch(Search.inputOrderLBSearch(searchVars));
      //solver.setSearch(Search.activityBasedSearch(searchVars)); 		 
      //solver.setSearch(new ImpactBased(searchVars, 2,3,10, 0, false));


      //limit the total search time
      solver.limitTime("10m"); 

      // state which variable is to be maximised (or minimised)
      model.setObjective(Model.MINIMIZE, makespan);

      //      if (solver.solve()) {
      while (solver.solve()) { //print the solution
         System.out.println("Solution " + solver.getSolutionCount() + ": ");
         /* Print out the schedule (comment this out for big problems) 
         for (int job = 0; job < numJobs; job++) {
            System.out.print(job + ": ");
            for (int task = 0; task < numRes; task++) {
               System.out.print("(t:" + task + ";r:" + problem[job][task][0] + ";d:" + problem[job][task][1] 
            		            + ";s:" + jobtask[job][task].getStart().getValue() + ") ");
            }
            System.out.println();
         }
           */
         System.out.print("(sec = " + solver.getTimeCount() + ") ");
         System.out.println("Makespan = " + makespan.getValue());
      }
      solver.printStatistics();
   }
}
