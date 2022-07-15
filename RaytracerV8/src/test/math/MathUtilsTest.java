package test.math;

import org.junit.Test;
import main.math.MathUtils;
import static org.junit.Assert.assertEquals;

public class MathUtilsTest {
    private double lowDouble = 10.5, highDouble = 1500.01;
    private int lowInt = -100, highInt = 0;
    private double epsilon = 0.0001; // should have effectively no variation

    @Test
    public void testClampDouble() {
        // test low
        assertEquals(MathUtils.clampDouble(lowDouble - 1, lowDouble, highDouble), lowDouble, epsilon);

        // test high
        assertEquals(MathUtils.clampDouble(highDouble * 2, lowDouble, highDouble), highDouble, epsilon);

        // test between
        assertEquals(MathUtils.clampDouble((highDouble - lowDouble) / 2, lowDouble, highDouble), (highDouble - lowDouble) / 2, epsilon);
    }

    @Test
    public void testClampInt() {
        // test low
        assertEquals(MathUtils.clampInt(lowInt - 1, lowInt, highInt), lowInt);

        // test high
        assertEquals(MathUtils.clampInt(highInt * 2, lowInt, highInt), highInt);

        // test between
        assertEquals(MathUtils.clampInt(lowInt + 5, lowInt, highInt), lowInt + 5);
    }
}
