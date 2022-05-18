import java.util.Comparator;
import java.util.Date;

public class Task implements Comparable<Task>{

    public Date startDate;
    public Date endDate;
    public String id;
    public int workerCount;
    public int duration;

    public Task(Date date, String id, int wc, int dur){
        endDate = date;
        startDate = new Date(endDate.getTime() - dur*60*1000);
        this.id = id;
        workerCount = wc;
        duration = dur;
    }

    public int compareTo(Task other){
        return id.compareTo(other.id);
    }

    public static class Comparators {
        public static Comparator<Task> STARTTIME = new Comparator<Task>() {
            public int compare(Task o1, Task o2) {
                return (int)((o1.startDate.getTime() - o2.startDate.getTime())/1000/60);
            }
        };
        public static Comparator<Task> WORKERCOUNT = new Comparator<Task>() {
            public int compare(Task o1, Task o2){
                return o1.workerCount - o2.workerCount;
            }
        };
        public static Comparator<Task> ID = new Comparator<Task>() {
            public int compare(Task o1, Task o2){
                return o1.compareTo(o2);
            }
        };
    }

    @Override
    public String toString() {
        return "Task [id: " + id + ", Start: " + startDate + ", End: " + endDate + ", Duration: " + duration + "]";
    }

    @Override
    public boolean equals(Object other){
        if(other == this)
            return true;
        if(!(other instanceof Task))
            return false;

        Task o  = (Task) other;
        return (id.compareTo(o.id) == 0 && endDate.compareTo(o.endDate) == 0 && workerCount == o.workerCount && duration == o.duration) ? true : false;
    }
}
