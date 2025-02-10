package L03;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.search.strategy.Search;

public class HollywoodFilmShoot {

    public static void main(String[] args) {

        // 1) 间接定义场景 & 演员数据 (与前例相同)
        final int NB_SCENES = 6; // S0..S5
        final int NB_ACTORS = 5; // A0..A4

        boolean[][] actorInScene = {
                {false, false, false,  true, false,  true}, // A0
                { true, false, false, false, false, false}, // A1
                {false,  true, false,  true,  true, false}, // A2
                { true,  true,  true, false, false, false}, // A3
                {false, false,  true, false,  true,  true}, // A4
        };

        int[] dailyCost = {5, 3, 8, 6, 9};

        // 2) 创建Choco模型
        Model model = new Model("HollywoodFilmShoot");

        // 3) 定义 dayOfScene[s], 并设 allDifferent
        IntVar[] dayOfScene = model.intVarArray("dayOfScene", NB_SCENES, 0, NB_SCENES - 1);
        model.allDifferent(dayOfScene, "NEQ").post();

        // 4) 定义每位演员的 start[a], end[a]
        IntVar[] start = new IntVar[NB_ACTORS];
        IntVar[] end   = new IntVar[NB_ACTORS];
        for (int a = 0; a < NB_ACTORS; a++) {
            start[a] = model.intVar("startA"+a, 0, NB_SCENES - 1);
            end[a]   = model.intVar("endA"+a,   0, NB_SCENES - 1);
            model.arithm(start[a], "<=", end[a]).post();
        }

        // 5) 约束：若演员a参加场景s，则 dayOfScene[s] ∈ [start[a], end[a]]
        for (int a = 0; a < NB_ACTORS; a++) {
            for (int s = 0; s < NB_SCENES; s++) {
                if (actorInScene[a][s]) {
                    model.arithm(dayOfScene[s], ">=", start[a]).post();
                    model.arithm(dayOfScene[s], "<=", end[a]).post();
                }
            }
        }

        // 6) 加入大篷车限制：每天最多只能有3位演员在场
        //    我们遍历每一天 d=0..5，统计当天在场的演员数量
        for (int d = 0; d < NB_SCENES; d++) {
            // 为每位演员 a 创建一个布尔变量 isPresent[a,d]，表示"演员a在第d天是否在场"
            BoolVar[] isPresent = new BoolVar[NB_ACTORS];
            for (int a = 0; a < NB_ACTORS; a++) {
                // isPresent[a] = 1 当且仅当 start[a] <= d <= end[a]
                // 可以用 reification (IfThen) 约束，也可以用 max/min trick
                isPresent[a] = model.boolVar("isPresent_A"+a+"_D"+d);

                // 强制: isPresent[a] = 1 ↔ (start[a] <= d <= end[a])
                // Choco里可用以下写法：
                model.ifThenElse(
                        model.and(
                                model.arithm(start[a], "<=", d),
                                model.arithm(end[a], ">=", d)
                        ),
                        model.arithm(isPresent[a], "=", 1),
                        model.arithm(isPresent[a], "=", 0)
                );
            }
            // 约束： sum(isPresent[a]) <= 3
            model.sum(isPresent, "<=", 3).post();
        }

        // 7) 费用计算与最小化
        IntVar[] actorCost = new IntVar[NB_ACTORS];
        int c = 1;
        for (int a = 0; a < NB_ACTORS; a++) {
            // durationA = end[a] - start[a] + 1
            IntVar duration = model.intVar("durationA"+a, 1, NB_SCENES);
            model.arithm(duration, "=", end[a], "-", start[a].add(c).intVar()).post();

            // actorCost[a] = dailyCost[a] * duration
            actorCost[a] = model.intScaleView(duration, dailyCost[a]);
        }
        IntVar totalCost = model.intVar("totalCost", 0, 99999);
        model.sum(actorCost, "=", totalCost).post();

        model.setObjective(Model.MINIMIZE, totalCost);

        // 8) 求解
        Solver solver = model.getSolver();
        solver.setSearch(Search.inputOrderUBSearch(dayOfScene));

        int solutionCount = 0;
        while (solver.solve()) {
            solutionCount++;
            System.out.println("=== Found solution #" + solutionCount + " ===");
            System.out.println("  totalCost = " + totalCost.getValue());
            for (int s = 0; s < NB_SCENES; s++) {
                System.out.printf("  Scene S%d on day %d%n", s, dayOfScene[s].getValue());
            }
            for (int a = 0; a < NB_ACTORS; a++) {
                System.out.printf("  Actor A%d: start=%d, end=%d, cost=%d%n",
                        a, start[a].getValue(), end[a].getValue(), actorCost[a].getValue());
            }
            System.out.println("====================================");
        }

        if (solutionCount == 0) {
            System.out.println("No solution found under the 3-caravan constraint!");
        } else {
            System.out.println("Search finished. Found " + solutionCount + " solutions.");
        }
    }
}

