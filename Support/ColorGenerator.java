/*
Randomly chooses color from color list for new task in TaskList
When number of tasks is less than 12, colors are guaranteed to be distinct
If number of tasks is greater than 12, colors of tasks might be repeated

CMPT275 Project
Group 21
 */
package Support;
import java.awt.*;
import java.util.Random;
import java.util.Vector;

public class ColorGenerator {
    private static Random mRandom = new Random(System.currentTimeMillis());
    static Vector<Integer> takenColors = new Vector<>();

    public static Color generateColor() {
        Color c = null;
        if(takenColors.size() == 12){
            int validation = 0;
            while (validation != -1) {
                c = generateRandom();
                validation = getIndexFromColor(c);
            }
        }else{
            int index = mRandom.nextInt(12);
            for(int i = 0; i < 12; i++){
                if(index >= 12){
                    index -= 12;
                }
                if(!takenColors.contains(index)){
                    c = getColorFromIndex(index);
                    takenColors.add(index);
                    break;
                }
                index++;
            }
        }

        return c;
    }

    private static Color getColorFromIndex(int index){
        Color c;
        if(index == 0){
            c = new Color(246, 227, 158);
        }else if(index == 1){
            c = new Color(253, 205, 127);
        }else if(index == 2){
            c = new Color(246, 165, 93);
        }else if(index == 3){
            c = new Color(242, 129, 115);
        }else if(index == 4){
            c = new Color(241, 130, 148);
        }else if(index == 5){
            c = new Color(241, 146, 188);
        }else if(index == 6){
            c = new Color(227, 184, 212);
        }else if(index == 7){
            c = new Color(139, 140, 193);
        }else if(index == 8){
            c = new Color(150, 202, 226);
        }else if(index == 9){
            c = new Color(162, 217, 216);
        }else if(index == 10){
            c = new Color(153, 208, 170);
        }else{
            c = new Color(202, 223, 141);
        }
        return c;
    }

    private static Color generateRandom(){
        int red;
        int green;
        int blue;
        int decider = mRandom.nextInt(6);
        if(decider == 0){
            red = ((mRandom.nextInt(50) + 130));
            green = ((mRandom.nextInt(50) + 204));
            blue = ((mRandom.nextInt(50) + 204));
        }else if(decider == 1){
            red = ((mRandom.nextInt(50) + 204));
            green = ((mRandom.nextInt(50) + 130));
            blue = ((mRandom.nextInt(50) + 204));
        }else if(decider == 2){
            red = ((mRandom.nextInt(50) + 204));
            green = ((mRandom.nextInt(50) + 204));
            blue = ((mRandom.nextInt(50) + 130));
        }else if(decider == 3){
            red = ((mRandom.nextInt(50) + 130));
            green = ((mRandom.nextInt(50) + 130));
            blue = ((mRandom.nextInt(50) + 204));
        }else if(decider == 4){
            red = ((mRandom.nextInt(50) + 130));
            green = ((mRandom.nextInt(50) + 204));
            blue = ((mRandom.nextInt(50) + 130));
        }else{
            red = ((mRandom.nextInt(50) + 204));
            green = ((mRandom.nextInt(50) + 130));
            blue = ((mRandom.nextInt(50) + 130));
        }
        return new Color(red, green, blue);
    }

    //make color available for new tasks
    public static void freeColor(Color c){
      int index = getIndexFromColor(c);
      if(index != -1){
          takenColors.removeElement(index);
      }
    }

    //return -1 is color is not in the list
    static int getIndexFromColor(Color c){
        int index;
        if(c == null)
            return -1;

        if(c.equals(new Color(246, 227, 158) )){
            index = 0;
        }else if(c.equals(new Color(253, 205, 127))){
            index = 1;
        }else if(c.equals(new Color(246, 165, 93))){
            index = 2;
        }else if(c.equals(new Color(242, 129, 115))){
            index = 3;
        }else if(c.equals(new Color(241, 130, 148))){
            index = 4;
        }else if(c.equals(new Color(241, 146, 188))){
            index = 5;
        }else if(c.equals(new Color(227, 184, 212))){
            index = 6;
        }else if(c.equals(new Color(139, 140, 193))){
            index = 7;
        }else if(c.equals(new Color(150, 202, 226))){
            index = 8;
        }else if(c.equals(new Color(162, 217, 216))){
            index = 9;
        }else if(c.equals(new Color(153, 208, 170))){
            index = 10;
        }else if(c.equals(new Color(202, 223, 141))){
            index = 11;
        }else{
            return -1;
        }
        return index;
    }
}
