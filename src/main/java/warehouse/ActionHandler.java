package warehouse;

import javax.swing.*;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import warehouse.data_components.data_elements.*;
import warehouse.gui_components.*;
import warehouse.data_components.*;
import warehouse.gui_components.dialog_components.DocumentDialog;
import warehouse.gui_components.report_components.DeliveryReportTable;
import warehouse.gui_components.report_components.LogReportTable;
import warehouse.gui_components.report_components.RemaindReportTable;
import warehouse.gui_components.report_components.TurnReportTable;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static warehouse.ResourcesList.*;
import static warehouse.data_components.SortOrders.*;

public class ActionHandler {

    private static final String NO_DATASET = "";
    private static final String CATALOG_DATASET = "catalog";
    private static final String CONTRACTORS_DATASET = "contractors";
    private static final String DOCUMENTS_LIST_DATASET = "documents list";
    private static final String REMAIND_REPORT_DATASET = "remaind report";
    private static final String TURN_REPORT_DATASET = "turn report";
    private static final String DELIVERY_REPORT_DATASET = "delivery report";
    private static final String LOG_REPORT_DATASET = "log report";

    private DBHandler dbHandler;
    private String state;

    private JPanel cardPane;
    private CardLayout cardLayout;

    private JPanel emptyPane;
    private SimpleDataTable catalogTable;
    private SimpleDataTable contractorsTable;
    private DocumentsTable documentsTable;
    private RemaindReportTable remaindReportTable;
    private TurnReportTable turnReportTable;
    private DeliveryReportTable deliveryReportTable;
    private LogReportTable logReportTable;

    private DocumentDialog documentDialog;

    public ActionHandler() {
        dbHandler = MainClass.getDbHandler();
    }

    public void init() {
        //Создаем панели главного онка
        emptyPane = new JPanel();
        catalogTable = new SimpleDataTable();
        contractorsTable = new SimpleDataTable();
        documentsTable = new DocumentsTable();
        remaindReportTable = new RemaindReportTable();
        deliveryReportTable = new DeliveryReportTable();
        turnReportTable = new TurnReportTable();

        logReportTable = new LogReportTable();

        //Добавляем созданные панели в менеджер расположения
        cardPane = MainClass.getGui().getCardPane();
        cardLayout = (CardLayout) cardPane.getLayout();
        cardPane.add(emptyPane, NO_DATASET);
        cardPane.add(catalogTable.getVisualComponent(), CATALOG_DATASET);
        cardPane.add(contractorsTable.getVisualComponent(), CONTRACTORS_DATASET);
        cardPane.add(documentsTable.getVisualComponent(), DOCUMENTS_LIST_DATASET);
        cardPane.add(remaindReportTable.getVisualComponent(), REMAIND_REPORT_DATASET);
        cardPane.add(turnReportTable.getVisualComponent(), TURN_REPORT_DATASET);
        cardPane.add(deliveryReportTable.getVisualComponent(), DELIVERY_REPORT_DATASET);
        cardPane.add(logReportTable.getVisualComponent(), LOG_REPORT_DATASET);
        state = NO_DATASET;

        //Создаем объекты диалоговых окон
        documentDialog = new DocumentDialog();
    }

