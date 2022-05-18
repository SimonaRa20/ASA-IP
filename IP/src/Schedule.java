import java.util.*;

public class Schedule {
    public List<Employee> employees;
    public List<Task> tasks;

    public Map<Employee, List<Task>> employeeTasks = new TreeMap<>();

    public List<Task> finishedTasks;
    public List<Task> unfinishedTasks;

    public double fitness = 0;

    public Schedule(List<Employee> employees, List<Task> tasks) throws InterruptedException{
        this.employees = employees;
        this.tasks = new ArrayList<>(tasks);
        this.unfinishedTasks = new ArrayList<>(tasks);
        this.finishedTasks = new ArrayList<>();

        for(Employee employee : this.employees)
            employeeTasks.put(employee, new ArrayList<>());
        generateRandomSchedule();
    }

    public Schedule(List<Employee> motherEmployees, List<List<Task>> motherTasks, List<Employee> fatherEmployees, List<List<Task>> fatherTasks, List<Task> allTasks){
        this.employees = new ArrayList<>(motherEmployees);
        this.employees.addAll(fatherEmployees);

        this.tasks = new ArrayList<>(allTasks);
        this.unfinishedTasks = new ArrayList<>(allTasks);
        this.finishedTasks = new ArrayList<>();

        for(int i = 0; i < motherEmployees.size(); i++){
            employeeTasks.put(motherEmployees.get(i), motherTasks.get(i));
        }

        for(int i = 0; i < fatherEmployees.size(); i++){
            employeeTasks.put(fatherEmployees.get(i), fatherTasks.get(i));
        }
    }

    private void generateRandomSchedule() throws InterruptedException{
        Collections.shuffle(unfinishedTasks, new Random());

        List<Task> unfinished = new ArrayList<>(unfinishedTasks);

        for(Task task : unfinished){
            List<Employee> help = new ArrayList<>();
            boolean enough = true;
            int count = 0;
            for(int i = 0; i < task.workerCount; i++){
                int index = (int) (Math.random() * employees.size());
                Employee worker = employees.get(index);
                if(!help.contains(worker)){
                    if(CanPlaceTask(worker, task))
                        help.add(worker);
                    else {
                        i--;
                        count++;
                    }
                } else {
                    i--;
                    count++;
                }
                if(count > employees.size() * 2){
                    enough = false;
                    break;
                }
            }

            if(enough && help.size() == task.workerCount){
                for(Employee worker : help){
                    employeeTasks.get(worker).add(task);
                }
                finishedTasks.add(task);
                unfinishedTasks.remove(task);
            }
        }
    }

    public int totalWorkingTime(List<Task> work){
        int workingTime = 0;

        for(Task task : work){
            workingTime += task.duration;
        }

        return workingTime/60;
    }

    private void FixMutatedSchedule(){

        ReconstructFinishedUnfinishedTasks();

        ClearInvalidTasksAfterMutation();

        FixDuplicatingTasksBetweenWorkers();

        AddUnfinishedTasksToWorkersIfValid();

        List<Employee> BadWorkers = NotEnoughWorkingHours();

        TryAddingTasksToShortHourWorkers(BadWorkers);
    }

    private void ReconstructFinishedUnfinishedTasks(){

        for (Map.Entry<Employee, List<Task>> entry : employeeTasks.entrySet()){
            for(Task task : entry.getValue()){
                if(unfinishedTasks.contains(task)){
                    unfinishedTasks.remove(task);
                    finishedTasks.add(task);
                }
            }
        }
    }

