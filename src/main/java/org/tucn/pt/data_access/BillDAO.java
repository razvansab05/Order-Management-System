package org.tucn.pt.data_access;

import org.tucn.pt.Connection.ConnectionFactory;
import org.tucn.pt.model.Bill;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles specialized database access operations for the Bill log.
 * Bills bypass the generic AbstractDAO because they are insert-only records.
 */
public class BillDAO {
    private static final Logger LOGGER = Logger.getLogger(BillDAO.class.getName());
    private static final String INSERT_QUERY = "INSERT INTO bill (orderid, clientname, productname, quantity, totalprice) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM bill";

    /**
     * Inserts a newly generated bill into the database log.
     *
     * @param bill The bill record to insert.
     * @return The inserted bill with its auto-generated database ID applied.
     */
    public Bill insert(Bill bill){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try{
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, bill.orderId());
            statement.setString(2, bill.clientName());
            statement.setString(3, bill.productName());
            statement.setInt(4, bill.quantity());
            statement.setDouble(5, bill.totalPrice());
            statement.executeUpdate();
            generatedKeys = statement.getGeneratedKeys();

            if(generatedKeys.next()){
                int generatedId = generatedKeys.getInt(1);
                return new Bill(generatedId, bill.orderId(), bill.clientName(), bill.productName(), bill.quantity(), bill.totalPrice());
            }
        } catch (SQLException e){
            LOGGER.log(Level.WARNING, "BillDAO: insert " + e.getMessage());
        } finally {
            ConnectionFactory.close(generatedKeys);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return bill;
    }

    /**
     * Retrieves all bill records from the log table.
     *
     * @return A list of all Bill records.
     */
    public List<Bill> findAll(){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Bill> bills = new ArrayList<>();

        try{
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(SELECT_ALL_QUERY);
            resultSet = statement.executeQuery();

            while(resultSet.next()){
                bills.add(new Bill(
                        resultSet.getInt("id"),
                        resultSet.getInt("orderid"),
                        resultSet.getString("clientname"),
                        resultSet.getString("productname"),
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("totalprice")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "BillDAO: findAll " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return bills;
    }
}
