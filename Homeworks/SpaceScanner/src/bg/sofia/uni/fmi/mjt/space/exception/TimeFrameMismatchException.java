package bg.sofia.uni.fmi.mjt.space.exception;

public class TimeFrameMismatchException extends RuntimeException {

    public TimeFrameMismatchException(String message) {
        super(message);
    }

    public TimeFrameMismatchException(String message, Throwable t) {
        super(message, t);
    }
}
