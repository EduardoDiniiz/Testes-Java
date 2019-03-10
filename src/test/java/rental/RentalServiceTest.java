package rental;

import entitys.Movie;
import entitys.Rental;
import entitys.User;
import excepetions.MovieNotFoundException;
import excepetions.MovieWithoutStockException;
import excepetions.RentalException;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import services.RentalService;
import utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utils.DateUtils.isMesmaData;
import static utils.DateUtils.obterDataComDiferencaDias;

public class RentalServiceTest {

    private RentalService rentalService;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        rentalService = new RentalService();
    }

    @Test
    public void testRental() throws MovieWithoutStockException, RentalException, MovieNotFoundException {
        Assume.assumeFalse(DateUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        User user = new User("User 01");
        List<Movie> movies = new ArrayList<>();
        Movie movie = new Movie("Movie 2", 1, 4.0);
        movies.add(movie);
        Rental rental = rentalService.rentalMovie(user, movies);

        error.checkThat(rental.getPrice(), is(equalTo(4.0)));
        error.checkThat(isMesmaData(rental.getDateRental(), new Date()), is(true));
        error.checkThat(rental.getUser(), is(equalTo(user)));
        error.checkThat(isMesmaData(rental.getDateReturn(), obterDataComDiferencaDias(1)), is(true));
    }

    @Test(expected = MovieWithoutStockException.class)
    public void testRental_movieWithoutStock() throws Exception {
        //cenario
        User user = new User("User 1");
        List<Movie> movies = new ArrayList<>();
        Movie movie = new Movie("Movie 2", 0, 4.0);
        movies.add(movie);
        //acao
        rentalService.rentalMovie(user, movies);
    }

    @Test
    public void testRental_userEmpty() throws MovieWithoutStockException, MovieNotFoundException {
        //cenario
        Movie movie = new Movie("Movie 2", 1, 4.0);
        List<Movie> movies = new ArrayList<>();
        movies.add(movie);

        //acao
        try {
            rentalService.rentalMovie(null, movies);
            Assert.fail();
        } catch (RentalException e) {
            assertThat(e.getMessage(), is("Usuario vazio"));
        }
    }

    @Test(expected = MovieNotFoundException.class)
    public void testRentalNotFoundMovie() throws MovieNotFoundException, RentalException, MovieWithoutStockException {
        List<Movie> movies = new ArrayList<>();
        User user = new User("User 1");

        rentalService.rentalMovie(user, movies);
    }

    @Test
    public void notReturningMovieOnSunday() throws MovieNotFoundException, RentalException, MovieWithoutStockException {
        Assume.assumeTrue(DateUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
        User user = new User("User 1");
        List<Movie> movies = new ArrayList<>();
        Movie movie01 = new Movie("Movie 1", 1, 2.0);
        movies.add(movie01);
        Rental rental = rentalService.rentalMovie(user, movies);

        boolean isMonday = DateUtils.verificarDiaSemana(rental.getDateReturn(), Calendar.MONDAY);
        assertTrue(isMonday);
    }
}
