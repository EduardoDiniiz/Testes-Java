package repositories;

import entitys.Rental;

import java.util.List;

public interface RentalRepository {

    public void save(Rental rental);

    List<Rental> getRentalPending();
}
