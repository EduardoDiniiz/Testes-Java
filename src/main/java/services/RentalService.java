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

        Rental rental = new Rental(user, movies, new Date(), DateUtils.adicionarDias(new Date(), 1), this.calculatorPayment(movies));

        return rental;
    }

    public Double calculatorPayment(List<Movie> movies) {
        Double priceTotal = 0.0;
        Double priceMovie = 0.0;
        for (int i = 0; i < movies.size(); i++) {
            priceMovie = movies.get(i).getPriceRental();
            switch (i) {
                case 2:
                    priceMovie = priceMovie * 0.75;
                    break;
                case 3:
                    priceMovie = priceMovie * 0.50;
                    break;
                case 4:
                    priceMovie = priceMovie * 0.25;
                    break;
                case 5:
                    priceMovie = priceMovie * 0.00;
                    break;
            }
            priceTotal += priceMovie;
        }

        return priceTotal;
    }
}
