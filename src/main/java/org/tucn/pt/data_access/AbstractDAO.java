package org.tucn.pt.data_access;

import org.tucn.pt.Connection.ConnectionFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A generic Data Access Object (DAO) that uses reflection to perform
 * basic CRUD operations on a database table mapped to a model class.
 *
 * @param <T> The model class type this DAO manages.
 */
public class AbstractDAO<T> {
    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());

    private final Class<T> type;

    public AbstractDAO(){
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Dynamically determines the database table name based on the generic type.
     * Includes a safeguard to prevent conflicts with the SQL 'order' keyword.
     *
     * @return The name of the database table as a String.
     */
    private String getTableName() {
        String tableName = type.getSimpleName().toLowerCase();
        return tableName.equals("order") ? "orders" : tableName;
    }

    /**
     * Generates a SQL SELECT query with a specific filtering field.
     *
     * @param field The column name to filter by.
     * @return The generated SQL query string.
     */
    private String createSelectQuery(String field){
        StringBuilder sb = new StringBuilder();

        sb.append("SELECT * FROM ");
        sb.append(getTableName());
        sb.append(" WHERE ").append(field).append(" = ?");

        return sb.toString();
    }

    /**
     * Generates a SQL SELECT query to retrieve all records from the table.
     *
     * @return The generated SQL query string.
     */
    private String createSelectAllQuery() { return "SELECT * FROM " + getTableName(); }

    /**
     * Dynamically generates a SQL INSERT query based on the object's fields.
     *
     * @return The generated SQL query string.
     */
    private String createInsertQuery(){
        Field[] fields = type.getDeclaredFields();
        List<String> columns = new ArrayList<>();

        for(Field f : fields){
            if(!f.getName().equals("id")){
                columns.add(f.getName());
            }
        }

        String cols = columns.stream().collect(Collectors.joining(", "));
        String placeholders = columns.stream().map(c -> "?").collect(Collectors.joining(", "));
        return "INSERT INTO " + getTableName() + " (" + cols + " ) VALUES (" + placeholders + ")";
    }

    /**
     * Dynamically generates a SQL UPDATE query based on the object's fields.
     *
     * @return The generated SQL query string.
     */
    private String createUpdateQuery(){
        Field[] fields = type.getDeclaredFields();
        List<String> setClauses = new ArrayList<>();

        for(Field f : fields){
            if(!f.getName().equals("id")){
                setClauses.add(f.getName() + " = ?");
            }
        }

        String setClause = setClauses.stream().collect(Collectors.joining(", "));
        return "UPDATE " + getTableName() + " SET " + setClause + " WHERE id = ?";
    }

    /**
     * Generates a SQL DELETE query based on the ID column.
     *
     * @return The generated SQL query string.
     */
    private String createDeleteQuery() { return "DELETE FROM " + getTableName() + " WHERE id = ?"; }

    /**
     * Retrieves all records from the database table mapped to this DAO's type.
     *
     * @return A list of objects containing all table records.
     */
    public List<T> findAll(){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectAllQuery();

        try{
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            return createObjects(resultSet);
        } catch(SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO: findAll " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return new ArrayList<>();
    }

    /**
     * Retrieves a single record from the database by its ID.
     *
     * @param id The unique identifier of the record to find.
     * @return The requested object, or null if no matching record is found.
     */
    public T findById(int id){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery("id");

        try{
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            List<T> results = createObjects(resultSet);
            if(results.isEmpty()){
                return null;
            }
            return results.get(0);

        } catch(SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO: findById " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }

    /**
     * Inserts a new record into the database using the provided object's fields.
     *
     * @param t The object containing the data to be inserted.
     * @return The inserted object with its auto-generated database ID applied.
     */
    public T insert(T t){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        String query = createInsertQuery();

        try{
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            Field[] fields = type.getDeclaredFields();

            int paramIndex = 1;
            for(Field field : fields){
                if(!field.getName().equals("id")){
                    field.setAccessible(true);
                    statement.setObject(paramIndex++, field.get(t));
                }
            }
            statement.executeUpdate();
            generatedKeys = statement.getGeneratedKeys();
            if(generatedKeys.next()){
                PropertyDescriptor pd = new PropertyDescriptor("id", type);
                pd.getWriteMethod().invoke(t, generatedKeys.getInt(1));
            }
        } catch(SQLException | IllegalAccessException | IntrospectionException | InvocationTargetException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO: insert " + e.getMessage());
        } finally {
            ConnectionFactory.close(generatedKeys);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return t;
    }

    /**
     * Updates an existing record in the database matching the provided object's ID.
     *
     * @param t The object containing the updated data.
     * @return The updated object.
     */
    public T update(T t){
        Connection connection = null;
        PreparedStatement statement = null;
        String query = createUpdateQuery();

        try{
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            Field[] fields = type.getDeclaredFields();
            int paramIndex = 1;
            int idValue = 0;
            for(Field field : fields){
                field.setAccessible(true);
                if(field.getName().equals("id")){
                    idValue = (int) field.get(t);
                }
                else {
                    statement.setObject(paramIndex++, field.get(t));
                }
            }
            statement.setInt(paramIndex, idValue);
            statement.executeUpdate();
        } catch(SQLException | IllegalAccessException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO: update " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return t;
    }

    /**
     * Deletes a record from the database matching the provided ID.
     *
     * @param id The unique identifier of the record to delete.
     */
    public void delete(int id){
        Connection connection = null;
        PreparedStatement statement = null;
        String query = createDeleteQuery();

        try{
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch(SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO: delete " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }

    /**
     * Maps the rows from a SQL ResultSet to a list of Java objects using reflection.
     *
     * @param resultSet The SQL ResultSet obtained from a query execution.
     * @return A list of instantiated objects populated with the ResultSet data.
     */
    private List<T> createObjects(ResultSet resultSet){
        List<T> list = new ArrayList<>();
        Constructor[] constrs = type.getDeclaredConstructors();
        Constructor constr = null;

        for(int i = 0; i < constrs.length; i++){
            constr = constrs[i];
            if(constr.getGenericParameterTypes().length == 0){
                break;
            }
        }

        try{
            while(resultSet.next()){
                constr.setAccessible(true);
                T instance = (T) constr.newInstance();

                for(Field field : type.getDeclaredFields()){
                    String fieldName = field.getName();
                    Object value = resultSet.getObject(fieldName);
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, type);
                    Method method = propertyDescriptor.getWriteMethod();

                    if (value != null) {
                        if (field.getType() == int.class && value instanceof Number) {
                            method.invoke(instance, ((Number) value).intValue());
                        } else if (field.getType() == double.class && value instanceof Number) {
                            method.invoke(instance, ((Number) value).doubleValue());
                        } else {
                            method.invoke(instance, value);
                        }
                    }
                }
                list.add(instance);
            }
        }catch ( InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException | InvocationTargetException | SQLException | IntrospectionException e) {
            e.printStackTrace();
        }
        return list;
    }
}
