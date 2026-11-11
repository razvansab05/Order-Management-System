package org.tucn.pt.business_logic.validators;

/**
 * A generic interface for defining validation rules for model objects.
 *
 * @param <T> The type of object to be validated.
 */
public interface Validator<T> {
    /**
     * Validates the provided object against specific business rules.
     *
     * @param t The object to validate.
     * @throws IllegalArgumentException if the object fails validation.
     */
    public void validate(T t);
}
