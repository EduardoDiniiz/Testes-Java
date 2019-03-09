package excepetions;

public class MovieWithoutStockException extends Exception {
    public MovieWithoutStockException(String msg){
        super(msg);
    }

    public MovieWithoutStockException(){
        super();
    }
}
