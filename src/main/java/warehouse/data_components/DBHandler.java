package warehouse.data_components;

import org.apache.poi.ss.usermodel.DateUtil;
import org.sqlite.date.DateFormatUtils;
import warehouse.data_components.data_elements.*;

import java.sql.*;
import java.util.*;
import java.util.Date;

import static warehouse.ResourcesList.*;
import static warehouse.data_components.DocumentTypes.*;

public class DBHandler {

    private static DBHandler dbHandler = new DBHandler();

    private Connection connection;
    private LinkedList<Statement> stmtList;

    //Объект для выполнения sql-запросов, которые не могут быть подготовлены заранее
    private Statement statement;

    //Подготавливаемые заранее sql-запросы
    private PreparedStatement getCatalogStmt;
    private PreparedStatement getContractorsStmt;
    private PreparedStatement getDocumentsStmt;
    private PreparedStatement getDocumentOperationsStmt;
    private PreparedStatement addCatalogElementStmt;
    private PreparedStatement addContractorElementStmt;
    private PreparedStatement addDocumentStmt;
    private PreparedStatement addOperationStmt;
    private PreparedStatement getMaxIdDocumentStmt;
    private PreparedStatement validateOperatonStmt;

    private PreparedStatement editCatalogElementStmt;
    private PreparedStatement editContractroElementStmt;
    private PreparedStatement editDocumentElementStmt;
    private PreparedStatement removeOperationStmt;

    private PreparedStatement getIncsTurnStmt;
    private PreparedStatement getDecsTurnStmt;
    private PreparedStatement getDeliveryElemntsStmt;
    private PreparedStatement getIncsDeliveryStmt;
    private PreparedStatement getDecsDeliveryStmt;

    private DBHandler() {
    }

    public static DBHandler getInstance() {
        return dbHandler;
    }

