import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Main {
    // data1.tsv file contains 2433 tasks
    // We can do ~50% of the tasks in this data1.tsv file with around 40 workers
    // We can do ~80% of the tasks in this data1.tsv file with around 90 workers
    // We can do ~90% of the tasks in this data1.tsv file with around 100 - 110 workers
    public static int NUMBER_OF_WORKERS = 20;
    public static int NUMBER_OF_POPULATION = 20;
    public static int NUMBER_OF_GENERATIONS = 50;

    public static void main(String[] args) throws IOException, ParseException, InterruptedException, ExecutionException {
        List<Employee> workers = Utils.getWorkers(NUMBER_OF_WORKERS);
        List<Task> tasks = Utils.getTasks("src/data4.tsv");
        //System.out.println("Worker size: " + workers.size());
        //System.out.println("Task size: " + tasks.size());
        Population population = new Population(NUMBER_OF_POPULATION, workers, tasks);
        //System.out.println("Population size: " + population.population.size());

        population.calcFitness();
        System.out.println("At generation 0 the highest fitness score is: " + population.getMaxFitness());
        System.out.println("And the lowest number of unfinished tasks is: " + population.getSizeOfUnfinishedTasksOfHighestFitnessScore());

        while(population.generations < NUMBER_OF_GENERATIONS){

            population.calcFitness();
            population.selection();
            population.reproduction();
            System.out.println("At generation " + population.generations + " the highest fitness score is: " + population.getMaxFitness());
            System.out.println("And the lowest number of unfinished tasks is: " + population.getSizeOfUnfinishedTasksOfHighestFitnessScore());
        }

        Utils.printResults(population.getMaxFitnessSchedule());

    }
}
