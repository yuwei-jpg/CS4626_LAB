package L03;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class OperationsScheduling {
    public static void main(String[] args) {

        Model model = new Model("Operation Scheduling");

        int[] durations = {7,2,2,4,2,5,3};
        int task_num = durations.length;

        IntVar[] startTimes = new IntVar[task_num];
        for(int i=0;i<task_num;i++){
           startTimes[i] =  model.intVar("start_"+i,0,24);
        }
        IntVar makespan = model.intVar("makespan",0,24);

        // Precedence constraints
        model.arithm(startTimes[1], ">=", startTimes[0].add(durations[0]).intVar()).post(); // B after A
        model.arithm(startTimes[3], ">=", startTimes[2].add(durations[2]).intVar()).post(); // D after C
        model.arithm(startTimes[4], ">=", startTimes[3].add(durations[3]).intVar()).post(); // E after D
        model.arithm(startTimes[5], ">=", startTimes[3].add(durations[3]).intVar()).post(); // F after D
        model.arithm(startTimes[5], ">=", startTimes[6].add(durations[6]).intVar()).post(); // F after G

        model.or(
                model.arithm(startTimes[0],">",startTimes[3].add(durations[3]).intVar()),
                model.arithm(startTimes[0].add(durations[0]).intVar(),"<=",startTimes[3])
        ).post();

        model.or(
                model.arithm(startTimes[2],">",startTimes[3].add(durations[3]).intVar()),
                model.arithm(startTimes[2].add(durations[2]).intVar(),"<=",startTimes[3])
        ).post();

        for (int i = 0; i < task_num; i++) {
            model.arithm(makespan, ">=", startTimes[i].add(durations[i]).intVar()).post();
        }

        // Objective: minimize makespan
        model.setObjective(Model.MINIMIZE, makespan);

        Solver solver = model.getSolver();
        if (solver.solve()) {
            System.out.println("Optimal Schedule:");
            for (int i = 0; i < task_num; i++) {
                System.out.println("Task " + i + " starts at: " + startTimes[i].getValue());
            }
            System.out.println("Total duration (makespan): " + makespan.getValue());
        } else {
            System.out.println("No solution found.");
        }

    }
}
