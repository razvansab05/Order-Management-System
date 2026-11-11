package org.tucn.pt.business_logic.validators;

import org.tucn.pt.model.Client;

import java.util.regex.Pattern;

/**
 * Validates that a client's email address matches a standard format.
 */
public class EmailValidator implements Validator<Client>{
    private static final String EMAIL_PATTERN =  "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    /**
     * Validates the email format of the given client.
     *
     * @param c The client whose email is to be validated.
     * @throws IllegalArgumentException if the email is null or does not match the regex pattern.
     */
    public void validate(Client c){
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        if(c.getEmail() == null || !pattern.matcher(c.getEmail()).matches()){
            throw new IllegalArgumentException("Invalid email address");
        }
    }
}
