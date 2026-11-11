package org.tucn.pt.presentation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Field;
import java.util.List;

/**
 * A utility class that uses reflection to dynamically generate and refresh
 * JTable models based on a list of arbitrary objects.
 */
public class ReflectionTableUtil {

    /**
     * Creates a JTable by dynamically extracting the field names of the provided
     * objects to serve as column headers, and populating the rows with their values.
     *
     * @param objects A list of objects to display in the table.
     * @param <T>     The type of objects in the list.
     * @return A JTable populated with the objects' data.
     */
    public static <T>JTable createTable(List<T> objects) {
        if(objects == null || objects.isEmpty()) {
            return new JTable(new DefaultTableModel());
        }

        Class<?> cls = objects.get(0).getClass();
        Field[] fields = cls.getDeclaredFields();

        String[] columnNames = new String[fields.length];
        for(int i = 0; i < fields.length; i++){
            columnNames[i] = fields[i].getName();
        }

        Object[][] data = new Object[objects.size()][fields.length];
        for(int row = 0; row < objects.size(); row++){
            for(int col = 0; col < fields.length; col++){
                fields[col].setAccessible(true);

                try{
                    data[row][col] = fields[col].get(objects.get(row));
                } catch (IllegalAccessException e){
                    data[row][col] = "N/A";
                }
            }
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {return false; }
        };

        return new JTable(model);
    }

    /**
     * Updates an existing JTable by generating a new model based on the provided object list
     * and applying it to the table.
     *
     * @param table   The JTable to update.
     * @param objects The list of objects providing the new data.
     * @param <T>     The type of objects in the list.
     */
    public static <T> void refreshTable(JTable table, List<T> objects) {
        JTable updated = createTable(objects);
        table.setModel(updated.getModel());
    }
}
