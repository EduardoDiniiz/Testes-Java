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
import org.mockito.*;
import repositories.RentalRepository;
import services.EmailService;
import services.RentalService;
import services.SPCService;
import utils.DateUtils;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utils.DateUtils.isMesmaData;
import static utils.DateUtils.obterDataComDiferencaDias;

public class RentalServiceTest {
    @InjectMocks
    private RentalService rentalService;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private SPCService spcService;
    @Mock
    private EmailService emailService;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    // init services and mocks
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRental() throws MovieWithoutStockException, RentalException, MovieNotFoundException {
        Assume.assumeFalse(DateUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
        // scenario

        User user = new User("User 01");
        List<Movie> movies = new ArrayList<>();
        Movie movie = new Movie("Movie 2", 1, 4.0);
        movies.add(movie);
        // action

        Rental rental = rentalService.rentalMovie(user, movies);

        // check
        error.checkThat(rental.getPrice(), is(equalTo(4.0)));
        error.checkThat(isMesmaData(rental.getDateRental(), new Date()), is(true));
        error.checkThat(rental.getUser(), is(equalTo(user)));
        error.checkThat(isMesmaData(rental.getDateReturn(), obterDataComDiferencaDias(1)), is(true));
    }

    @Test(expected = MovieWithoutStockException.class)
    public void testRental_movieWithoutStock() throws Exception {
        // scenario
        User user = new User("User 1");
        List<Movie> movies = new ArrayList<>();
        Movie movie = new Movie("Movie 2", 0, 4.0);
        movies.add(movie);
        // action
        rentalService.rentalMovie(user, movies);
    }

    @Test
    public void testRental_userEmpty() throws MovieWithoutStockException, MovieNotFoundException {
        // scenario
        Movie movie = new Movie("Movie 2", 1, 4.0);
        List<Movie> movies = new ArrayList<>();
        movies.add(movie);

        // action
        try {
            rentalService.rentalMovie(null, movies);
            Assert.fail();
        } catch (RentalException e) {
            // verify
            assertThat(e.getMessage(), is("Usuario vazio"));
        }
    }

    @Test(expected = MovieWithoutStockException.class)
    public void testRental_movieEmpty() throws MovieWithoutStockException, MovieNotFoundException, RentalException {
        // scenario
        User user = new User("User 1");

        // action
        rentalService.rentalMovie(user, null);
    }

    @Test(expected = MovieNotFoundException.class)
    public void testRentalNotFoundMovie() throws MovieNotFoundException, RentalException, MovieWithoutStockException {
        // scenario
        List<Movie> movies = new ArrayList<>();
        User user = new User("User 1");

        // action
        rentalService.rentalMovie(user, movies);
    }

    @Test
    public void notReturningMovieOnSunday() throws MovieNotFoundException, RentalException, MovieWithoutStockException {
        Assume.assumeTrue(DateUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
        // scenario
        User user = new User("User 1");
        List<Movie> movies = new ArrayList<>();
        Movie movie01 = new Movie("Movie 1", 1, 2.0);
        movies.add(movie01);

        // action
        Rental rental = rentalService.rentalMovie(user, movies);

        // verify
        boolean isMonday = DateUtils.verificarDiaSemana(rental.getDateReturn(), Calendar.MONDAY);
        assertTrue(isMonday);
    }

    @Test(expected = RentalException.class)
    public void notAllowNegativeUser() throws Exception {
        // scenario
        User user = new User("User 1");
        List<Movie> movies = new ArrayList<>();
        Movie movie01 = new Movie("Movie 1", 1, 2.0);
        movies.add(movie01);
        // mock
        Mockito.when(spcService.hasNegativeName(Mockito.any(User.class))).thenReturn(true);
        // action
        rentalService.rentalMovie(user, movies);
        // verify
        Mockito.verify(spcService).hasNegativeName(user);
    }

    @Test
    public void sendEmailForRentalWithDelay() {
        // scenario
        User user = new User("User 1");
        User user2 = new User("User 2");
        Movie movie01 = new Movie("Movie 1", 1, 2.0);
        List<Rental> rentals = Arrays.asList(new Rental(user, Arrays.asList(movie01), DateUtils.obterDataComDiferencaDias(-4), DateUtils.obterDataComDiferencaDias(-2), 10.0), new Rental(user2, Arrays.asList(movie01), new Date(), DateUtils.obterDataComDiferencaDias(1), 10.0));
        // mock
        Mockito.when(rentalRepository.getRentalPending()).thenReturn(rentals);
        // action
        rentalService.notifyDelays();
        // verify
        Mockito.verify(emailService, Mockito.times(1)).notifyDelay(Mockito.any(User.class));
        Mockito.verify(emailService).notifyDelay(user);
        Mockito.verify(emailService, Mockito.never()).notifyDelay(user2);
    }

    @Test
    public void treatExceptionSPC() throws Exception {
        // scenario
        User user = new User("User 1");
        List<Movie> movies = new ArrayList<>();
        Movie movie01 = new Movie("Movie 1", 1, 2.0);
        movies.add(movie01);
        // mock
        Mockito.when(spcService.hasNegativeName(user)).thenThrow(new Exception("Problemas com SPC, tente novamente"));
        //verify
        exception.expect(RentalException.class);
        exception.expectMessage("Problemas com SPC, tente novamente");
        // action
        this.rentalService.rentalMovie(user, movies);

    }

    @Test
    public void extendRental() {
        // scenario
        User user = new User("User 1");
        List<Movie> movies = new ArrayList<>();
        Movie movie01 = new Movie("Movie 1", 1, 2.0);
        movies.add(movie01);

        // action
        this.rentalService.extendRental(new Rental(user, movies, DateUtils.obterDataComDiferencaDias(-4), DateUtils.obterDataComDiferencaDias(-1), 2.0), 2);

        // verify
        ArgumentCaptor<Rental> argumentCaptor = ArgumentCaptor.forClass(Rental.class);
        Mockito.verify(rentalRepository).save(argumentCaptor.capture());
        Rental rentalReturn = argumentCaptor.getValue();
        error.checkThat(rentalReturn.getPrice(), is(4.0));
        error.checkThat(rentalReturn.getDateRental().getDate(), is(new Date().getDate()));
        error.checkThat(rentalReturn.getDateReturn().getDay(), is(DateUtils.obterDataComDiferencaDias(2).getDay()));

    }
}
