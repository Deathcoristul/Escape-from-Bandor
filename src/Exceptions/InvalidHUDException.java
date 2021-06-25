package Exceptions;

public class InvalidHUDException extends Exception{
    public InvalidHUDException()
    {
        super("Invalid image for HUD");
    }
}
