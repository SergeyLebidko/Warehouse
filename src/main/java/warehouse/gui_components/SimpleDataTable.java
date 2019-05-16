package warehouse.gui_components;

import warehouse.data_access_components.SimpleDataElement;
import warehouse.data_access_components.SortOrders;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Comparator;

import static warehouse.ResourcesList.*;
import static warehouse.data_access_components.SortOrders.*;

public class SimpleDataTable {

    private JPanel contentPane;
    private Model model;
    private CellRenderer cellRenderer;
    private HeaderRenderer headerRenderer;
    private JTable table;
    private SimpleElementComparator simpleElementComparator;

    private JTextField findField;
    private JButton removeFilterBtn;
    private JLabel statusLab;

    private SortOrders sortOrder;
    private String filter;

    private ArrayList<SimpleDataElement> content;

    private class SimpleElementComparator implements Comparator<SimpleDataElement> {

        @Override
        public int compare(SimpleDataElement o1, SimpleDataElement o2) {
            String name1 = o1.getName();
            String name2 = o2.getName();
            return sortOrder.getMul() * name1.compareTo(name2);
        }

    }

    private class Model extends AbstractTableModel {

        private int rowCount;
        private int columnCount;

        public Model() {
            rowCount = 0;
            columnCount = 2;
            statusLab.setText("Строки: " + rowCount);
        }

        public void refresh() {
            if (content == null) return;

            if (filterEmpty()) {
                rowCount = content.size();
                statusLab.setText("Строки: " + rowCount);
                fireTableDataChanged();
                return;
            }

            rowCount = 0;
            for (SimpleDataElement element : content) {
                if (filterCheck(element)) rowCount++;
            }
            statusLab.setText("Строки: " + rowCount);
            fireTableDataChanged();
        }

        private boolean filterCheck(SimpleDataElement dataElement) {
            String name = dataElement.getName();
            name = name.toLowerCase();
            return name.indexOf(filter.toLowerCase()) != (-1);
        }

        private boolean filterEmpty() {
            return filter.equals("");
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

            //Если фильтра нет
            if (filterEmpty()) {
                return content.get(rowIndex);
            }

            //Если фильтр есть
            int index = -1;
            for (SimpleDataElement dataElement : content) {
                if (filterCheck(dataElement)) {
                    index++;
                    if (index == rowIndex) return dataElement;
                }
            }
            return "";
        }

    }

    private class CellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lab = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lab.setFont(mainFont);

            SimpleDataElement element = (SimpleDataElement) value;
            if (column == 0) {
                lab.setText(element.getId() + "");
                lab.setHorizontalAlignment(SwingConstants.CENTER);
            }
            if (column == 1) {
                lab.setText(element.toString());
                lab.setHorizontalAlignment(SwingConstants.LEFT);
            }

            if (!isSelected) {
                if ((row % 2) == 0) {
                    lab.setBackground(evenCellsColor);
                }
                if ((row % 2) != 0) {
                    lab.setBackground(notEvenCellsColor);
                }
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
                lab.setText("Номер");
            }
            if (column == 1) {
                lab.setText("Наименование");
            }

            lab.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            lab.setHorizontalAlignment(SwingConstants.CENTER);

            if (column==0){
                lab.setIcon(null);
            }
            if (column == 1) {
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

    public SimpleDataTable() {
        createFields();
        createActionListeners();
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

        findField = new JTextField();
        findField.setFont(mainFont);
        removeFilterBtn = new JButton(removeFilterBtnText, removeFilterIcon);

        Box filterBox = Box.createHorizontalBox();
        filterBox.add(findField);
        filterBox.add(Box.createHorizontalStrut(5));
        filterBox.add(removeFilterBtn);


        contentPane.add(filterBox, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPane.add(statusLab, BorderLayout.SOUTH);

        simpleElementComparator = new SimpleElementComparator();
        content = null;
        sortOrder = NO_ORDER;
        filter = "";
    }

    private void createActionListeners() {
        //Обработчик событий строки поиска
        findField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                setFilter(findField.getText());
            }
        });

        //Обработчик кнопки очистки фильтра поиска
        removeFilterBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeFilter();
            }
        });

        //Обработчик щелчка по заголовку столбца
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 & e.getButton() == MouseEvent.BUTTON1) {
                    int columnNumber = table.getTableHeader().columnAtPoint(e.getPoint());
                    if (columnNumber!=1)return;
                    revertSortOrder();
                }
            }
        });
    }

    public JPanel getVisualComponent() {
        return contentPane;
    }

    public SimpleDataElement getSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == (-1)) return null;
        SimpleDataElement selectedElement = (SimpleDataElement) model.getValueAt(selectedRow, 1);
        return selectedElement;
    }

    public void setFilter(String nextFilter) {
        nextFilter = nextFilter.trim();
        if (nextFilter.equals(filter)) return;
        filter = nextFilter;

        //Уведомляем модель таблицы о произошедших изменениях
        model.refresh();
    }

    public void removeFilter() {
        findField.setText("");
        if (filter.equals("")) return;
        filter = "";

        //Уведомляем модель таблицы о произошедших изменениях
        model.refresh();
    }

    public void refresh(ArrayList<SimpleDataElement> list) {
        refresh(list, NO_ORDER);
    }

    public void refresh(ArrayList<SimpleDataElement> list, SortOrders sortOrder) {
        content = list;
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
        content.sort(simpleElementComparator);

        //Уведомляем таблицу и ее модель о произошедших изменениях
        model.refresh();
        table.getTableHeader().repaint();
    }

}
