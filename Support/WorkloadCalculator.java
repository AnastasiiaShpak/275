package Support;

import com.company.Data;

import java.util.Arrays;
import java.util.Vector;

public class WorkloadCalculator{
    public static String getWorkload(){
        int[]levelCount = new int[6];
        Arrays.fill(levelCount, 0);

        for(int i = 0; i < Data.getBuiltDayList().getSize(); i++){
            double hours = Data.getBuiltDayList().getBuiltDays().get(i).getTotalHours();
            if(hours == 0)
                levelCount[0]++;
            else if(hours <= 4*5/7)
                levelCount[1]++;
            else if(hours <= 8*5/7)
                levelCount[2]++;
            else if(hours <= 12*5/7)
                levelCount[3]++;
            else if(hours <= 24)
                levelCount[4]++;
            else
                levelCount[5]++;
        }
        return "";
    }
}
