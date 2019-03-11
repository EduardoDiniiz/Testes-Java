package suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import rental.CalculationOfRentalValues;
import rental.RentalServiceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CalculationOfRentalValues.class,
        RentalServiceTest.class
})
public class suitesTests {
}
