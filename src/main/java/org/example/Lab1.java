package org.example;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;


public class Lab1 {
    public static void main(String[] args) {
        // Step 1: Create a Model
        Model model = new Model("Choco Example");

        // Step 2: Define Variables
        IntVar V1 = model.intVar("v1",1,5);
        IntVar V2 = model.intVar("v2",1,5);
        IntVar V3 = model.intVar("v3",1,5);
        IntVar V4 = model.intVar("v4",1,5);
        String op = "!=";

        // Step 3: Define Constraints
        // post is very important
        // "Get an instance of a solver and attach it to the model"
        model.arithm(V1,"<=",V4,"-",1).post(); // V1<=V4-1
        model.arithm(V1,"<",V2).post(); // V1<V2
        model.arithm(V2,"+",V3,">",6).post(); // V2+V3>6
        model.arithm(V2,"+",V4,"=",5).post(); // V2+V4=5
        model.arithm(V4,"<",V3).post(); // V4<V3
        model.arithm(V4,op,V3).post();
        model.arithm(V1,op,V2).post();
        model.arithm(V1,op,V3).post();
        model.arithm(V4,op,V1).post();
        model.arithm(V2,op,V3).post();
        model.arithm(V4,op,V2).post();
        Solver solver = model.getSolver();

        if (solver.solve()) { // Print the solution
            System.out.println(V1);
            System.out.println(V2);
            System.out.println(V3);
            System.out.println(V4);

        }
        else {
            System.out.println("No Solution");
        }
    }

}
