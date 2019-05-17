package warehouse.data_components;

import java.sql.*;
import java.util.ArrayList;

import static warehouse.ResourcesList.*;

public class DBHandler {

    private Statement statement;

    public DBHandler() throws Exception {
        Class.forName(jdbcClassName);
        Connection connection = DriverManager.getConnection(databaseConnectionString);
        statement = connection.createStatement();
    }

    public ArrayList<SimpleDataElement> getCatalog() throws SQLException {
        String query = "SELECT * FROM CATALOG ORDER BY NAME";

        ResultSet resultSet = statement.executeQuery(query);
        ArrayList<SimpleDataElement> list = new ArrayList<>();

        int id;
        String name;
        while (resultSet.next()){
            id = resultSet.getInt(1);
            name = resultSet.getString(2);
            list.add(new CatalogElement(id, name));
        }

        return list;
    }

    public ArrayList<SimpleDataElement> getContractors() throws SQLException {
        String query = "SELECT * FROM CONTRACTORS ORDER BY NAME";

        ResultSet resultSet = statement.executeQuery(query);
        ArrayList<SimpleDataElement> list = new ArrayList<>();

        int id;
        String name;
        while (resultSet.next()){
            id = resultSet.getInt(1);
            name = resultSet.getString(2);
            list.add(new ContractorsElement(id, name));
        }

        return list;
    }

}
