package dev.scpk.scpk.exceptions.security;

public class MissingIdForHashException extends Exception{
    public MissingIdForHashException(String message){
        super(message);
    }
}
