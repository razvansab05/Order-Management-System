package org.tucn.pt.business_logic.validators;

import org.tucn.pt.model.Client;

/**
 * Validates that a client's phone number is exactly 10 digits long.
 */
public class PhoneValidator implements Validator<Client> {

    /**
     * Validates the phone number length and format of the given client.
     *
     * @param c The client whose phone number is to be validated.
     * @throws IllegalArgumentException if the phone number is not exactly 10 digits long.
     */
    public void validate(Client c) {
        if (c.getPhone() == null || c.getPhone().length() != 10) {
            throw new IllegalArgumentException("Phone number must be exactly 10 characters long.");
        }
        if (!c.getPhone().matches("\\d+")) {
            throw new IllegalArgumentException("Phone number must contain only digits.");
        }
    }
}