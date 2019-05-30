package warehouse.gui_components;

import warehouse.data_components.data_elements.Operation;
import warehouse.data_components.SortOrders;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;

import static warehouse.ResourcesList.*;
import static warehouse.data_components.SortOrders.*;

public class OperationsTable {

    public static final int MAX_WIDTH_NUMBER_COLUMN = 150;
    public static final int MIN_WIDTH_NUMBER_COLUMN = 100;

    private JPanel contentPane;
    private Model model;
    private CellRenderer cellRenderer;
    private HeaderRenderer headerRenderer;
    private JTable table;

    private OperationComparator operationComparator;

    private int sortedColumn;
    private SortOrders sortOrder;

    private ArrayList<Operation> content;

    private class OperationComparator implements Comparator<Operation> {

        @Override
        public int compare(Operation o1, Operation o2) {
            if (sortedColumn==0){
                Integer id1 = o1.getCatalogId();
                Integer id2 = o2.getCatalogId();
                return sortOrder.getMul()*id1.compareTo(id2);
            }
            if (sortedColumn==1){
                String name1 = o1.getCatalogName();
                String name2 = o2.getCatalogName();
                return sortOrder.getMul()*name1.compareTo(name2);
            }
            if (sortedColumn==2){
                Integer count1 = o1.getCount();
                Integer count2 = o2.getCount();
                return sortOrder.getMul()*count1.compareTo(count2);
            }
            return 0;
        }

    }

    private class Model extends AbstractTableModel {

        private int rowCount;
        private int columnCount;

        public Model() {
            rowCount = 0;
            columnCount = 3;
        }

        public void refresh() {
            if (content == null) return;
            content.sort(operationComparator);
            rowCount = content.size();
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return rowCount;
        }

        @Override
        public int getColumnCount() {
            return columnCount;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (content == null) return "";
            return content.get(rowIndex);
        }

    }

    private class CellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lab = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lab.setFont(mainFont);

            Operation operation = (Operation) value;
            if (column == 0) {
                lab.setText(operation.getCatalogId() + "");
                lab.setHorizontalAlignment(SwingConstants.CENTER);
            }
            if (column == 1) {
                lab.setText(operation.getCatalogName());
                lab.setHorizontalAlignment(SwingConstants.LEFT);
            }
            if (column == 2) {
                lab.setText(operation.getCount() + "");
                lab.setHorizontalAlignment(SwingConstants.CENTER);
            }

            return lab;
        }

    }

    private class HeaderRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lab = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lab.setBackground(headerColor);

            if (column == 0) {
                lab.setText("№ в каталоге");
            }
            if (column == 1) {
                lab.setText("Наименование");
            }
            if (column == 2) {
                lab.setText("Количество");
            }

            lab.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            lab.setHorizontalAlignment(SwingConstants.CENTER);

            if (column != sortedColumn) {
                lab.setIcon(noOrderIcon);
            }
            if (column == sortedColumn) {
                switch (sortOrder) {
                    case TO_UP: {
                        lab.setIcon(toUpIcon);
                        break;
                    }
                    case TO_DOWN: {
                        lab.setIcon(toDownIcon);
                        break;
                    }
                    case NO_ORDER: {
                        lab.setIcon(noOrderIcon);
                    }
                }
            }

            return lab;
        }

    }

    public OperationsTable() {
        createFields();
        createActionListeners();
    }

    private void createFields() {
        contentPane = new JPanel(new BorderLayout(5, 5));
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
        table.getColumnModel().getColumn(0).setMaxWidth(MAX_WIDTH_NUMBER_COLUMN);
        table.getColumnModel().getColumn(0).setMinWidth(MIN_WIDTH_NUMBER_COLUMN);
        table.getColumnModel().getColumn(2).setMaxWidth(MAX_WIDTH_NUMBER_COLUMN);
        table.getColumnModel().getColumn(2).setMinWidth(MIN_WIDTH_NUMBER_COLUMN);

        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);

        content = null;
        sortedColumn = 1;
        sortOrder = NO_ORDER;
        operationComparator = new OperationComparator();
    }

    private void createActionListeners() {
        //Обработчик щелчка по заголовку столбца
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 & e.getButton() == MouseEvent.BUTTON1) {
                    int columnNumber = table.getTableHeader().columnAtPoint(e.getPoint());
                    sortedColumn = columnNumber;
                    revertSortOrder();
                }
            }
        });
    }

    public JPanel getVisualComponent() {
        return contentPane;
    }

    public Operation getSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == (-1)) return null;
        Operation selectedElement = (Operation) model.getValueAt(selectedRow, 0);
        return selectedElement;
    }

    public void refresh(ArrayList<Operation> list, int sortedColumn, SortOrders sortOrder) {
        content = list;
        this.sortedColumn = sortedColumn;
        this.sortOrder = sortOrder;
        model.refresh();
    }

    private void revertSortOrder() {
        if (content == null) return;
        SortOrders nextOrder = null;
        switch (sortOrder) {
            case NO_ORDER: {
                nextOrder = TO_UP;
                break;
            }
            case TO_UP: {
                nextOrder = TO_DOWN;
                break;
            }
            case TO_DOWN: {
                nextOrder = TO_UP;
                break;
            }
        }
        sortOrder = nextOrder;

        //Уведомляем таблицу и ее модель о произошедших изменениях
        model.refresh();
        table.getTableHeader().repaint();
    }

}
