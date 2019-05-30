package warehouse.data_components;

import org.sqlite.date.DateFormatUtils;
import warehouse.data_components.data_elements.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
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

    public ArrayList<Document> getDocuments() throws SQLException {
        ArrayList<Document> list = new ArrayList<>();

        String query = "SELECT DOCUMENTS.ID, DOCUMENTS.DATE, DOCUMENTS.TYPE, DOCUMENTS.CONTRACTOR_ID, CONTRACTORS.NAME " +
                "FROM DOCUMENTS, CONTRACTORS " +
                "WHERE DOCUMENTS.CONTRACTOR_ID=CONTRACTORS.ID " +
                "ORDER BY DATE(DATE)";

        //Формируем список документов
        Integer id;
        Date date;
        DocumentTypes type = null;
        int contractorId;
        String contractorName;

        ResultSet resultSet = statement.executeQuery(query);
        int typeInt;
        while (resultSet.next()) {
            id = resultSet.getInt(1);
            date = convertStringToDate(resultSet.getString(2));
            typeInt = resultSet.getInt(3);
            if (typeInt == (-1)) type = CONS;
            if (typeInt == (1)) type = COM;
            contractorId = resultSet.getInt(4);
            contractorName = resultSet.getString(5);

            list.add(new Document(id, date, type, contractorId, contractorName));
        }

        //Заполняем для каждого документа список операций
        ArrayList<Operation> opList;
        for (Document document : list) {
            opList = getDocumentOperations(document.getId());
            document.getOperationList().addAll(opList);
        }

        return list;
    }

    public ArrayList<RemaindElement> getRemaindElements(Integer catalogId, Date endDate) throws SQLException {
        ArrayList<RemaindElement> list = new ArrayList<>();
        String query = "SELECT CATALOG.ID, CATALOG.NAME, SUM(DOCUMENTS.TYPE * OPERATIONS.COUNT) " +
                "FROM CATALOG, DOCUMENTS, OPERATIONS " +
                "WHERE OPERATIONS.CATALOG_ID=CATALOG.ID AND " +
                "OPERATIONS.DOCUMENT_ID=DOCUMENTS.ID ";

        if (catalogId != null) {
            query += " AND CATALOG.ID=" + catalogId;
        }
        if (endDate != null) {
            query += " AND DATE(DOCUMENTS.DATE)<=DATE(\"" + DateFormatUtils.format(endDate, "yyyy-MM-dd") + "\")";
        }

        query += " GROUP BY CATALOG.ID, CATALOG.NAME ORDER BY CATALOG.NAME";

        ResultSet resultSet = statement.executeQuery(query);

        int idTmp;
        String nameTmp;
        int countTmp;
        while (resultSet.next()) {
            idTmp = resultSet.getInt(1);
            nameTmp = resultSet.getString(2);
            countTmp = resultSet.getInt(3);
            list.add(new RemaindElement(idTmp, nameTmp, countTmp));
        }

        return list;
    }

    public ArrayList<TurnElement> getTurnElements(Date beginDate, Date endDate, Integer catalogId) throws SQLException {
        //Сперва получаем список позиций из каталога, по которым в указанном промежутке времени есть хоть одна операция
        ArrayList<CatalogElement> catalogElements = new ArrayList<>();
        String query = "SELECT CATALOG.ID, CATALOG.NAME" +
                " FROM CATALOG" +
                " WHERE EXISTS" +
                " (SELECT OPERATIONS.ID" +
                " FROM OPERATIONS, DOCUMENTS" +
                " WHERE OPERATIONS.DOCUMENT_ID=DOCUMENTS.ID" +
                " AND OPERATIONS.CATALOG_ID=CATALOG.ID" +
                " AND DATE(DOCUMENTS.DATE) BETWEEN DATE(\"" + DateFormatUtils.format(beginDate, "yyyy-MM-dd") + "\") AND DATE(\"" + DateFormatUtils.format(endDate, "yyyy-MM-dd") + "\"))";

        if (catalogId != null) {
            query += " AND CATALOG.ID=" + catalogId;
        }

        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            catalogElements.add(new CatalogElement(resultSet.getInt(1), resultSet.getString(2)));
        }

        //Теперь для каждой найденной на предыдущем этапе позиции каталога формируем данные по оборотам
        ArrayList<TurnElement> list = new ArrayList<>();

        String catalogName;
        Integer beginCount;
        Integer incCount;
        Integer decCount;
        Integer endCount;

        //Дата, на которую будем рассчитывать начальные остатки
        Date previosDate;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginDate);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        previosDate = calendar.getTime();

        ArrayList<RemaindElement> remaindElements;

        for (CatalogElement currentCatalogElement : catalogElements) {
            //Сперва рассчитываем начальные остатки
            remaindElements = getRemaindElements(currentCatalogElement.getId(), previosDate);
            if (remaindElements.size() > 0) {
                beginCount = remaindElements.get(0).getCount();
            } else {
                beginCount = null;
            }

            //Затем рассчитываем приход
            incCount = 0;
            query = "SELECT SUM(OPERATIONS.COUNT)" +
                    " FROM OPERATIONS, DOCUMENTS" +
                    " WHERE OPERATIONS.CATALOG_ID=" + currentCatalogElement.getId() + "" +
                    " AND OPERATIONS.DOCUMENT_ID=DOCUMENTS.ID" +
                    " AND DOCUMENTS.TYPE=" + COM.getMul() + "" +
                    " AND DATE(DOCUMENTS.DATE) BETWEEN DATE(\"" + DateFormatUtils.format(beginDate, "yyyy-MM-dd") + "\") AND DATE(\"" + DateFormatUtils.format(endDate, "yyyy-MM-dd") + "\")";
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                incCount = resultSet.getInt(1);
            }
            if (incCount == 0) incCount = null;

            //Затем рассчитываем расход
            decCount = 0;
            query = "SELECT SUM(OPERATIONS.COUNT)" +
                    " FROM OPERATIONS, DOCUMENTS" +
                    " WHERE OPERATIONS.CATALOG_ID=" + currentCatalogElement.getId() + "" +
                    " AND OPERATIONS.DOCUMENT_ID=DOCUMENTS.ID" +
                    " AND DOCUMENTS.TYPE=" + CONS.getMul() + "" +
                    " AND DATE(DOCUMENTS.DATE) BETWEEN DATE(\"" + DateFormatUtils.format(beginDate, "yyyy-MM-dd") + "\") AND DATE(\"" + DateFormatUtils.format(endDate, "yyyy-MM-dd") + "\")";
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                decCount = resultSet.getInt(1);
            }
            if (decCount==0)decCount=null;

            //Рассчитываем итог на конец периода
            endCount = (beginCount==null?0:beginCount) + (incCount == null ? 0 : incCount) - (decCount == null ? 0 : decCount);

            //Вносим данные в результирующий список
            list.add(new TurnElement(currentCatalogElement.getId(), currentCatalogElement.getName(), beginCount, incCount, decCount, endCount));

        }

        return list;
    }

    public ArrayList<DeliveryElement> getDeliveryElements(Date beginDate, Date endDate, Integer catalogId) throws SQLException{
        return null;
    }

    public ArrayList<LogElement> getLogElements(LogRequestSettings requestSettings) throws SQLException {
        ArrayList<LogElement> list = new ArrayList<>();
        String query = "SELECT DOCUMENTS.ID, DOCUMENTS.DATE, CONTRACTORS.NAME, DOCUMENTS.TYPE, CATALOG.NAME, OPERATIONS.COUNT " +
                "FROM DOCUMENTS, CONTRACTORS, CATALOG, OPERATIONS " +
                "WHERE OPERATIONS.DOCUMENT_ID=DOCUMENTS.ID AND " +
                "OPERATIONS.CATALOG_ID=CATALOG.ID AND " +
                "DOCUMENTS.CONTRACTOR_ID=CONTRACTORS.ID ";

        if (requestSettings != null) {

            Date beginDate = requestSettings.getBeginDate();
            if (beginDate != null) {
                query += "AND DATE(DOCUMENTS.DATE)>=DATE(\"" + DateFormatUtils.format(beginDate, "yyyy-MM-dd") + "\") ";
            }

            Date endDate = requestSettings.getEndDate();
            if (endDate != null) {
                query += "AND DATE(DOCUMENTS.DATE)<=DATE(\"" + DateFormatUtils.format(endDate, "yyyy-MM-dd") + "\") ";
            }

            Integer contractorId = requestSettings.getContractorId();
            if (contractorId != null) {
                query += "AND CONTRACTORS.ID=" + contractorId + " ";
            }

            DocumentTypes type = requestSettings.getDocumentType();
            if (type != null) {
                query += " AND DOCUMENTS.TYPE=" + type.getMul() + " ";
            }

            Integer catalogId = requestSettings.getCatalogId();
            if (catalogId != null) {
                query += " AND CATALOG.ID=" + catalogId + " ";
            }

        }

        query += "ORDER BY DATE(DOCUMENTS.DATE)";

        ResultSet resultSet = statement.executeQuery(query);

        int documentId;
        Date date;
        String contractorName;
        DocumentTypes documentType;
        String catalogName;
        int count;

        LogElement element;
        while (resultSet.next()) {
            documentId = resultSet.getInt(1);
            date = convertStringToDate(resultSet.getString(2));
            contractorName = resultSet.getString(3);
            documentType = DocumentTypes.getType(resultSet.getInt(4));
            catalogName = resultSet.getString(5);
            count = resultSet.getInt(6);

            element = new LogElement(documentId, date, contractorName, documentType, catalogName, count);
            list.add(element);
        }

        return list;
    }

    private ArrayList<Operation> getDocumentOperations(int documentId) throws SQLException {
        ArrayList<Operation> list = new ArrayList<>();

        String query = "SELECT OPERATIONS.ID, CATALOG.ID, NAME, COUNT " +
                "FROM OPERATIONS, CATALOG " +
                "WHERE DOCUMENT_ID=" + documentId + " AND CATALOG_ID=CATALOG.ID " +
                "ORDER BY NAME";

        ResultSet resultSet = statement.executeQuery(query);

        Integer id;
        int catalogId;
        String catalogName;
        int count;

        while (resultSet.next()) {
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
