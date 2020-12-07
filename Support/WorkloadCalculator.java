/*
Evaluates schedule level of difficulty in one phrase
Should be called only after schedule is built

CMPT275 Project
Group 21
 */

package Support;
import com.company.Data;
import java.util.Arrays;


public class WorkloadCalculator{

    public static String getWorkload(){
        int[]levelCount = new int[6];
        Arrays.fill(levelCount, 0);

        if(Data.getBuiltDayList() == null|| Data.getBuiltDayList().getSize() == 0){
            return "";
        }

        for(int i = 0; i < Data.getBuiltDayList().getSize(); i++){
            double hours = Data.getBuiltDayList().getBuiltDays().get(i).getTotalHours();
            if(hours == 0)
                levelCount[0]++;
            else if(hours <= 4.0*5.0/7.0)
                levelCount[1]++;
            else if(hours <= 8.0*5.0/7.0)
                levelCount[2]++;
            else if(hours <= 12.0*5.0/7.0)
                levelCount[3]++;
            else if(hours <= 24)
                levelCount[4]++;
            else
                levelCount[5]++;
        }
        double totalDays = Data.getBuiltDayList().getSize();
        String workload = "";

        //case 1: 100% mono schedule
        for(int i = 0; i < levelCount.length; i++){
            if(levelCount[i]/totalDays == 1){
                workload = assignNameToLevel(i);
                break;
            }
        }

        //case 2: 90% mono schedule
        if(workload.equals("")){
            for(int i = 0; i < levelCount.length; i++){
                if(levelCount[i]/totalDays >= 0.9){
                    workload = "mostly " + assignNameToLevel(i);
                    break;
                }
            }
        }

        //case 3: half-half schedule
        if(workload.equals("")){
            int zeroCount = 0;
            for(int level: levelCount){
                if(level == 0)
                    zeroCount++;
            }
            if(zeroCount == 4){
                int biggest = -1;
                int smallest = -1;
                for(int i = 0; i < levelCount.length; i++){
                    if(levelCount[i] != 0){
                        if(biggest == -1){
                            biggest = i;
                        }else{
                            smallest = i;
                        }
                    }
                }
                if(levelCount[biggest] < levelCount[smallest]){
                    int temp = biggest;
                    biggest = smallest;
                    smallest = temp;
                }
                workload = assignNameToLevel(biggest) + " schedule with a few " + assignNameToLevel(smallest) + " days";
            }
        }

        //case 4: No hard days and above
        if(workload.equals("")) {
            if(levelCount[3] == 0 && levelCount[4] == 0 && levelCount[5] == 0){
                workload = "not too hard";
            }
        }

        //case 5: No Normal and below
        if(workload.equals("")){
            if(levelCount[2] == 0 && levelCount[1] == 0 && levelCount[0]/totalDays == 0){
                workload = "not easy at all";
            }
        }

        //case 6: non of the above, get average workload across all days
        if(workload.equals("")){
            String averageWorkload;
            double avgLoad = Data.getBuiltDayList().totalHours/totalDays;
            if(avgLoad <= 4*5.0/7.0){
                averageWorkload = "Easy-peasy";
            }else if(avgLoad <= 8*5.0/7.0){
                averageWorkload = "Normal";
            }else if(avgLoad <= 12*5.0/7.0){
                averageWorkload = "Hard";
            }else if(avgLoad <= 24*5.0/7.0){
                averageWorkload = "Ridiculous";
            }else{
                averageWorkload = "Ludicrous";
            }
            workload = averageWorkload + " on average, but big variety in days";
        }

        return "Workload: " + workload;
    }

    //level can only be number between 0 and 5 (both included)
    static String assignNameToLevel(int level){
        String name;
        if(level == 0){
            name = "free";
        }else if(level == 1){
            name = "light";
        }else if(level == 2){
            name = "normal";
        }else if(level == 3){
            name = "hard";
        }else if(level == 4){
            name = "ridiculous";
        }else{
            name = "ludicrous";
        }
        return name;
    }
}
