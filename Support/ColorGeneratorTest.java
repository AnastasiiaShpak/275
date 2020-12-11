/*
Testing for color generator
Simple getters and setters are not covered
getColorFromIndex() and getIndexFromColor() are trivial and were not covered as well
result of generateRandom() is always different and it doesn't use any outside data, therefore impossible to make test cases

CMPT275 Project
Group 21
 */
package Support;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class ColorGeneratorTest {
    @Test
    void generateColor(){
        //when task size < 12
        Color c = ColorGenerator.generateColor();
        int index = ColorGenerator.getIndexFromColor(c);
        assertTrue(index > -1 && index < 13); //color is from the  color list

        //when task size is 12+
        ColorGenerator.takenColors.setSize(12);
        Color c2 = ColorGenerator.generateColor();
        int index2 = ColorGenerator.getIndexFromColor(c2);
        assertFalse(index2 > -1 && index2 < 13); //color is not from the  color list
    }

    @Test
    void freeColor(){
        ColorGenerator.takenColors.add(0);

        //case1: color is not one of 12 colors in the list
        ColorGenerator.freeColor(new Color(0, 0, 0));
        assertEquals(ColorGenerator.takenColors.size(), 1); //color was not removed

        //case2: color is one of 12 colors in the list
        ColorGenerator.freeColor(new Color(246, 227, 158));
        assertEquals(ColorGenerator.takenColors.size(), 0); //color is removed
    }
}