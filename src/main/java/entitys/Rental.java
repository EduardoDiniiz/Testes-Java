package entitys;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Rental {

    private User user;
    private List<Movie> movie;
    private Date dateRental;
    private Date dateReturn;
    private Double price;

    public Rental(User user, List<Movie> movie, Date dateRental, Date dateReturn, Double price) {
        this.user = user;
        this.movie = movie;
        this.dateRental = dateRental;
        this.dateReturn = dateReturn;
        this.price = price;
    }
}
