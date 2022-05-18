import java.util.ArrayList;
import java.util.List;

public class Population {
    public double mutationRate = 1;
    public List<Schedule> population = new ArrayList<>();
    public List<Schedule> matingPool = new ArrayList<>();
    public int generations = 0;

    public Population(int count, List<Employee> employees, List<Task> tasks) throws InterruptedException{
        for(int i = 0; i < count; i++){
            Schedule schedule = new Schedule(employees, tasks);
            population.add(schedule);
        }
    }

    public void calcFitness(){
        for(Schedule schedule : population)
            schedule.calcFitness();
    }

    public void selection(){
        matingPool = new ArrayList<>();

        // System.out.println("selection");
        double maxFitness = getMaxFitness();
        // System.out.println("selection end");

        for(Schedule schedule : population){

            int normalized = (int)((schedule.fitness / maxFitness) * 100);
            // System.out.println("schedule fitness: " + schedule.fitness );
            // System.out.println("max fitness: " + maxFitness );
            // System.out.println("Normalized value: " + normalized);

            for(int i = 0; i < normalized; i++){
                this.matingPool.add(schedule);
            }
        }
    }

    public void reproduction(){

        for(int i = 0; i < population.size(); i++){

            int m = (int)(Math.random() * this.matingPool.size());
            int d = (int)(Math.random() * this.matingPool.size());
            // System.out.println("m: " + m);
            // System.out.println("d: " + d);
            // System.out.println("matingPool size: " + matingPool.size());

            Schedule mom = this.matingPool.get(m);
            Schedule dad = this.matingPool.get(d);

            Schedule child = mom.crossover(dad);

            child.mutate(this.mutationRate);

            this.population.set(i, child);

            this.population.get(i).calcFitness();
        }

        this.generations++;
    }

    public double getMaxFitness(){
        double record = 0;

        for(int i = 0; i < population.size(); i++){
            // System.out.println("maxFitness - " + population.get(i).fitness);
            if(population.get(i).fitness > record){
                record = population.get(i).fitness;
            }
        }

        return record;
    }

    public int getSizeOfUnfinishedTasksOfHighestFitnessScore(){
        // System.out.println("getSizeOfUnfinishedTasksOfHighestFitnessScore");
        double maxFitness = getMaxFitness();

        for(Schedule schedule : population){
            if(maxFitness == schedule.fitness)
                return schedule.unfinishedTasks.size();
        }

        return 0;
    }

    public Schedule getMaxFitnessSchedule(){

        double maxFitness = getMaxFitness();
        for(Schedule schedule : population){
            if(maxFitness == schedule.fitness)
                return schedule;
        }
        return null;
    }
}
