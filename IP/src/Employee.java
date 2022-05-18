import java.util.ArrayList;
import java.util.List;

public class Employee implements Comparable<Employee>{

    /** identifier*/
    public int id;

    /** maximum working time per month */
    public int maxHoursPerMonth;

    public List<String> WorkPlaces = new ArrayList<>();

    Employee(int id, int maxHoursPerMonth){
        this.id = id;
        this.maxHoursPerMonth = maxHoursPerMonth;
    }

    public int compareTo(Employee other){
        return Integer.compare(this.id, other.id);
    }

    @Override
    public String toString(){
        return "[Worker: "+id+"]";
    }

    @Override
    public boolean equals(Object other){
        if(other == this)
            return true;
        if(!(other instanceof Employee))
            return false;

        Employee o  = (Employee) other;
        return (id == o.id) ? true : false;
    }

}
