package warehouse.gui_components;

import warehouse.ActionHandler;
import warehouse.MainClass;
import warehouse.data_components.RemaindElement;
import warehouse.data_components.SortOrders;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;

import static warehouse.data_components.SortOrders.*;
import static warehouse.ResourcesList.*;

public class RemaindReportTable {

    private static final int MAX_WIDTH_NUMBER_COLUMN = 170;
    private static final int MIN_WIDTH_NUMBER_COLUMN = 130;

    private ActionHandler actionHandler;

    private JPanel contentPane;
    private Model model;
    private CellRenderer cellRenderer;
    private HeaderRenderer headerRenderer;
    private JTable table;
    private SEChoiсeDialog seChoiсeDialog;

    private JButton startBtn;

    private JLabel statusLab;
    private JLabel nameLab;

    private String displayName;
    private int sortedColumn;
    private SortOrders sortOrder;
    private RemaindElementComparator remaindElementComparator;

    private ArrayList<RemaindElement> content;

    private class RemaindElementComparator implements Comparator<RemaindElement> {

        @Override
        public int compare(RemaindElement o1, RemaindElement o2) {
            if (sortedColumn == 0) {
                Integer id1 = o1.getCatalogId();
                Integer id2 = o2.getCatalogId();
                return sortOrder.getMul() * id1.compareTo(id2);
            }
            if (sortedColumn == 1) {
                String name1 = o1.getCatalogName();
                String name2 = o2.getCatalogName();
                return sortOrder.getMul() * name1.compareTo(name2);
            }
            if (sortedColumn == 2) {
                Integer count1 = o1.getCount();
                Integer count2 = o2.getCount();
                return sortOrder.getMul() * count1.compareTo(count2);
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
            statusLab.setText("Строки: " + rowCount);
        }

        public void refresh() {
            if (content == null) return;

            content.sort(remaindElementComparator);

            rowCount = content.size();
            statusLab.setText("Строки: " + rowCount);
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

            RemaindElement element = (RemaindElement) value;
            if (column == 0) {
                lab.setText(element.getCatalogId() + "");
                lab.setHorizontalAlignment(SwingConstants.CENTER);
            }
            if (column == 1) {
                lab.setText(element.getCatalogName());
                lab.setHorizontalAlignment(SwingConstants.LEFT);
            }
            if (column == 2) {
                lab.setText(element.getCount() + "");
                lab.setHorizontalAlignment(SwingConstants.CENTER);
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
                lab.setText("№ в кат.");
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

    public RemaindReportTable() {
        createFields();
        createActionListeners();
    }

    private void createFields() {
        actionHandler = MainClass.getActionHandler();
        seChoiсeDialog = new SEChoiсeDialog();

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
        table.setFont(mainFont);
        table.setRowHeight(rowHeight);
        table.setShowVerticalLines(false);
        table.setGridColor(gridColor);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(MAX_WIDTH_NUMBER_COLUMN);
        table.getColumnModel().getColumn(0).setMinWidth(MIN_WIDTH_NUMBER_COLUMN);
        table.getColumnModel().getColumn(2).setMaxWidth(MAX_WIDTH_NUMBER_COLUMN);
        table.getColumnModel().getColumn(2).setMinWidth(MIN_WIDTH_NUMBER_COLUMN);

        JPanel topPane = new JPanel();
        topPane.setLayout(new BorderLayout(5, 5));

        Box nameBox = Box.createHorizontalBox();
        nameLab = new JLabel("");
        nameLab.setFont(mainFont);
        nameBox.add(nameLab);

        Box parametersBox = Box.createHorizontalBox();

        startBtn = new JButton(toFormBtnText, toFormIcon);
        startBtn.setToolTipText(getToFormBtnToolTip);

        parametersBox.add(startBtn);

        topPane.add(nameBox, BorderLayout.NORTH);
        topPane.add(parametersBox, BorderLayout.SOUTH);

        contentPane.add(topPane, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPane.add(statusLab, BorderLayout.SOUTH);

        displayName = "";
        sortedColumn = 0;
        sortOrder = NO_ORDER;
        remaindElementComparator = new RemaindElementComparator();
    }

    private void createActionListeners() {

        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionHandler.showRemaindReportWithSettings(null, null);
            }
        });

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

    public void refresh(ArrayList<RemaindElement> list, String displayName, int sortedColumn, SortOrders sortOrder) {
        content = list;
        this.displayName = displayName;
        this.sortedColumn = sortedColumn;
        this.sortOrder = sortOrder;
        nameLab.setText(displayName);
        model.refresh();
        table.getTableHeader().repaint();
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