    private void ClearInvalidTasksAfterMutation(){

        for (Map.Entry<Employee, List<Task>> entry : employeeTasks.entrySet()){

            Employee employee = entry.getKey();
            List<Task> initialWorkerTasks = entry.getValue();
            employeeTasks.put(employee, new ArrayList<Task>());
            Collections.sort(initialWorkerTasks,Task.Comparators.STARTTIME);

            for(Task task : initialWorkerTasks){
                List<Employee> help = new ArrayList<>();
                boolean enough = true;

                if(CanPlaceTask(employee, task))
                    help.add(employee);
                else
                    enough = false;

                if(enough){
                    int count = 0;
                    for(int i = 0; i < task.workerCount-1; i++){
                        Employee randomWorker = this.employees.get((int)Math.random()*employeeTasks.size());
                        if(randomWorker.compareTo(employee) != 0){

                            if(!employeeTasks.get(randomWorker).contains(task))
                                if(CanPlaceTask(randomWorker, task))
                                    help.add(randomWorker);
                                else {
                                    i--;
                                    count++;
                                }
                            else
                                help.add(randomWorker);


                        } else {
                            i--;
                            count++;
                        }

                        if(count > employees.size()*2){
                            enough = false;
                            break;
                        }
                    }
                }

                if(enough && help.size() == task.workerCount){

                    for(Employee helper : help){
                        if(!employeeTasks.get(helper).contains(task))
                            employeeTasks.get(helper).add(task);
                    }
                } else {
                    if(!unfinishedTasks.contains(task))
                        unfinishedTasks.add(task);
                    finishedTasks.remove(task);
                }
            }
        }
    }

    private void AddUnfinishedTasksToWorkersIfValid(){

        Collections.shuffle(unfinishedTasks, new Random());
        List<Task> unfinished = new ArrayList<>(unfinishedTasks);

        for(Task task : unfinished){
            for (Map.Entry<Employee, List<Task>> entry : employeeTasks.entrySet()){

                Employee worker = entry.getKey();
                List<Task> initialWorkerTasks = entry.getValue();
                Collections.sort(initialWorkerTasks, Task.Comparators.STARTTIME);
                boolean placeable = true;
                List<Employee> help = new ArrayList<>();

                if(!CanPlaceTask(worker, task)){
                    placeable = false;
                }

                if(placeable){
                    help.add(worker);
                    if(help.size() != task.workerCount)
                        for(int i = 0; i < employees.size(); i++){
                            if(employees.get(i).compareTo(worker) != 0){
                                if(!employeeTasks.get(employees.get(i)).contains(task)){
                                    if(CanPlaceTask(employees.get(i), task)){
                                        help.add(employees.get(i));
                                    }
                                } else {
                                    help.add(employees.get(i));
                                }
                            }

                            if(help.size() == task.workerCount){
                                placeable = true;
                                break;
                            } else {
                                placeable = false;
                            }
                        }
                }

                if(placeable && help.size() == task.workerCount && !finishedTasks.contains(task)){
                    for(Employee helper : help){
                        if(!employeeTasks.get(helper).contains(task))
                            employeeTasks.get(helper).add(task);
                    }
                    unfinishedTasks.remove(task);
                    finishedTasks.add(task);
                }
            }
        }

    }

    private void FixDuplicatingTasksBetweenWorkers(){

        Map<Task, Integer> taskManager = new TreeMap<>();
        Map<Task, List<Employee>> workerManager = new TreeMap<>();

        int count = 0;
        for (Map.Entry<Employee, List<Task>> entry : employeeTasks.entrySet()){
            for(Task task : entry.getValue()){
                if(taskManager.get(task) == null)
                    taskManager.put(task, 1);
                else {
                    count = taskManager.get(task);
                    count++;
                    taskManager.put(task, count);
                }

                if(workerManager.get(task) == null){
                    List<Employee> w = new ArrayList<>();
                    w.add(entry.getKey());
                    workerManager.put(task, w);
                } else{
                    workerManager.get(task).add(entry.getKey());
                }
            }
        }

        boolean cleared = false;
        for (Map.Entry<Task, Integer> entry : taskManager.entrySet()){
            if(entry.getKey().workerCount > entry.getValue()){
                System.out.println("Not enough workers for task: ");
            } else if(entry.getKey().workerCount < entry.getValue()){
                cleared = true;
                List<Employee> disposable = workerManager.get(entry.getKey());
                for(int i = 0; i < entry.getValue() - entry.getKey().workerCount; i++){

                    Employee removeFrom = disposable.get((int)Math.random() * disposable.size());
                    employeeTasks.get(removeFrom).remove(entry.getKey());
                    disposable.remove(removeFrom);
                }
            }
        }
    }

