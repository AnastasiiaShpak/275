/*
Calculates color for new task in TaskList
The more elements are in TaskList, the more colors get similar to each other

 */


package Support;
import com.company.Data;

import java.awt.*;
import java.util.Random;

public class ColorGenerator {
    private static Random mRandom = new Random(System.currentTimeMillis());
    public static Color generateRandomColor() {
        Color c;
        int count;
        if (Data.getTaskList().getSize() < 40){
            count = 0;
            do {
                c = generate();
                count++;
            } while (!CompareWithExisting(c, 40) && count < 30);
        }
        count = 0;
        do {
            c = generate();
            count++;
        }while(!CompareWithExisting(c, 30) && count < 30);

        count = 0;
        do {
            c = generate();
            count++;
        }while(!CompareWithExisting(c, 20) && count < 30);

        return c;
    }

    private static Color generate(){
        int red;
        int green;
        int blue;
        int decider = mRandom.nextInt(6);

        if(decider == 0){
            red = ((mRandom.nextInt(40) + 130));
            green = ((mRandom.nextInt(34) + 220));
            blue = ((mRandom.nextInt(34) + 220));
        }else if(decider == 1){
            red = ((mRandom.nextInt(34) + 220));
            green = ((mRandom.nextInt(40) + 130));
            blue = ((mRandom.nextInt(34) + 220));
        }else if(decider == 2){
            red = ((mRandom.nextInt(34) + 220));
            green = ((mRandom.nextInt(34) + 220));
            blue = ((mRandom.nextInt(40) + 130));
        }else if(decider == 3){
            red = ((mRandom.nextInt(40) + 130));
            green = ((mRandom.nextInt(40) + 130));
            blue = ((mRandom.nextInt(34) + 220));
        }else if(decider == 4){
            red = ((mRandom.nextInt(40) + 130));
            green = ((mRandom.nextInt(34) + 220));
            blue = ((mRandom.nextInt(40) + 130));
        }else{
            red = ((mRandom.nextInt(34) + 220));
            green = ((mRandom.nextInt(40) + 130));
            blue = ((mRandom.nextInt(40) + 130));
        }
        return new Color(red, green, blue);
    }

    private static boolean CompareWithExisting(Color c, int distance){
        for(int i = 0; i < Data.getTaskList().getSize(); i++){
            Color c2 = Data.getTaskList().getTasks().get(i).getColor();
            if(c2 != null && Math.abs(c2.getRed() - c.getRed()) <= distance && Math.abs(c2.getGreen() - c.getGreen()) <= distance && Math.abs(c2.getBlue() - c.getBlue()) <= distance){
                return false;
            }
        }
        return true;
    }
}
