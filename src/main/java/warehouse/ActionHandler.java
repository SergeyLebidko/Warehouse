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

    private static final String NO_DATASET = "";
    private static final String CATALOG_DATASET = "catalog";
    private static final String CONTRACTORS_DATASET = "contractors";

    private DBHandler dbHandler;
    private String state;

    private JPanel contentPane;

    private SimpleDataTable simpleDataTable;    //Компонент для отображения в главном окне простых таблиц
    private DocumentsTable documentsTable;      //Компонент для отображения списка документов

    public ActionHandler() {
        dbHandler = MainClass.getDbHandler();
        simpleDataTable = new SimpleDataTable();
        contentPane = null;
        state = NO_DATASET;
    }

    public void setContentPane(JPanel contentPane) {
        this.contentPane = contentPane;
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
        if (command.equals(OPEN_DOCUMENTS_LIST_COMMAND)){
            //Вставить код
        }

        //Экспорт данных в книгу Excel
        if (command.equals(EXPORT_TO_XLS_COMMAND)) {
            saveExcelWorkbook();
        }

    }

    private void showCatalog() {
        ArrayList<SimpleDataElement> list;
        try {
            list = dbHandler.getCatalog();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, failCatalogAccess + " " + e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        simpleDataTable.refresh(list, "Каталог", 1, TO_UP);
        contentPane.add(simpleDataTable.getVisualComponent());
        contentPane.revalidate();
        contentPane.repaint();
        state = CATALOG_DATASET;
    }

    private void showContractors() {
        ArrayList<SimpleDataElement> list;
        try {
            list = dbHandler.getContractors();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, failContractorsAccess + " " + e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        simpleDataTable.refresh(list, "Контрагенты", 1, TO_UP);
        contentPane.add(simpleDataTable.getVisualComponent());
        contentPane.revalidate();
        contentPane.repaint();
        state = CONTRACTORS_DATASET;
    }

    private void showDocumentList(){
        ArrayList<Document> list;

    }

    private void saveExcelWorkbook() {
        HSSFWorkbook workbook = null;

        //Получаем рабочую книгу из текущего отображаемого компонента
        if (state.equals(CATALOG_DATASET) | state.equals(CONTRACTORS_DATASET)) {
            workbook = simpleDataTable.getExcelWorkbook();
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
