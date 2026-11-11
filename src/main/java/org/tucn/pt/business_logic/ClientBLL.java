package org.tucn.pt.business_logic;

import org.tucn.pt.business_logic.validators.EmailValidator;
import org.tucn.pt.business_logic.validators.PhoneValidator;
import org.tucn.pt.business_logic.validators.Validator;
import org.tucn.pt.data_access.ClientDAO;
import org.tucn.pt.model.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Encapsulates the business logic and validation rules for Client operations.
 */
public class ClientBLL {
    private List<Validator<Client>> validators;
    private ClientDAO clientDAO;

    public ClientBLL(){
        validators = new ArrayList<Validator<Client>>();
        validators.add(new EmailValidator());
        validators.add(new PhoneValidator());
        clientDAO = new ClientDAO();
    }

    /**
     * Finds a client by their ID, applying business logic checks.
     *
     * @param id The unique identifier of the client.
     * @return The located Client object.
     * @throws NoSuchElementException if the client does not exist.
     */
    public Client findClientById(int id){
        Client client = clientDAO.findById(id);
        if(client == null){
            throw new NoSuchElementException("Client with id: " + id + " was not found");
        }
        return client;
    }

    /**
     * Retrieves a list of all clients stored in the database.
     *
     * @return A list of Client objects.
     */
    public List<Client> findAllClients() { return clientDAO.findAll(); }

    /**
     * Inserts a new client into the system after validating their data.
     *
     * @param c The client to insert.
     * @return The inserted client object.
     * @throws IllegalArgumentException if the client fails validation.
     */
    public Client insertClient(Client c){
        validators.forEach(v -> v.validate(c));
        return clientDAO.insert(c);
    }

    /**
     * Validates the provided client data and updates the existing record in the database.
     *
     * @param c The client object containing updated information.
     * @return The updated client object.
     * @throws IllegalArgumentException if validation fails.
     */
    public Client updateClient(Client c){
        validators.forEach(v -> v.validate(c));
        return clientDAO.update(c);
    }

    /**
     * Deletes a client from the database.
     *
     * @param id The unique identifier of the client to delete.
     */
    public void deleteClient(int id){ clientDAO.delete(id); }

    /**
     * Extracts and returns a sorted list of all client names.
     *
     * @return A list of client names sorted alphabetically.
     */
    public List<String> getClientNames(){
        return clientDAO.findAll().stream()
                .map(Client::getName)
                .sorted()
                .collect(Collectors.toList());
    }
}
