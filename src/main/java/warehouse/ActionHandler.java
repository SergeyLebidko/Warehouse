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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActionHandler {

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

    public void init() {
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

    public void showDocumentWithEditOption(Document document) {
        if (document == null) return;
        documentDialog.showDocument(document, true);
    }

    public void showDocumentWithoutEditOption(Document document) {
        if (document == null) return;
        documentDialog.showDocument(document, false);
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
            System.out.println("Возникла ошибка при записи: " + e.getMessage());
        }

        //Затем открываем её
        try {
            Desktop.getDesktop().open(exportFile);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, failOpenExportXLSFile + " " + e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

}
