package warehouse.gui_components;

import warehouse.ActionHandler;
import warehouse.MainClass;
import warehouse.data_components.LogElement;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.ArrayList;

import static warehouse.ResourcesList.*;

public class LogReportTable {

    private static final int MAX_WIDTH_NUMBER_COLUMN = 140;
    private static final int MIN_WIDTH_NUMBER_COLUMN = 100;
    private static final int MAX_WIDTH_DATE_COLUMN = 220;
    private static final int MIN_WIDTH_DATE_COLUMN = 120;
    private static final int MAX_WIDTH_TYPE_COLUMN = 120;
    private static final int MIN_WIDTH_TYPE_COLUMN = 100;

    private ActionHandler actionHandler;

    private JPanel contentPane;
    private Model model;
    private CellRenderer cellRenderer;
    private HeaderRenderer headerRenderer;
    private JTable table;

    private JButton startBtn;

    private JLabel statusLab;
    private JLabel nameLab;

    private String displayName;

    private ArrayList<LogElement> content;

    private class Model extends AbstractTableModel {

        private int rowCount;
        private int columnCount;

        public Model() {
            rowCount = 0;
            columnCount = 6;
            statusLab.setText("Строки: " + rowCount);
        }

        public void refresh() {
            if (content == null) return;
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

            DateFormat dateFormat = DateFormat.getDateInstance();
            LogElement element = (LogElement) value;
            if (column == 0) {
                lab.setText(element.getDocumentId() + "");
                lab.setHorizontalAlignment(SwingConstants.CENTER);
            }
            if (column == 1) {
                lab.setText(dateFormat.format(element.getDate()));
                lab.setHorizontalAlignment(SwingConstants.CENTER);
            }
            if (column == 2) {
                lab.setText(element.getContractorName());
                lab.setHorizontalAlignment(SwingConstants.LEFT);
            }
            if (column == 3) {
                lab.setText(element.getDocumentType().getName());
                lab.setHorizontalAlignment(SwingConstants.CENTER);
            }
            if (column == 4) {
                lab.setText(element.getCatalogName());
                lab.setHorizontalAlignment(SwingConstants.LEFT);
            }
            if (column == 5) {
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
                lab.setText("№ док.");
            }
            if (column == 1) {
                lab.setText("Дата");
            }
            if (column == 2) {
                lab.setText("Контрагент");
            }
            if (column == 3) {
                lab.setText("Тип");
            }
            if (column == 4) {
                lab.setText("Наименование");
            }
            if (column == 5) {
                lab.setText("Количество");
            }

            lab.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            lab.setHorizontalAlignment(SwingConstants.CENTER);

            return lab;
        }

    }

    public LogReportTable() {
        createFields();
        createActionListeners();
    }

    private void createFields() {
        actionHandler = MainClass.getActionHandler();

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
        table.getColumnModel().getColumn(1).setMaxWidth(MAX_WIDTH_DATE_COLUMN);
        table.getColumnModel().getColumn(1).setMinWidth(MIN_WIDTH_DATE_COLUMN);
        table.getColumnModel().getColumn(3).setMaxWidth(MAX_WIDTH_TYPE_COLUMN);
        table.getColumnModel().getColumn(3).setMinWidth(MIN_WIDTH_TYPE_COLUMN);
        table.getColumnModel().getColumn(5).setMaxWidth(MAX_WIDTH_NUMBER_COLUMN);
        table.getColumnModel().getColumn(5).setMinWidth(MIN_WIDTH_NUMBER_COLUMN);

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
    }

    private void createActionListeners() {
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionHandler.showLogReportWithSettings(null);
            }
        });
    }

    public JPanel getVisualComponent() {
        return contentPane;
    }

    public void refresh(ArrayList<LogElement> list, String displayName) {
        content = list;
        this.displayName = displayName;
        nameLab.setText(displayName);
        model.refresh();
    }

}
