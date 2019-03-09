package services;

import entitys.Movie;
import entitys.Rental;
import entitys.User;
import excepetions.MovieNotFoundException;
import excepetions.MovieWithoutStockException;
import excepetions.RentalException;
import utils.DateUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class RentalService {

    public Rental rentalMovie(User user, List<Movie> movies) throws RentalException, MovieWithoutStockException, MovieNotFoundException {
        if (user == null) {
            throw new RentalException("Usuario vazio");
        }

        if (movies == null) {
            throw new MovieWithoutStockException("Filme vazio");
        }

        if (movies.size() == 0) {
            throw new MovieNotFoundException("Nenhum filme associado a esse aluguel!");
        }

        for (Movie movie : movies) {
            if (movie.getStock() == 0) {
                throw new MovieWithoutStockException();
            }
        }

        Double price = 0.0;

        for (Movie movie : movies) {
            price += movie.getPriceRental();
        }

        Rental rental = new Rental(user, movies, new Date(), DateUtils.adicionarDias(new Date(), 1), price);

        return rental;
    }
}
