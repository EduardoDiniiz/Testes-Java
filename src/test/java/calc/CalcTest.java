package calc;

import calculator.Calculator;
import excepetions.NotDivideByZeroException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CalcTest {

    private Calculator calculator;

    @Before
    public void setup(){
        calculator = new Calculator();
    }

    @Test
    public void sumTwoNumbers(){

        int a = 5;
        int b = 3;

        int result = calculator.sum(a, b);

        Assert.assertEquals(8, result);
    }

    @Test
    public void subtractTwoNumbers(){

        int a = 8;
        int b = 5;

        int result = calculator.subtract(a, b);

        Assert.assertEquals(3, result);
    }

    @Test
    public void divideTwoNumbers() throws NotDivideByZeroException {
        int a = 10;
        int b = 2;

        int result = calculator.divideTwoNumbers(a, b);

        Assert.assertEquals(5, result);
    }

    @Test(expected = NotDivideByZeroException.class)
    public void divideByZeroTest() throws NotDivideByZeroException {
        int a = 10;
        int b = 0;

        int result = calculator.divideTwoNumbers(a, b);
    }
}