    public void initConnection() throws Exception {
        Class.forName(jdbcClassName);
        connection = DriverManager.getConnection(databaseConnectionString);
        connection.setAutoCommit(false);
        stmtList = new LinkedList<>();

        //Подготавливаем объекты для выполнения запросов
        statement = connection.createStatement();
        stmtList.add(statement);
        String query;

        query = "SELECT * FROM CATALOG";
        getCatalogStmt = connection.prepareStatement(query);
        stmtList.add(getCatalogStmt);

        query = "SELECT * FROM CONTRACTORS";
        getContractorsStmt = connection.prepareStatement(query);
        stmtList.add(getContractorsStmt);

        query = "SELECT DOCUMENTS.ID, DOCUMENTS.DATE, DOCUMENTS.TYPE, DOCUMENTS.CONTRACTOR_ID, CONTRACTORS.NAME " +
                "FROM DOCUMENTS, CONTRACTORS " +
                "WHERE DOCUMENTS.CONTRACTOR_ID=CONTRACTORS.ID";
        getDocumentsStmt = connection.prepareStatement(query);
        stmtList.add(getDocumentsStmt);

        query = "SELECT OPERATIONS.ID, CATALOG.ID, NAME, COUNT " +
                "FROM OPERATIONS, CATALOG " +
                "WHERE DOCUMENT_ID=? AND CATALOG_ID=CATALOG.ID";
        getDocumentOperationsStmt = connection.prepareStatement(query);
        stmtList.add(getDocumentOperationsStmt);

        query = "INSERT INTO CATALOG (NAME) VALUES(?)";
        addCatalogElementStmt = connection.prepareStatement(query);
        stmtList.add(addCatalogElementStmt);

        query = "INSERT INTO CONTRACTORS (NAME) VALUES(?)";
        addContractorElementStmt = connection.prepareStatement(query);
        stmtList.add(addContractorElementStmt);

        query = "INSERT INTO DOCUMENTS (DATE, TYPE, CONTRACTOR_ID) VALUES (?, ? ,?)";
        addDocumentStmt = connection.prepareStatement(query);
        stmtList.add(addDocumentStmt);

        query = "INSERT INTO OPERATIONS (DOCUMENT_ID, CATALOG_ID, COUNT) VALUES (?, ?, ?)";
        addOperationStmt = connection.prepareStatement(query);
        stmtList.add(addOperationStmt);

        query = "SELECT MAX(DOCUMENTS.ID) FROM DOCUMENTS";
        getMaxIdDocumentStmt = connection.prepareStatement(query);
        stmtList.add(getMaxIdDocumentStmt);

        query = "SELECT SUM(DOCUMENTS.TYPE*OPERATIONS.COUNT)" +
                " FROM OPERATIONS, DOCUMENTS" +
                " WHERE OPERATIONS.DOCUMENT_ID=DOCUMENTS.ID" +
                " AND OPERATIONS.CATALOG_ID=?" +
                " GROUP BY DATE" +
                " ORDER BY DATE(DOCUMENTS.DATE)";
        validateOperatonStmt = connection.prepareStatement(query);
        stmtList.add(validateOperatonStmt);

        query = "UPDATE CATALOG SET NAME=? WHERE ID=?";
        editCatalogElementStmt = connection.prepareStatement(query);
        stmtList.add(editCatalogElementStmt);

        query = "UPDATE CONTRACTORS SET NAME=? WHERE ID=?";
        editContractroElementStmt = connection.prepareStatement(query);
        stmtList.add(editContractroElementStmt);

        query = "UPDATE DOCUMENTS SET DATE=?, TYPE=?, CONTRACTOR_ID=? WHERE ID=?";
        editDocumentElementStmt = connection.prepareStatement(query);
        stmtList.add(editDocumentElementStmt);

        query = "DELETE FROM OPERATIONS WHERE DOCUMENT_ID=?";
        removeOperationStmt = connection.prepareStatement(query);
        stmtList.add(removeOperationStmt);

        query = "SELECT SUM(OPERATIONS.COUNT)" +
                " FROM OPERATIONS, DOCUMENTS" +
                " WHERE OPERATIONS.CATALOG_ID=?" +
                " AND OPERATIONS.DOCUMENT_ID=DOCUMENTS.ID" +
                " AND DOCUMENTS.TYPE=" + COM.getMul() + "" +
                " AND DATE(DOCUMENTS.DATE) BETWEEN DATE(?) AND DATE(?)";
        getIncsTurnStmt = connection.prepareStatement(query);
        stmtList.add(getIncsTurnStmt);

        query = "SELECT SUM(OPERATIONS.COUNT)" +
                " FROM OPERATIONS, DOCUMENTS" +
                " WHERE OPERATIONS.CATALOG_ID=?" +
                " AND OPERATIONS.DOCUMENT_ID=DOCUMENTS.ID" +
                " AND DOCUMENTS.TYPE=" + CONS.getMul() + "" +
                " AND DATE(DOCUMENTS.DATE) BETWEEN DATE(?) AND DATE(?)";
        getDecsTurnStmt = connection.prepareStatement(query);
        stmtList.add(getDecsTurnStmt);

        query = "SELECT CATALOG.ID, CATALOG.NAME" +
                " FROM CATALOG" +
                " WHERE EXISTS" +
                " (SELECT OPERATIONS.ID" +
                " FROM OPERATIONS, DOCUMENTS" +
                " WHERE OPERATIONS.CATALOG_ID=CATALOG.ID" +
                " AND OPERATIONS.DOCUMENT_ID=DOCUMENTS.ID" +
                " AND DOCUMENTS.CONTRACTOR_ID=?" +
                " AND DATE(DOCUMENTS.DATE) BETWEEN DATE(?) AND DATE(?))";
        getDeliveryElemntsStmt = connection.prepareStatement(query);
        stmtList.add(getDeliveryElemntsStmt);

        query = "SELECT SUM(COUNT)" +
                " FROM OPERATIONS, DOCUMENTS" +
                " WHERE DOCUMENTS.CONTRACTOR_ID=?" +
                " AND OPERATIONS.DOCUMENT_ID=DOCUMENTS.ID" +
                " AND DOCUMENTS.TYPE=?" +
                " AND OPERATIONS.CATALOG_ID=?" +
                " AND DATE(DOCUMENTS.DATE) BETWEEN DATE(?) AND DATE(?)";
        getIncsDeliveryStmt = connection.prepareStatement(query);
        stmtList.add(getIncsDeliveryStmt);

        query = "SELECT SUM(COUNT)" +
                " FROM OPERATIONS, DOCUMENTS" +
                " WHERE DOCUMENTS.CONTRACTOR_ID=?" +
                " AND OPERATIONS.DOCUMENT_ID=DOCUMENTS.ID" +
                " AND DOCUMENTS.TYPE=?" +
                " AND OPERATIONS.CATALOG_ID=?" +
                " AND DATE(DOCUMENTS.DATE) BETWEEN DATE(?) AND DATE(?)";
        getDecsDeliveryStmt = connection.prepareStatement(query);
        stmtList.add(getDecsDeliveryStmt);
    }

