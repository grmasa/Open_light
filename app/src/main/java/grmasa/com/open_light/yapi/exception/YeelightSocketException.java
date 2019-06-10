package grmasa.com.open_light.yapi.exception;

/**
 * Thrown when a socket error occurs
 */
public class YeelightSocketException extends Exception {
    /**
     * Constructor for the exception
     * @param cause Exception cause
     */
    public YeelightSocketException(Throwable cause) {
        super(cause);
    }
}
