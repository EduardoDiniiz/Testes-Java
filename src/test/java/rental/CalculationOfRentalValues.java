package rental;

import entitys.Movie;
import entitys.Rental;
import entitys.User;
import excepetions.MovieNotFoundException;
import excepetions.MovieWithoutStockException;
import excepetions.RentalException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import services.RentalService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

@RunWith(Parameterized.class)
public class CalculationOfRentalValues {

    private RentalService rentalService;

    @Parameterized.Parameter
    public List<Movie> movies;

    @Parameterized.Parameter(value = 1)
    public Double priceRental;

    @Parameterized.Parameter(value = 2)
    public String typeTest;

    @Before
    public void setup() {
        this.rentalService = new RentalService();
    }

    private static Movie movie01 = new Movie("Movie 1", 1, 4.0);
    private static Movie movie02 = new Movie("Movie 2", 1, 4.0);
    private static Movie movie03 = new Movie("Movie 3", 1, 4.0);
    private static Movie movie04 = new Movie("Movie 4", 1, 4.0);
    private static Movie movie05 = new Movie("Movie 5", 1, 4.0);
    private static Movie movie06 = new Movie("Movie 6", 1, 4.0);
    private static Movie movie07 = new Movie("Movie 7", 1, 4.0);


    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{
                {Arrays.asList(movie01, movie02), 8.0, "2 movies: Without discount"},
                {Arrays.asList(movie01, movie02, movie03), 11.0, "3 movies: 25%"},
                {Arrays.asList(movie01, movie02, movie03, movie04), 13.0, "4 movies: 50%"},
                {Arrays.asList(movie01, movie02, movie03, movie04, movie05), 14.0, "5 movies: 75%"},
                {Arrays.asList(movie01, movie02, movie03, movie04, movie05, movie06), 14.0, "6 movies: 100%"},
                {Arrays.asList(movie01, movie02, movie03, movie04, movie05, movie06, movie07), 18.0, "7 movies: Without discount"}
        });
    }

    @Test
    public void payWithParametersTest() throws MovieNotFoundException, RentalException, MovieWithoutStockException {
        User user = new User("User 1");

        Rental rental = rentalService.rentalMovie(user, movies);

        Assert.assertThat(rental.getPrice(), is(this.priceRental));

    }

}
