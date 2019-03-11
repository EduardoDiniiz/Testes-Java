package services;

import entitys.Movie;
import entitys.Rental;
import entitys.User;
import excepetions.MovieNotFoundException;
import excepetions.MovieWithoutStockException;
import excepetions.RentalException;
import repositories.RentalRepository;
import utils.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RentalService {

    private RentalRepository rentalRepository;
    private SPCService spcService;
    private EmailService emailService;

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

        try {
            if (spcService.hasNegativeName(user)) {
                throw new RentalException("Usuário com nome negativado");
            }
        } catch (Exception e) {
            throw new RentalException("Problemas com SPC, tente novamente");
        }

        if (DateUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY)) {
            Rental rental = new Rental(user, movies, new Date(), DateUtils.adicionarDias(new Date(), 2), this.calculatorPayment(movies));
            this.rentalRepository.save(rental);
            return rental;
        } else {
            Rental rental = new Rental(user, movies, new Date(), DateUtils.adicionarDias(new Date(), 1), this.calculatorPayment(movies));
            this.rentalRepository.save(rental);
            return rental;
        }

    }

    public void notifyDelays() {
        List<Rental> rentals = rentalRepository.getRentalPending();

        for (Rental rental : rentals) {
            if (rental.getDateReturn().before(new Date())) {
                emailService.notifyDelay(rental.getUser());
            }
        }
    }

    public void extendRental(Rental rental, int dias) {
        Rental newRental = new Rental(rental.getUser(), rental.getMovie(), new Date(), DateUtils.obterDataComDiferencaDias(dias), rental.getPrice() * dias);
        rentalRepository.save(newRental);
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
