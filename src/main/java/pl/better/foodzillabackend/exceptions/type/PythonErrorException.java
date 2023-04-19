package pl.better.foodzillabackend.exceptions.type;

public class PythonErrorException extends RuntimeException{
    public PythonErrorException(String message) {
        super(message);
    }
}
