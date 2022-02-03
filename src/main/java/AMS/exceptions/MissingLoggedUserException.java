package AMS.exceptions;

public class MissingLoggedUserException extends RuntimeException{
    public MissingLoggedUserException() {
        super("User data not found");
    }
}
