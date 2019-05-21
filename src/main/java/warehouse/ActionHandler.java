package warehouse;

import javax.swing.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import warehouse.gui_components.*;
import warehouse.data_components.*;
import static warehouse.ResourcesList.*;
import static warehouse.data_components.SortOrders.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class ActionHandler {

    public static final String OPEN_CATALOG_COMMAND = "open catalog";
    public static final String OPEN_CONTRACTORS_COMMAND = "open contractors";
    public static final String OPEN_DOCUMENTS_LIST_COMMAND = "open documents list";
    public static final String EXPORT_TO_XLS_COMMAND = "export to excel";
    public static final String OPEN_DOCUMENT_COMMAND = "open document";

    private static final String NO_DATASET = "";
    private static final String CATALOG_DATASET = "catalog";
    private static final String CONTRACTORS_DATASET = "contractors";
    private static final String DOCUMENTS_LIST_DATASET = "documents list";

    private DBHandler dbHandler;
    private String state;

    private JPanel cardPane;
    private CardLayout cardLayout;

    private JPanel emptyPane;
    private SimpleDataTable catalogTable;
    private SimpleDataTable contractorsTable;
    private DocumentsTable documentsTable;

    private DocumentDialog documentDialog;

    public ActionHandler() {
        dbHandler = MainClass.getDbHandler();
    }

    public void init(){
        //Создаем панели главного онка
        emptyPane = new JPanel();
        catalogTable = new SimpleDataTable();
        contractorsTable = new SimpleDataTable();
        documentsTable = new DocumentsTable();

        //Добавляем созданные панели в менеджер расположения
        cardPane = MainClass.getGui().getCardPane();
        cardLayout = (CardLayout) cardPane.getLayout();
        cardPane.add(emptyPane, NO_DATASET);
        cardPane.add(catalogTable.getVisualComponent(), CATALOG_DATASET);
        cardPane.add(contractorsTable.getVisualComponent(), CONTRACTORS_DATASET);
        cardPane.add(documentsTable.getVisualComponent(), DOCUMENTS_LIST_DATASET);
        state = NO_DATASET;

        //Создаем объекты диалоговых окон
        documentDialog = new DocumentDialog();
    }

    public void commandHandler(String command) {
        //Открыть каталог
        if (command.equals(OPEN_CATALOG_COMMAND)) {
            showCatalog();
            return;
        }

        //Открыть список контрагентов
        if (command.equals(OPEN_CONTRACTORS_COMMAND)) {
            showContractors();
            return;
        }

        //Открыть список документов
        if (command.equals(OPEN_DOCUMENTS_LIST_COMMAND)) {
            showDocumentList();
        }

        //Экспорт данных в книгу Excel
        if (command.equals(EXPORT_TO_XLS_COMMAND)) {
            saveAndOpenExcelWorkbook();
        }

        //Открытие документа
        if (command.equals(OPEN_DOCUMENT_COMMAND)){
            openDocument();
        }

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

    public void openDocument(){
        if (!state.equals(DOCUMENTS_LIST_DATASET))return;
        Document document = documentsTable.getSelectedRow();
        if (document==null)return;

        documentDialog.showDocument(document);
    }

    public void saveAndOpenExcelWorkbook() {
        HSSFWorkbook workbook = null;

        //Получаем рабочую книгу из текущего отображаемого компонента
        if (state.equals(CATALOG_DATASET)) {
            workbook = catalogTable.getExcelWorkbook();
        }
        if (state.equals(CONTRACTORS_DATASET)){
            workbook = contractorsTable.getExcelWorkbook();
        }
        if (state.equals(DOCUMENTS_LIST_DATASET)){
            workbook = documentsTable.getExcelWorkbook();
        }

        if (workbook == null) return;

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
        try (FileOutputStream out = new FileOutputStream(exportFolder + File.separator + exportFileName)) {
            workbook.write(out);

        } catch (IOException e) {
            System.out.println("Возникла ошибка при записи: " + e.getMessage());
        }

        //Затем открываем её
        try {
            Desktop.getDesktop().open(new File(exportFolder + File.separator + exportFileName));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, failOpenExportXLSFile + " " + e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

}
