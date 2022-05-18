import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Utils {
    public static List<Task> getTasks(String file) throws ParseException, NumberFormatException, IOException {
        List<Task> tasks = new ArrayList<Task>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String parts[] = line.split("\\t");

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = formatter.parse(parts[0].replaceAll("\"", ""));
            int duration = Integer.parseInt(parts[1]);
            int count = Integer.parseInt(parts[2]);
            String id = parts[3].replaceAll("\"", "");

            Task task = new Task(date, id, count, duration);

            tasks.add(task);
        }
        br.close();
        return tasks;
    }

    public static List<Employee> getWorkers(int number){

        List<Employee> workers = new ArrayList<>();

        int half = number / 2;

        for(int i = 0; i < number; i++){
            Employee worker;

            if(i < half){
                worker = new Employee(i, 84);
            } else {
                worker = new Employee(i, 164);
            }

            workers.add(worker);
        }

        return workers;
    }



    public static void printResults(Schedule schedule) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer;
        for(Employee worker : schedule.employees){
            writer = new PrintWriter("result/results_" + worker.id + ".txt", "UTF-8");
            writer.println("Max hours per month: " + worker.maxHoursPerMonth);
            writer.println("Total working hours: " + schedule.totalWorkingTime(schedule.employeeTasks.get(worker)));

            List<Task> workerTasks =  schedule.employeeTasks.get(worker);
            Collections.sort(workerTasks, Task.Comparators.STARTTIME);

            for(Task task : workerTasks){
                writer.println(task);
            }
            writer.close();
        }

    }

    public static void printUnfinishedID(List<Task> unfinished, String id) throws FileNotFoundException, UnsupportedEncodingException{
        PrintWriter writer = new PrintWriter(id+".txt");
        Collections.sort(unfinished, Task.Comparators.STARTTIME);
        Collections.sort(unfinished, Task.Comparators.ID);
        for(Task task : unfinished){
            if(task.id.compareTo(id) == 0)
                writer.println(task + " Worker count: " + task.workerCount);

        }
        writer.close();
    }
}
