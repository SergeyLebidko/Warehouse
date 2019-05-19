package warehouse.data_components;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

import static warehouse.ResourcesList.*;
import static warehouse.data_components.DocumentTypes.*;

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
        while (resultSet.next()) {
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
        while (resultSet.next()) {
            id = resultSet.getInt(1);
            name = resultSet.getString(2);
            list.add(new ContractorsElement(id, name));
        }

        return list;
    }

    public ArrayList<Document> getDocumentList() throws SQLException {
        ArrayList<Document> list = new ArrayList<>();

        String query = "SELECT DOCUMENTS.ID, DATE, TYPE, CONTRACTORS.ID, NAME " +
                "FROM DOCUMENTS, CONTRACTORS " +
                "WHERE CONTRACTORS.ID=CONTRACTOR_ID " +
                "ORDER BY DATE(DATE)";

        System.out.println(query);

        Document document;

        Integer id;
        Date date;
        DocumentTypes type = null;
        int contractorId;
        String contractorName;

        ResultSet resultSet = statement.executeQuery(query);

        int typeInt;
        ArrayList<Operation> opList;
        while (resultSet.next()) {
            id = resultSet.getInt(1);
            date = convertStringToDate(resultSet.getString(2));
            typeInt = resultSet.getInt(3);
            if (typeInt == (-1)) type = CONS;
            if (typeInt == (1)) type = COM;
            contractorId = resultSet.getInt(4);
            contractorName = resultSet.getString(5);

            document = new Document(id, date, type, contractorId, contractorName);
            opList = getOperationList(id);
            document.getOperationList().addAll(opList);

            list.add(document);
        }

        return list;
    }

    public ArrayList<Operation> getOperationList(int documentId) throws SQLException {
        ArrayList<Operation> list = new ArrayList<>();

        String query = "SELECT OPERATIONS.ID, CATALOG.ID, NAME, COUNT " +
                "FROM OPERATIONS, CATALOG " +
                "WHERE DOCUMENT_ID="+documentId+" AND CATALOG_ID=CATALOG.ID " +
                "ORDER BY NAME";

        ResultSet resultSet = statement.executeQuery(query);

        Integer id;
        int catalogId;
        String catalogName;
        int count;

        while (resultSet.next()){
            id = resultSet.getInt(1);
            catalogId = resultSet.getInt(2);
            catalogName = resultSet.getString(3);
            count = resultSet.getInt(4);

            list.add(new Operation(id, documentId, catalogId, catalogName, count));
        }

        return list;
    }

    private Date convertStringToDate(String dateStr) {
        Date result;

        int year;
        int month;
        int day;

        String yearStr;
        String monthStr;
        String dayStr;

        String[] dateArr = dateStr.split("-");
        yearStr = dateArr[0];
        monthStr = dateArr[1];
        dayStr = dateArr[2];

        year = Integer.parseInt(yearStr) - 1900;
        month = Integer.parseInt(monthStr) - 1;
        day = Integer.parseInt(dayStr);

        result = new Date(year, month, day);

        return result;
    }

}
