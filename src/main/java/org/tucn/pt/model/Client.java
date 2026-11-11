package org.tucn.pt.model;

/**
 * Represents a client within the orders management system.
 * Maps to the 'client' table in the database.
 */
public class Client {
    private int id;
    private String name;
    private String email;
    private String address;
    private String phone;

    public Client(){
    }

    /**
     * Constructs a new Client with a specified ID.
     *
     * @param id      The unique integer identifier of the client.
     * @param name    The full name of the client.
     * @param address The physical address of the client.
     * @param email   The email address of the client.
     * @param phone   The 10-digit phone number of the client.
     */
    public Client(int id, String name, String address, String email, String phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }

    /**
     * Constructs a new Client without an ID (used before database insertion).
     *
     * @param name    The full name of the client.
     * @param address The physical address of the client.
     * @param email   The email address of the client.
     * @param phone   The 10-digit phone number of the client.
     */
    public Client(String name, String address, String email, String phone) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Returns a string representation of the Client object.
     *
     * @return A string containing the client's ID, name, address, email, and phone.
     */
    @Override
    public String toString(){
        return "Client [id=" + id +", name=" + name + ", address=" + address + ", email" + email + ", phone=" + phone + "]";
    }
}
