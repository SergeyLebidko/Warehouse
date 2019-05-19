package warehouse.gui_components;

import com.github.lgooddatepicker.components.DatePicker;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import warehouse.data_components.DataElement;
import warehouse.data_components.Document;
import warehouse.data_components.SimpleDataElement;
import warehouse.data_components.SortOrders;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

import static warehouse.ResourcesList.*;

public class DocumentsTable implements DataTable {

    private JPanel contentPane;
    private Model model;
    private CellRenderer cellRenderer;
    private HeaderRenderer headerRenderer;
    private JTable table;

    private JTextField idFindField;
    private DatePicker beginDate;
    private DatePicker endDate;
    private JComboBox typeBox;
    private JTextField contractorsNameFindField;

    private JLabel statusLab;
    private JLabel nameLab;

    private String displayName;
    private int sortedColumn;
    private SortOrders sortOrder;

    private class DocumentComparator implements Comparator<Document> {

        @Override
        public int compare(Document o1, Document o2) {
            return 0;
        }

    }

    private class Model extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return 0;
        }

        @Override
        public int getColumnCount() {
            return 0;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return null;
        }

    }

    private class CellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

    }

    private class HeaderRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

    }

    public DocumentsTable() {
        createFields();
        createAtionListeners();
    }

    private void createFields() {
        contentPane = new JPanel(new BorderLayout(5, 5));
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusLab = new JLabel("");
        model = new Model();
        table = new JTable(model);
        cellRenderer = new CellRenderer();
        headerRenderer = new HeaderRenderer();
        table.setDefaultRenderer(Object.class, cellRenderer);
        table.getTableHeader().setDefaultRenderer(headerRenderer);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(rowHeight);
        table.setShowVerticalLines(false);
        table.setGridColor(gridColor);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(preferredWidthNumberColumn);
        table.getColumnModel().getColumn(1).setMaxWidth(preferredWidthDateColumn);
        table.getColumnModel().getColumn(2).setMaxWidth(preferredWidthTypeColumn);

    }

    private void createAtionListeners() {

    }

    @Override
    public JPanel getVisualComponent() {
        return contentPane;
    }

    @Override
    public Document getSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == (-1)) return null;
        Document selectedElement = (Document) model.getValueAt(selectedRow, 1);
        return selectedElement;
    }

    @Override
    public void setIdFilter(String nextFilter) {

    }

    @Override
    public void refresh(ArrayList<? extends DataElement> list, String displayName, int sortedColumn, SortOrders sortOrder) {

    }

    @Override
    public HSSFWorkbook getExcelWorkbook() {
        return null;
    }
}
