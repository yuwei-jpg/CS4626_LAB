package L03;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Task;


public class ServiceTeamScheduling {

    public static void main(String[] args) {
        // 1) 题目给出的任务数据：
        int nbTasks = 8;
        int[] durations =    {7, 4, 5, 1, 3, 2, 4, 5}; // 对应任务0..7的持续时间
        int[] staffReq =     {1, 4, 5, 5, 1, 4, 3, 2}; // 对应任务0..7的员工需求
        int[] earliestStart = {3, 0, 1, 5, 0, 2, 6, 1}; // 对应任务0..7的最早开始
//        int[] latestStart  = {15,12,15,11,18,10,16,18}; // 对应任务0..7的最晚开始

        // 2) 创建一个 Choco 模型
        Model model = new Model("Service Team Scheduling");

        // 3) 为每个任务创建 start[i] 和 end[i] 变量
        //    为了简化，这里将 start[i], end[i] 均限制在 [0..10]
        IntVar[] start = new IntVar[nbTasks];
        IntVar[] end   = new IntVar[nbTasks];

        for (int i = 0; i < nbTasks; i++) {
            // start[i] 受【最早开始】和【最晚开始】的限制，但我们还要强制在 10 天内完工
            // 所以 start[i] ≥ earliestStart[i], 并且 start[i] + durations[i] ≤ 10
            // latestStart[i] 同时也可能大于 10，所以这里需要取最小值，以免不合理
            int maxStartPossible = 10;

            start[i] = model.intVar("start_"+i, earliestStart[i], maxStartPossible);
            end[i]   = model.intVar("end_"+i, 0, 10); // end[i] 也必须在 [0..10] 之内

            // 约束：end[i] = start[i] + durations[i]
            model.arithm(end[i], "=", start[i], "+", durations[i]).post();

            // 额外约束：end[i] ≤ 10 (即必须在 10 天内完成)
            model.arithm(end[i], "<=", 10).post();
        }

        // 4) 创建 Task 对象，以便使用 cumulative 约束
        //    Task(start, duration, end) 要求 duration 也是一个 IntVar
        Task[] tasks = new Task[nbTasks];
        for (int i = 0; i < nbTasks; i++) {
            // 对 duration 先包装成一个固定值的常量 IntVar
            IntVar durVar = model.intVar(durations[i]);
            tasks[i] = new Task(start[i], durVar, end[i]);
        }

        // 5) 创建一个可变的“员工容量”决策变量 staffCapacity
        //    范围设为 [1..8]，也可根据需求设得更宽
        IntVar staffCapacity = model.intVar("staffCapacity", 1, 8);

        // 6) 构建 cumulative 约束：
        //    cumulative(tasks, heights, capacity)
        //    其中 heights[i] 表示任务 i 在执行期间占用的“资源高度”，也就是需要的员工数
        IntVar[] heights = new IntVar[nbTasks];
        for (int i = 0; i < nbTasks; i++) {
            // 每个任务占用的“高度”是一个定值 staffReq[i]，包装成 IntVar
            heights[i] = model.intVar(staffReq[i]);
        }

        // 累加约束：在任意时刻，被执行任务的“员工占用量”之和 不得超过 staffCapacity
        model.cumulative(tasks, heights, staffCapacity).post();

        // 如果有任务先后顺序（precedence constraints），在这里额外添加：
        //    例如：model.arithm(endOf(taskA), "<=", startOf(taskB)).post();
        // 题目没有给出明确的precedence约束，这里省略。

        // 7) 定义优化目标：最小化 staffCapacity
        model.setObjective(Model.MINIMIZE, staffCapacity);

        // 8) 开始求解
        Solver solver = model.getSolver();

        // 打印标头
        System.out.println("Searching for minimal staff capacity within 10 days...");

        // 用 solve() 循环遍历可行解，直到找不到更优解为止
        int solutionCount = 0;
        while (solver.solve()) {
            solutionCount++;
            System.out.println("------------------");
            System.out.println("Solution #" + solutionCount);
            System.out.println("  staffCapacity = " + staffCapacity.getValue());
            for (int i = 0; i < nbTasks; i++) {
                System.out.printf("  Task %d: start=%d, end=%d, duration=%d, staffNeeded=%d%n",
                        i, start[i].getValue(), end[i].getValue(), durations[i], staffReq[i]);
            }
        }

        if (solutionCount == 0) {
            System.out.println("No solution found under the 10-day constraint!");
        } else {
            System.out.println("==================");
            System.out.println("Search complete. Found " + solutionCount + " solutions.");
        }
    }
}

