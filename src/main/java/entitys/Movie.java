package entitys;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Movie {

    private String name;
    private Integer stock;
    private Double priceRental;

    public Movie(String name, Integer stock, Double priceRental){
        this.name = name;
        this.stock = stock;
        this.priceRental = priceRental;
    }

}
