package warehouse.gui_components;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import warehouse.data_components.DataElement;
import warehouse.data_components.SimpleDataElement;
import warehouse.data_components.SortOrders;

import javax.swing.*;
import java.util.ArrayList;

public interface DataTable {

    JPanel getVisualComponent();
    SimpleDataElement getSelectedRow();
    void setIdFilter(String nextFilter);
    void refresh(ArrayList<? extends DataElement> list, String displayName, int sortedColumn, SortOrders sortOrder);
    HSSFWorkbook getExcelWorkbook();

}