    public void showCatalog() {
        ArrayList<SimpleDataElement> list;
        try {
            list = dbHandler.getCatalog();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, failCatalogAccess + " " + e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        catalogTable.refresh(list, "Каталог", 1, TO_UP);
        state = CATALOG_DATASET;
        cardLayout.show(cardPane, state);
    }

    public void showContractors() {
        ArrayList<SimpleDataElement> list;
        try {
            list = dbHandler.getContractors();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, failContractorsAccess + " " + e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        contractorsTable.refresh(list, "Контрагенты", 1, TO_UP);
        state = CONTRACTORS_DATASET;
        cardLayout.show(cardPane, state);
    }

    public void showDocumentList() {
        ArrayList<Document> list;
        try {
            list = dbHandler.getDocuments();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, failDocumentsAccess + " " + e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        documentsTable.refresh(list, "Документы", 1, TO_UP);
        state = DOCUMENTS_LIST_DATASET;
        cardLayout.show(cardPane, state);
    }

    public void showDocument(Document document) {
        if (document == null) return;
        documentDialog.showViewDocumentDialog(document);
    }

    public void add() {
        if (state.equals(CATALOG_DATASET)) {
            addCatalogElement();
            return;
        }
        if (state.equals(CONTRACTORS_DATASET)) {
            addContractorElement();
            return;
        }
        if (state.equals(DOCUMENTS_LIST_DATASET)) {
            addDocument();
            return;
        }
    }

    private void addCatalogElement() {
        String name = getNewSimpleElementName("");
        if (name==null)return;

        GUI gui = MainClass.getGui();
        JFrame frm = gui.getFrm();
        try {
            dbHandler.addCatalogElement(name);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frm, failAddCatalogElement+" "+e.getMessage(), "",JOptionPane.ERROR_MESSAGE);
            return;
        }
        showCatalog();
    }

    private void addContractorElement() {
        String name = getNewSimpleElementName("");
        if (name==null)return;

        GUI gui = MainClass.getGui();
        JFrame frm = gui.getFrm();
        try {
            dbHandler.addContractorElement(name);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frm, failAddContractorElement+" "+e.getMessage(), "",JOptionPane.ERROR_MESSAGE);
            return;
        }
        showContractors();
    }

    private String getNewSimpleElementName(String startValue){
        GUI gui = MainClass.getGui();
        JFrame frm = gui.getFrm();

        String name=null;
        while (true){
            name = JOptionPane.showInputDialog(frm, "Введите наименование нового элемента", startValue);
            if (name == null)break;
            name = name.trim();
            if (name.indexOf("%")!=(-1) || name.indexOf("_")!=(-1)){
                JOptionPane.showMessageDialog(frm, "Символы % и _ недопустимы", "", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if (name.equals("")){
                JOptionPane.showMessageDialog(frm, "Наименование не может быть пустым", "", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            break;
        }
        return name;
    }

    private void addDocument() {
        //Вставить код
    }

    public void showRemaindReport() {
        ArrayList<RemaindElement> list = new ArrayList<>();
        remaindReportTable.refresh(list, "Остатки", 0, NO_ORDER);
        state = REMAIND_REPORT_DATASET;
        cardLayout.show(cardPane, state);
    }

    public void showRemaindReportWithSettings(Integer catalogId, Date endDate) {
        ArrayList<RemaindElement> list;
        try {
            list = dbHandler.getRemaindElements(catalogId, endDate);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, failRemaindReportAccess + " " + e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        state = REMAIND_REPORT_DATASET;
        cardLayout.show(cardPane, state);
        remaindReportTable.refresh(list, "Остатки", 1, TO_UP);
    }

    public void showTurnReport() {
        ArrayList<TurnElement> list = new ArrayList<>();
        turnReportTable.refresh(list, "Обороты", 0, NO_ORDER);
        state = TURN_REPORT_DATASET;
        cardLayout.show(cardPane, state);
    }

    public void showTurnReportWithSettings(Date beginDate, Date endDate, Integer catalogId) {
        ArrayList<TurnElement> list;
        try {
            list = dbHandler.getTurnElements(beginDate, endDate, catalogId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, failTurnReportAccess + " " + e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        state = TURN_REPORT_DATASET;
        cardLayout.show(cardPane, state);
        turnReportTable.refresh(list, "Обороты", 1, TO_UP);
    }

    public void showDeliveryReport() {
        ArrayList<DeliveryElement> list = new ArrayList<>();
        deliveryReportTable.refresh(list, "Обороты с контрагентом", 0, NO_ORDER);
        state = DELIVERY_REPORT_DATASET;
        cardLayout.show(cardPane, state);
    }

    public void showDeliveryReportWithSettings(Date beginDate, Date endDate, Integer contractroId) {
        ArrayList<DeliveryElement> list;
        try {
            list = dbHandler.getDeliveryElements(beginDate, endDate, contractroId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, failDeliveryReportAccess + " " + e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        state = DELIVERY_REPORT_DATASET;
        cardLayout.show(cardPane, state);
        deliveryReportTable.refresh(list, "Обороты с контрагентом", 1, TO_UP);
    }

    public void showLogReport() {
        ArrayList<LogElement> list = new ArrayList<>();
        logReportTable.refresh(list, "Журнал операций", 0, NO_ORDER);
        state = LOG_REPORT_DATASET;
        cardLayout.show(cardPane, state);
    }

    public void showLogReportWithSettings(LogRequestSettings logRequestSettings) {
        ArrayList<LogElement> list;
        try {
            list = dbHandler.getLogElements(logRequestSettings);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, failLogReportAccess + " " + e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        state = LOG_REPORT_DATASET;
        cardLayout.show(cardPane, state);
        logReportTable.refresh(list, "Журнал операций", 1, TO_UP);
    }

    public void exportToExcelFromCurrentComponent() {
        if (state.equals(NO_DATASET)) return;

        HSSFWorkbook workbook = null;
        String name = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH-mm");

        //Определяем текущую отображаемую панель
        if (state.equals(CATALOG_DATASET)) {
            workbook = catalogTable.getExcelWorkbook();
            name = "Каталог";
        }
        if (state.equals(CONTRACTORS_DATASET)) {
            workbook = contractorsTable.getExcelWorkbook();
            name = "Контрагенты";
        }
        if (state.equals(DOCUMENTS_LIST_DATASET)) {
            workbook = documentsTable.getExcelWorkbook();
            name = "Документы";
        }
        if (state.equals(REMAIND_REPORT_DATASET)) {
            workbook = remaindReportTable.getExcelWorkbook();
            name = "Остатки";
        }
        if (state.equals(TURN_REPORT_DATASET)) {
            workbook = turnReportTable.getExcelWorkbook();
            name = "Обороты";
        }
        if (state.equals(LOG_REPORT_DATASET)) {
            workbook = logReportTable.getExcelWorkbook();
            name = "Журнал операций";
        }
        if (state.equals(DELIVERY_REPORT_DATASET)) {
            workbook = deliveryReportTable.getExcelWorkbook();
            name = "Отчет по оборотам с контрагентом";
        }

        name += " " + dateFormat.format(new Date());

        saveAndOpenExcelWorkbook(workbook, name);
    }

    public void saveAndOpenExcelWorkbook(HSSFWorkbook workbook, String name) {
        File exportFile = new File(exportFolder + File.separator + name + ".xls");

        //Проверяем наличие папки, в которую будем экспортировать файл. Если ее нет - создаем
        File exportDir = new File(exportFolder);
        if (!exportDir.exists()) {
            boolean successCreate = exportDir.mkdir();
            if (!successCreate) {
                JOptionPane.showMessageDialog(null, failExportFolderCreate, "", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        //Пытаемся записать книгу на диск
        try (FileOutputStream out = new FileOutputStream(exportFile)) {
            workbook.write(out);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, failExportXLSFile + " " + e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Затем открываем её
        try {
            Desktop.getDesktop().open(exportFile);
            System.out.println();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, failOpenExportXLSFile + " " + e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    public void closeDBConnection() {
        try {
            dbHandler.disposeConnection();
        } catch (SQLException e) {
        }
    }

}
