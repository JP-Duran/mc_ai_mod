package dwarf.entity.custom;


import java.util.LinkedList;
import java.util.Queue;

public class DwarfEvaluationManager {

    // Queue to hold the evaluation tasks
    private static final Queue<EvaluationTask> taskQueue = new LinkedList<>();
    // Currently running evaluation task is initailized to null
    private static EvaluationTask currentTask = null;

    public static void addTask(EvaluationTask task) {
        taskQueue.add(task);
    }

    // Called every tick
    public static void tick() {
        // If no task is empty and queue is not empty, start next task
        if (currentTask == null && !taskQueue.isEmpty()) {
            currentTask = taskQueue.poll();
            System.out.println("Starting new evaluation task.");
            currentTask.start();
        }

        // If a task is running, tick it and check if it's complete
        if (currentTask != null) {
            System.out.println("Checking if current task is complete...");

            currentTask.tick();

            // If the task is complete, set current task to null so next can start
            if (currentTask.isComplete()) {
                System.out.println("Current task complete. Clearing current task.");
                currentTask = null;
            }
        }
    }

}

