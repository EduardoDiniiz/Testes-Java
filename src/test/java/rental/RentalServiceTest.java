package rental;

import entitys.Movie;
import entitys.Rental;
import entitys.User;
import excepetions.MovieNotFoundException;
import excepetions.MovieWithoutStockException;
import excepetions.RentalException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import services.RentalService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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
    public void pay75PcOfValueIn3Movie() throws MovieNotFoundException, RentalException, MovieWithoutStockException {
        User user = new User("User 1");
        List<Movie> movies = new ArrayList<>();
        Movie movie01 = new Movie("Movie 1", 1, 4.0);
        Movie movie02 = new Movie("Movie 2", 1, 4.0);
        Movie movie03 = new Movie("Movie 3", 1, 4.0);
        movies.addAll(Arrays.asList(movie01, movie02, movie03));
        Rental rental = rentalService.rentalMovie(user, movies);
        Assert.assertEquals(11.0, rental.getPrice(), 0.1);

    }

    @Test
    public void pay50PcOfValueIn3Movie() throws MovieNotFoundException, RentalException, MovieWithoutStockException {
        User user = new User("User 1");
        List<Movie> movies = new ArrayList<>();
        Movie movie01 = new Movie("Movie 1", 1, 4.0);
        Movie movie02 = new Movie("Movie 2", 1, 4.0);
        Movie movie03 = new Movie("Movie 3", 1, 4.0);
        Movie movie04 = new Movie("Movie 4", 1, 4.0);
        movies.addAll(Arrays.asList(movie01, movie02, movie03, movie04));
        Rental rental = rentalService.rentalMovie(user, movies);
        Assert.assertEquals(13.0, rental.getPrice(), 0.1);

    }

    @Test
    public void pay25PcOfValueIn3Movie() throws MovieNotFoundException, RentalException, MovieWithoutStockException {
        User user = new User("User 1");
        List<Movie> movies = new ArrayList<>();
        Movie movie01 = new Movie("Movie 1", 1, 4.0);
        Movie movie02 = new Movie("Movie 2", 1, 4.0);
        Movie movie03 = new Movie("Movie 3", 1, 4.0);
        Movie movie04 = new Movie("Movie 4", 1, 4.0);
        Movie movie05 = new Movie("Movie 5", 1, 4.0);
        movies.addAll(Arrays.asList(movie01, movie02, movie03, movie04, movie05));
        Rental rental = rentalService.rentalMovie(user, movies);
        Assert.assertEquals(14.0, rental.getPrice(), 0.1);

    }

    @Test
    public void pay0PcOfValueIn3Movie() throws MovieNotFoundException, RentalException, MovieWithoutStockException {
        User user = new User("User 1");
        List<Movie> movies = new ArrayList<>();
        Movie movie01 = new Movie("Movie 1", 1, 2.0);
        Movie movie02 = new Movie("Movie 2", 1, 2.0);
        Movie movie03 = new Movie("Movie 3", 1, 2.0);
        Movie movie04 = new Movie("Movie 4", 1, 2.0);
        Movie movie05 = new Movie("Movie 5", 1, 2.0);
        Movie movie06 = new Movie("Movie 6", 1, 2.0);
        movies.addAll(Arrays.asList(movie01, movie02, movie03, movie04, movie05, movie06));
        Rental rental = rentalService.rentalMovie(user, movies);
        Assert.assertEquals(7.0, rental.getPrice(), 0.1);

    }
}