    private List<Employee> NotEnoughWorkingHours(){

        List<Employee> needHours = new ArrayList<>();

        for (Map.Entry<Employee, List<Task>> entry : employeeTasks.entrySet()){
            Employee worker = entry.getKey();
            List<Task> values = entry.getValue();

            int minutesPerDay = 0;
            int MIN_MINUTES_PER_DAY = 3 * 60;
            int lastWorkDay = 0;
            for(Task task : values){

                if(lastWorkDay == task.startDate.getDate()){
                    minutesPerDay += task.duration;
                } else {
                    if(lastWorkDay != 0 && minutesPerDay < MIN_MINUTES_PER_DAY){
                        needHours.add(worker);
                    }
                    minutesPerDay = task.duration;
                }

                lastWorkDay = task.startDate.getDate();
            }
        }

        return needHours;
    }

    private void TryAddingTasksToShortHourWorkers(List<Employee> needHours){

        List<Employee> addedTasks = new ArrayList<>();
        Collections.sort(unfinishedTasks, Task.Comparators.WORKERCOUNT);
        for(Employee worker : needHours){
            List<Task> oneWorkerTasks = new ArrayList<>();
            for(Task task : unfinishedTasks){
                if(task.workerCount == 1 && worker.WorkPlaces.contains(task.id)){
                    oneWorkerTasks.add(task);
                }
            }
            for(Task task : oneWorkerTasks){
                if(CanPlaceTask(worker, task)){
                    unfinishedTasks.remove(task);
                    finishedTasks.add(task);
                    addedTasks.add(worker);
                    employeeTasks.get(worker).add(task);
                }
            }
        }
    }

    /**
     * Function to check if you can place a task to already existing task set
     */
    private boolean CanPlaceTask(Employee worker, Task task){

        for(Task originalTask : employeeTasks.get(worker)){
            if(DoesIntersect(task, originalTask)){
                return false;
            }
        }

        if(RequirementsNotValid(worker, task)){
            return false;
        }

        return true;
    }

    /**
     * Function to check if requirements for a new task are valid
     * @param worker worker
     * @param task new task
     * @return
     */
    private boolean RequirementsNotValid(Employee worker, Task task){

        List<Task> testingTasks = new ArrayList<>(employeeTasks.get(worker));
        testingTasks.add(task);

        Collections.sort(testingTasks, Task.Comparators.STARTTIME);

        int workingTime = 0;
        int lastWorkDay = 0;
        int workingTimePerDay = 0;
        String lastWorkId = "";
        int lastWorkWeek = 0;
        int WorkingTimePerWeek = 0;
        int MAX_MINUTES_PER_WEEK = 120 * 60;
        //--------------------------
        int maxWorkingHours = 0;
        int lastWorkHours = 0;
        int lastWorkMinutes = 0;

        for(Task test : testingTasks){
            //CHECK TO SEE IF WORKER IS WORKING DURING THE NIGHT
            if (test.startDate.getHours() >= 22 ||test.startDate.getHours() < 6){
                maxWorkingHours = 8;
            }else {
                maxWorkingHours = 12;
            }

            workingTime += test.duration;

            if(workingTime > worker.maxHoursPerMonth * 60){
                return true;
            }

            if(lastWorkDay == test.startDate.getDate()){
                workingTimePerDay += test.duration;
                //CHECK TO SEE IF MAX WORKING HOURS AREN'T EXCEEDED
                if(workingTimePerDay > maxWorkingHours * 60){
                    return true;
                }

            } else {

                workingTimePerDay = test.duration;
                //CHECK IF THERE IS 12-HOUR BREAK
                if (test.startDate.getDate()*24*60+test.startDate.getHours() * 60 + test.startDate.getMinutes() -
                        (lastWorkDay*24*60 + lastWorkHours * 60 + lastWorkMinutes) < 12 * 60){
                    return true;
                }
                if(worker.WorkPlaces.contains(test.id)){
                    lastWorkId = test.id;
                } else {
                    //CHECK TO SEE IF WORKER DOESN'T HAVE MORE THAN 2 ID TASKS OVER ALL
                    if(worker.WorkPlaces.size() < 2){
                        lastWorkId = test.id;
                        worker.WorkPlaces.add(test.id);
                    } else {
                        return true;
                    }
                }
            }

            //
            if(lastWorkWeek != (test.startDate.getDate() - 1) / 7){ // if day is a new weekday

                WorkingTimePerWeek = test.duration;
            } else {
                WorkingTimePerWeek += task.duration;
            }
            //CHECK TO SEE IF WEEK DOESN'T EXCEED 48 HOURS
            if(WorkingTimePerWeek > MAX_MINUTES_PER_WEEK){
                return true;
            }

            lastWorkDay = test.startDate.getDate();
            lastWorkHours = test.startDate.getHours();
            lastWorkMinutes = test.startDate.getMinutes();
        }

        return false;

    }

