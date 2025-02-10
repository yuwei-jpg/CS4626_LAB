package A1;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

import java.io.IOException;

public class a1_solution {
    public static void main(String[] args) throws IOException {
        Model model = new Model("Cell");

        CellDataReader reader = new CellDataReader("/Users/jiyuwei/IdeaProjects/CS4626_LAB/src/main/java/A1/cell1.txt");

        int numCells = reader.getNumCells(); // number of cells

        int frequency = reader.getFrequencyRange();

        int[][] separation = reader.getSeparations();

        IntVar[] freqVars = new IntVar[numCells];

        // every cell has frequency range [0,5]
        for (int i = 0; i < numCells; i++) {
            freqVars[i] = model.intVar("freq_" + i, 0, frequency);
            System.out.println(freqVars[i]);
        }

        // set the maximum frequency used variable range [0,5]
        IntVar maxFreqUsed = model.intVar("maxFreqUsed", 0, frequency);
        System.out.println(maxFreqUsed);

        // set the constraint that each frequency in the freqVars should less than maximum frequency
        for (int i = 0; i < numCells; i++) {
            model.arithm(freqVars[i], "<=", maxFreqUsed).post();
        }

        // as said in the requirements |C0 -C1|>=3, |C2 -C1|>=2, |C3 -C1|>=2,|C2 -C3|>=2,
        // so we know that the |freqVars[i]-freqVars[j]| >=3 or |freqVars[j]-freqVars[i]| >=3
        for (int i = 0;i<numCells;i++){
            for (int j=i+1;j<numCells;j++){
                int s =separation[i][j];
                if(s>0){
                    model.or(
                            model.arithm(freqVars[i], "-",freqVars[j], ">=", s),
                            model.arithm(freqVars[j],"-", freqVars[i], ">=", s)
                            ).post();
                }
            }

        }

        // minimize the maximum frequency used
        model.setObjective(Model.MINIMIZE, maxFreqUsed);

        Solver solver = model.getSolver();
        while (solver.solve()) { //print the solution
            System.out.println("find solutions:");
            for (int i = 0; i < numCells; i++) {
                System.out.println("cell " + i + " frequency = " + freqVars[i].getValue());
            }
            System.out.println("highest frequency used = " + maxFreqUsed.getValue());
        }
        System.out.println("No more solutions");


    }

}
