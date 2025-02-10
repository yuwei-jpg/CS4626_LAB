package A2;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Task;

import java.io.IOException;

public class a2_solution {
    public static void main(String[] args) throws IOException {
        Model model = new Model("Hospital");

        HospitalData reader = new HospitalData("/Users/jiyuwei/IdeaProjects/CS4626_LAB/src/main/java/A2/hospital1.txt");

        int numOps = reader.getNumOps();
        System.out.println("numOps"+numOps);
        int rooms = reader.getNumRooms();
        System.out.println("rooms"+rooms);
        int last_time = reader.getLastTime();
        System.out.println("last_time"+last_time);
        int[] req_assistants_each = reader.getReqAssistants();
        int[] durations_each = reader.getDurations();
        int[] last_each = reader.getLast();
        int[] clean_each = reader.getCleaning();
        int[] req_surgeons_each = reader.getReqSurgeons();

        int[] assistants_available = reader.getAssistantsOnDuty();
        int[] surgeons_available =reader.getSurgeonsOnDuty();

        for(int i=0;i<numOps;i++){
            System.out.println(i+"req_assistants_each"+req_assistants_each[i]);
            System.out.println(i+"durations_each"+durations_each[i]);
            System.out.println(i+"last_each"+last_each[i]);
            System.out.println(i+"clean_each"+clean_each[i]);
            System.out.println(i+"req_surgeons_each"+req_surgeons_each[i]);
        }

        //  Set the constraints
        IntVar[] start_time = model.intVarArray("start_time",numOps,0,last_time);
        IntVar[] end_time = model.intVarArray("end_time",numOps, 0,last_time);
        Task[] task = new Task[numOps];
        IntVar[] require_assistants = new IntVar[numOps];

        for (int i=0;i<numOps;i++){


            model.arithm(end_time[i],"=", start_time[i],"+", durations_each[i]).post();
            model.arithm(end_time[i],"<=",last_time).post();
            model.arithm(end_time[i],"<=",last_each[i]).post();

            IntVar dval = model.intVar(durations_each[i]);
            task[i] = new Task(start_time[i],dval,end_time[i]);

            require_assistants[i] = model.intVar(req_assistants_each[i]);
        }

        int cap = assistants_available[0];
        for (int a : assistants_available) {
            if (a < cap) {
                cap = a;
            }
        }
        IntVar assistantsCapacity = model.intVar("assistantsCapacity", cap);

        model.cumulative(task,require_assistants,assistantsCapacity);

        IntVar make_span = model.intVar("make_span", 0, last_time);
        model.max(make_span, end_time).post();

        model.setObjective(Model.MINIMIZE, make_span);

        Solver solver = model.getSolver();

        if (solver.solve()) {
            System.out.println("找到最优解, 最后手术完成时间(makespan) = " + make_span.getValue());
            for (int i = 0; i < numOps; i++) {
                System.out.println("手术 " + i +
                        ": 开始时间 = " + start_time[i].getValue() +
                        ", 结束时间 = " + end_time[i].getValue() +
                        ", 持续时间 = " + durations_each[i] +
                        ", 需要助手数 = " + req_assistants_each[i]+
                        ", 可用助理数 = " + assistants_available[i]
                );

            }
        } else {
            System.out.println("没有找到可行解。");
        }
    }

}