    private boolean DoesIntersect(Task next, Task first){
        //if the next task starts and end in the middle of the first task
        if(first.startDate.compareTo(next.startDate) <= 0 && first.endDate.compareTo(next.endDate) >= 0)
            return true;

        //if the next task start in the middle of the first task
        if(first.startDate.compareTo(next.startDate) <= 0 && first.endDate.compareTo(next.endDate) <= 0 && first.endDate.compareTo(next.startDate) >= 0)
            return true;

        //if the next task ends in the middle of the first task
        if(first.startDate.compareTo(next.startDate) >= 0 && first.endDate.compareTo(next.endDate) >= 0 && first.startDate.compareTo(next.endDate) <= 0)
            return true;

        //if the first task is in the middle of the next task
        if(first.startDate.compareTo(next.startDate) >= 0 && first.startDate.compareTo(next.endDate) <= 0)
            return true;

        //if the first task ended in the middle of the next task
        if(first.endDate.compareTo(next.endDate) <= 0 && first.endDate.compareTo(next.startDate) >= 0)
            return true;

        return false;
    }

    /**
     * Could be improved by adding more requirements and decreasing the value
     * And also letting the scheduler get tasks at random
     * That would let the algorithm get good tasks eventually be sorted
     * But that would take time for the complex evolving
     */
    public void calcFitness(){
        double value = finishedTasks.size()*100;

        for(Employee worker : employees){
            double workingHours = 0;
            for(Task task : employeeTasks.get(worker)){
                workingHours += task.duration;
            }
            workingHours /= 60;

            if(workingHours > worker.maxHoursPerMonth){
                value = value * Math.pow((double)(worker.maxHoursPerMonth / workingHours), 2);
            }
            if(workingHours < worker.maxHoursPerMonth){
                value = value * Math.pow((double)(workingHours / worker.maxHoursPerMonth), 2);
            }
        }
        this.fitness = value;
    }

    public Schedule crossover(Schedule parent){

        int crossover = (int)(Math.random() * employeeTasks.size());
        //System.out.println("Crossover: " + crossover);
        List<Employee> motherWorkers = new ArrayList<>();
        List<List<Task>> motherTasks = new ArrayList<>();
        List<Employee> fatherWorkers = new ArrayList<>();
        List<List<Task>> fatherTasks = new ArrayList<>();
        int count = 1;
        for (Map.Entry<Employee, List<Task>> entry : employeeTasks.entrySet()){

            if(count > crossover){
                break;
            }

            motherWorkers.add(entry.getKey());
            motherTasks.add(entry.getValue());

            count++;
        }

        count = 1;
        for (Map.Entry<Employee, List<Task>> entry : parent.employeeTasks.entrySet()){
            if(count > crossover){
                fatherWorkers.add(entry.getKey());
                fatherTasks.add(entry.getValue());
            }
            count++;
        }

        Schedule child = new Schedule(motherWorkers, motherTasks, fatherWorkers, fatherTasks, tasks);

        return child;
    }

    public void mutate(double rate){
        for (Map.Entry<Employee, List<Task>> entry : employeeTasks.entrySet()){
            Employee worker = entry.getKey();
            List<Task> values = entry.getValue();

            boolean mutated = false;
            for(int i = 0; i < values.size(); i++){
                if((Math.random()*100) < rate){
                    values.set(i, unfinishedTasks.get((int)(Math.random() * unfinishedTasks.size())));
                    mutated = true;
                }
            }

            if(mutated){
                employeeTasks.put(worker, values);
            }
        }

        FixMutatedSchedule();
    }
}
