package warehouse;

import javax.swing.*;

import warehouse.gui_components.*;
import warehouse.data_access_components.*;
import static warehouse.ResourcesList.*;
import static warehouse.data_access_components.SortOrders.*;

import java.sql.SQLException;
import java.util.ArrayList;


public class ActionHandler {

    public static final String OPEN_CATALOG_COMMAND = "open catalog";
    public static final String OPEN_CONTRACTORS_COMMAND = "open contractors";
    public static final String OPEN_DOCUMENTS_LIST_COMMAND = "open documents list";

    private static final String NO_DATASET = "";
    private static final String CATALOG_DATASET = "catalog";

    private DBHandler dbHandler;
    private String state;

    private JPanel contentPane;

    public ActionHandler() {
        dbHandler = MainClass.getDbHandler();
        contentPane = null;
        state = NO_DATASET;
    }

    public void setContentPane(JPanel contentPane) {
        this.contentPane = contentPane;
    }

    public void commandHandler(String command) {
        //Открыть каталог
        if (command.equals(OPEN_CATALOG_COMMAND)){
            showCatalog();
        }
    }

    private void showCatalog(){
        SimpleDataTable dataTable = new SimpleDataTable();
        ArrayList<SimpleDataElement> list;
        try {
            list = dbHandler.getCatalog();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, FAIL_CATALOG_ACCESS+" "+e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        dataTable.refresh(list, 1, TO_UP );
        contentPane.add(dataTable.getVisualComponent());
    }

}
