package org.example;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
public class Lab1_2 {
    public static void main(String[] args ){
        Model model = new Model("Car Problem");
        String op = "!=";
        IntVar a = model.intVar("A", 1, 6);
        IntVar b = model.intVar("B",1,6);
        IntVar c = model.intVar("C",1,6);
        IntVar d = model.intVar("D",1,6);
        IntVar e = model.intVar("E",1,6);
        IntVar f = model.intVar("F",1,6);


        model.distance(a, d, ">", 1).post();
        model.distance(b, f, ">", 1).post();
        model.distance(b, e, ">", 2).post();
        model.distance(b, a, ">", 0).post();
        model.distance(b, c, ">", 0).post();
        model.distance(c, a, ">", 0).post();

        model.arithm(a,op,b).post();
        model.arithm(a,op,c).post();
        model.arithm(a,op,d).post();
        model.arithm(a,op,e).post();
        model.arithm(a,op,f).post();
        model.arithm(b,op,c).post();
        model.arithm(b,op,d).post();
        model.arithm(b,op,f).post();
        model.arithm(c,op,d).post();
        model.arithm(c,op,e).post();
        model.arithm(c,op,f).post();
        model.arithm(e,op,d).post();
        model.arithm(f,op,d).post();
        model.arithm(f,op,e).post();


        Solver solver = model.getSolver();

        if (solver.solve()) { // Print the solution
            System.out.println(a);
            System.out.println(b);
            System.out.println(c);
            System.out.println(d);
            System.out.println(e);
            System.out.println(f);


        }
        else {
            System.out.println("No Solution");
        }

    }
}
