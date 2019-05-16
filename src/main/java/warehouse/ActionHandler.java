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

    private SimpleDataTable simpleDataTable;    //Компонент для отображения в главном окне простых таблиц

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
        if (command.equals(OPEN_CATALOG_COMMAND)){
            showCatalog();
            return;
        }

        //Открыть список контрагентов
        if (command.equals(OPEN_CONTRACTORS_COMMAND)){
            showContractors();
            return;
        }
    }

    private void showCatalog(){
        ArrayList<SimpleDataElement> list;
        try {
            list = dbHandler.getCatalog();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, failCatalogAccess +" "+e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        simpleDataTable.refresh(list, 1, TO_UP );
        contentPane.add(simpleDataTable.getVisualComponent());
        contentPane.revalidate();
        contentPane.repaint();
    }

    private void showContractors(){
        ArrayList<SimpleDataElement> list;
        try {
            list = dbHandler.getContractors();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, failContractorsAccess +" "+e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        simpleDataTable.refresh(list, 1, TO_UP );
        contentPane.add(simpleDataTable.getVisualComponent());
        contentPane.revalidate();
        contentPane.repaint();
    }

}
