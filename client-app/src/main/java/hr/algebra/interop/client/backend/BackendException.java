package hr.algebra.interop.client.backend;

public class BackendException extends RuntimeException {

    private final int status;

    public BackendException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int status() {
        return status;
    }
}
