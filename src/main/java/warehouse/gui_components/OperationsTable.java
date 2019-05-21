package warehouse.gui_components;

import warehouse.data_components.Operation;
import warehouse.data_components.SortOrders;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

import static warehouse.ResourcesList.*;

public class OperationsTable {

    private JPanel contentPane;
    private Model model;
    private CellRenderer cellRenderer;
    private HeaderRenderer headerRenderer;
    private JTable table;

    private OperationComparator operationComparator;

    private int sortedColumn;
    private SortOrders sortOrder;

    private ArrayList<Operation> content;

    private class OperationComparator implements Comparator<Operation>{

        @Override
        public int compare(Operation o1, Operation o2) {
            return 0;
        }

    }

    private class Model extends AbstractTableModel{

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

    private class CellRenderer extends DefaultTableCellRenderer{

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

    }

    private class HeaderRenderer extends DefaultTableCellRenderer{

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

    }

    public OperationsTable() {
        createFields();
    }

    private void createFields(){
        contentPane = new JPanel(new BorderLayout(5,5));
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
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
    }

    public JPanel getVisualComponent(){
        return contentPane;
    }

}