    public void disposeConnection() throws SQLException {
        for (Statement stmt : stmtList) {
            try {
                stmt.close();
            } catch (SQLException e) {
            }
        }
        connection.close();
    }

    public ArrayList<SimpleDataElement> getCatalog() throws SQLException {
        ResultSet resultSet = getCatalogStmt.executeQuery();
        ArrayList<SimpleDataElement> list = new ArrayList<>();

        int id;
        String name;
        while (resultSet.next()) {
            id = resultSet.getInt(1);
            name = resultSet.getString(2);
            list.add(new CatalogElement(id, name));
        }
        resultSet.close();

        return list;
    }

    public ArrayList<SimpleDataElement> getContractors() throws SQLException {
        ResultSet resultSet = getContractorsStmt.executeQuery();
        ArrayList<SimpleDataElement> list = new ArrayList<>();

        int id;
        String name;
        while (resultSet.next()) {
            id = resultSet.getInt(1);
            name = resultSet.getString(2);
            list.add(new ContractorsElement(id, name));
        }
        resultSet.close();

        return list;
    }

    public ArrayList<Document> getDocuments() throws SQLException {
        ArrayList<Document> list = new ArrayList<>();

        //Формируем список документов
        Integer id;
        Date date;
        DocumentTypes type = null;
        int contractorId;
        String contractorName;

        ResultSet resultSet = getDocumentsStmt.executeQuery();
        int typeInt;
        while (resultSet.next()) {
            id = resultSet.getInt(1);
            date = DateUtil.parseYYYYMMDDDate(resultSet.getString(2));
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
        resultSet.close();

        return list;
    }

    public void addCatalogElement(String name) throws Exception {
        addCatalogElementStmt.setString(1, name);
        try {
            addCatalogElementStmt.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            if (e.getErrorCode() == 19) throw new Exception(failItemAlreadyExists);
            throw e;
        }
        connection.commit();
    }

    public void addContractorElement(String name) throws Exception {
        addContractorElementStmt.setString(1, name);
        try {
            addContractorElementStmt.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            if (e.getErrorCode() == 19) throw new Exception(failItemAlreadyExists);
            throw e;
        }
        connection.commit();
    }

    public void addDocument(Document document) throws Exception {
        try {
            //Вносим данные документа
            addDocumentStmt.setString(1, DateFormatUtils.format(document.getDate(), "yyyy-MM-dd"));
            addDocumentStmt.setInt(2, document.getType().getMul());
            addDocumentStmt.setInt(3, document.getContractorId());
            addDocumentStmt.executeUpdate();

            //Получаем id внесенного документа
            int documentId;
            ResultSet resultSet = getMaxIdDocumentStmt.executeQuery();
            resultSet.next();
            documentId = resultSet.getInt(1);
            resultSet.close();

            //Вносим данные операций документа
            for (Operation operation : document.getOperationList()) {
                addOperationStmt.setInt(1, documentId);
                addOperationStmt.setInt(2, operation.getCatalogId());
                addOperationStmt.setInt(3, operation.getCount());
                addOperationStmt.executeUpdate();
            }

            //В случае внесения расходного документа проверяем корректность остатков
            if (document.getType() == CONS) {
                for (Operation operation : document.getOperationList()) {
                    if (!isCorrectRemaind(operation.getCatalogId())) throw new Exception("Некорректная сумма операции");
                }
            }
        } catch (Exception e) {
            connection.rollback();
            throw e;
        }

        connection.commit();
    }

    public void editCatalogElement(CatalogElement element) throws Exception {
        try {
            editCatalogElementStmt.setString(1, element.getName());
            editCatalogElementStmt.setInt(2, element.getId());
            editCatalogElementStmt.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
        connection.commit();
    }

    public void editContractorElement(ContractorsElement element) throws Exception {
        try {
            editContractroElementStmt.setString(1, element.getName());
            editContractroElementStmt.setInt(2, element.getId());
            editContractroElementStmt.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
        connection.commit();
    }

    public void editDocument(Document document) throws Exception {
        try {
            //Обновляем данные документа: поля "Дата", "Тип", "Контрагент"
            editDocumentElementStmt.setString(1, DateFormatUtils.format(document.getDate(), "yyyy-MM-dd"));
            editDocumentElementStmt.setInt(2, document.getType().getMul());
            editDocumentElementStmt.setInt(3, document.getContractorId());
            editDocumentElementStmt.setInt(4, document.getId());
            editDocumentElementStmt.executeUpdate();

            //Составляем список всех id из каталога, которые будут затронуты данной операцией
            HashSet<Integer> catalogIdSet = new HashSet<>();

            //Вносим в список все catalog id из текущих операций документа
            for (Operation operation : getDocumentOperations(document.getId())) {
                catalogIdSet.add(operation.getCatalogId());
            }
            //Вносим в список все catalog id из обновленного документа
            for (Operation operation : document.getOperationList()) {
                catalogIdSet.add(operation.getCatalogId());
            }

            //Удаляем все текущие операции
            removeOperationStmt.setInt(1, document.getId());
            removeOperationStmt.executeUpdate();

            //Вносим операции обновленного документа в БД
            for (Operation operation: document.getOperationList()){
                addOperationStmt.setInt(1, document.getId());
                addOperationStmt.setInt(2, operation.getCatalogId());
                addOperationStmt.setInt(3, operation.getCount());
                addOperationStmt.executeUpdate();
            }

            //Выполняем проверку корректности остатков
            for (Integer cataloId: catalogIdSet) {
                if (!isCorrectRemaind(cataloId)) throw new Exception("Некорректная сумма операции");
            }

        } catch (Exception e) {
            connection.rollback();
            throw e;
        }
        connection.commit();
    }

    private boolean isCorrectRemaind(int catalogId) throws SQLException {
        validateOperatonStmt.setInt(1, catalogId);
        ResultSet resultSet = validateOperatonStmt.executeQuery();
        int sum = 0;
        while (resultSet.next()) {
            sum += resultSet.getInt(1);
            if (sum < 0) {
                resultSet.close();
                return false;
            }
        }
        resultSet.close();
        return true;
    }

    public ArrayList<RemaindElement> getRemaindElements(Integer catalogId, Date endDate) throws SQLException {
        ArrayList<RemaindElement> list = new ArrayList<>();
        String query = "SELECT CATALOG.ID, CATALOG.NAME, SUM(DOCUMENTS.TYPE * OPERATIONS.COUNT)" +
                "FROM CATALOG, DOCUMENTS, OPERATIONS" +
                " WHERE OPERATIONS.CATALOG_ID=CATALOG.ID AND" +
                " OPERATIONS.DOCUMENT_ID=DOCUMENTS.ID" +
                " AND DATE(DOCUMENTS.DATE)<=DATE(\"" + DateFormatUtils.format(endDate, "yyyy-MM-dd") + "\")";

        if (catalogId != null) {
            query += " AND CATALOG.ID=" + catalogId;
        }

        query += " GROUP BY CATALOG.ID, CATALOG.NAME";

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
        resultSet.close();

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
            getIncsTurnStmt.setInt(1, currentCatalogElement.getId());
            getIncsTurnStmt.setString(2, DateFormatUtils.format(beginDate, "yyyy-MM-dd"));
            getIncsTurnStmt.setString(3, DateFormatUtils.format(endDate, "yyyy-MM-dd"));
            resultSet = getIncsTurnStmt.executeQuery();
            if (resultSet.next()) {
                incCount = resultSet.getInt(1);
            }
            if (incCount == 0) incCount = null;

            //Затем рассчитываем расход
            decCount = 0;
            getDecsTurnStmt.setInt(1, currentCatalogElement.getId());
            getDecsTurnStmt.setString(2, DateFormatUtils.format(beginDate, "yyyy-MM-dd"));
            getDecsTurnStmt.setString(3, DateFormatUtils.format(endDate, "yyyy-MM-dd"));
            resultSet = getDecsTurnStmt.executeQuery();
            if (resultSet.next()) {
                decCount = resultSet.getInt(1);
            }
            if (decCount == 0) decCount = null;

            //Рассчитываем итог на конец периода
            endCount = (beginCount == null ? 0 : beginCount) + (incCount == null ? 0 : incCount) - (decCount == null ? 0 : decCount);

            //Вносим данные в результирующий список
            list.add(new TurnElement(currentCatalogElement.getId(), currentCatalogElement.getName(), beginCount, incCount, decCount, endCount));

        }
        resultSet.close();

        return list;
    }

    public ArrayList<DeliveryElement> getDeliveryElements(Date beginDate, Date endDate, Integer contractorId) throws SQLException {
        //Получаем все позиции каталога, которые были задействаваны в операциях данного контрагента в выбранном периоде
        ArrayList<CatalogElement> catalogElements = new ArrayList<>();

        getDeliveryElemntsStmt.setInt(1, contractorId);
        getDeliveryElemntsStmt.setString(2, DateFormatUtils.format(beginDate, "yyyy-MM-dd"));
        getDeliveryElemntsStmt.setString(3, DateFormatUtils.format(endDate, "yyyy-MM-dd"));

        ResultSet resultSet = getDeliveryElemntsStmt.executeQuery();

        int catalogId;
        String catalogName;
        while (resultSet.next()) {
            catalogId = resultSet.getInt(1);
            catalogName = resultSet.getString(2);
            catalogElements.add(new CatalogElement(catalogId, catalogName));
        }

        //Для каждой найденной позиции каталога считаем сумму операций - приходных и расходных
        ArrayList<DeliveryElement> list = new ArrayList<>();

        Integer inc;
        Integer dec;
        for (CatalogElement element : catalogElements) {
            //Считаем приходные операции
            getIncsDeliveryStmt.setInt(1, contractorId);
            getIncsDeliveryStmt.setInt(2, COM.getMul());
            getIncsDeliveryStmt.setInt(3, element.getId());
            getIncsDeliveryStmt.setString(4, DateFormatUtils.format(beginDate, "yyyy-MM-dd"));
            getIncsDeliveryStmt.setString(5, DateFormatUtils.format(endDate, "yyyy-MM-dd"));
            resultSet = getIncsDeliveryStmt.executeQuery();
            inc = null;
            if (resultSet.next()) {
                inc = resultSet.getInt(1);
            }

            //Считаем расходные операции
            getDecsDeliveryStmt.setInt(1, contractorId);
            getDecsDeliveryStmt.setInt(2, CONS.getMul());
            getDecsDeliveryStmt.setInt(3, element.getId());
            getDecsDeliveryStmt.setString(4, DateFormatUtils.format(beginDate, "yyyy-MM-dd"));
            getDecsDeliveryStmt.setString(5, DateFormatUtils.format(endDate, "yyyy-MM-dd"));
            resultSet = getDecsDeliveryStmt.executeQuery();
            dec = null;
            if (resultSet.next()) {
                dec = resultSet.getInt(1);
            }

            list.add(new DeliveryElement(element.getId(), element.getName(), inc, dec));
        }
        resultSet.close();

        return list;
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
            date = DateUtil.parseYYYYMMDDDate(resultSet.getString(2));
            contractorName = resultSet.getString(3);
            documentType = DocumentTypes.getType(resultSet.getInt(4));
            catalogName = resultSet.getString(5);
            count = resultSet.getInt(6);

            element = new LogElement(documentId, date, contractorName, documentType, catalogName, count);
            list.add(element);
        }
        resultSet.close();

        return list;
    }

    private ArrayList<Operation> getDocumentOperations(int documentId) throws SQLException {
        ArrayList<Operation> list = new ArrayList<>();

        getDocumentOperationsStmt.setInt(1, documentId);
        ResultSet resultSet = getDocumentOperationsStmt.executeQuery();

        Integer id;
        int catalogId;
        String catalogName;
        int count;

        while (resultSet.next()) {
            id = resultSet.getInt(1);
            catalogId = resultSet.getInt(2);
            catalogName = resultSet.getString(3);
            count = resultSet.getInt(4);

            list.add(new Operation(id, catalogId, catalogName, count));
        }
        resultSet.close();

        return list;
    }

}
