package calculator;

import excepetions.NotDivideByZeroException;

public class Calculator {

    public int sum(int a, int b){
        return a + b;
    }

    public int subtract(int a, int b) {
        return a-b;
    }

    public int divideTwoNumbers(int a, int b) throws NotDivideByZeroException {
        if (b == 0 ) {
            throw new NotDivideByZeroException("Não pode dividir por zero");
        }
        return a/b;
    }
}
