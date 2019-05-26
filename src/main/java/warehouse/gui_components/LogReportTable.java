package warehouse.gui_components;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import warehouse.ActionHandler;
import warehouse.MainClass;
import warehouse.data_components.CatalogElement;
import warehouse.data_components.ContractorsElement;
import warehouse.data_components.LogElement;
import warehouse.data_components.LogRequestSettings;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import static warehouse.data_components.DocumentTypes.*;
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
    private SEChoiсeDialog seChoiсeDialog;

    private DatePicker beginDatePicker;
    private DatePicker endDatePicker;
    private JTextField contractorField;
    private JButton clearContractorBtn;
    private JComboBox typeBox;
    private JTextField catalogField;
    private JButton clearCatalogBtn;
    private LogRequestSettings logRequestSettings;

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
        seChoiсeDialog = new SEChoiсeDialog();
        logRequestSettings = new LogRequestSettings();

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

        beginDatePicker = new DatePicker();
        beginDatePicker.getComponentDateTextField().setEditable(false);

        endDatePicker = new DatePicker();
        endDatePicker.getComponentDateTextField().setEditable(false);

        contractorField = new JTextField(20);
        contractorField.setEditable(false);
        contractorField.setFont(mainFont);

        clearContractorBtn = new JButton(removeFilterIcon);

        typeBox = new JComboBox(new Object[]{"Все", COM.getName(), CONS.getName()});

        catalogField = new JTextField(20);
        catalogField.setEditable(false);
        catalogField.setFont(mainFont);

        clearCatalogBtn = new JButton(removeFilterIcon);

        startBtn = new JButton(toFormBtnText, toFormIcon);
        startBtn.setToolTipText(getToFormBtnToolTip);

        parametersBox.add(new JLabel("С:"));
        parametersBox.add(Box.createHorizontalStrut(5));
        parametersBox.add(beginDatePicker);
        parametersBox.add(new JLabel("По:"));
        parametersBox.add(Box.createHorizontalStrut(5));
        parametersBox.add(endDatePicker);
        parametersBox.add(Box.createHorizontalStrut(5));
        parametersBox.add(new JLabel("Контрагент:"));
        parametersBox.add(Box.createHorizontalStrut(5));
        parametersBox.add(contractorField);
        parametersBox.add(Box.createHorizontalStrut(5));
        parametersBox.add(clearContractorBtn);
        parametersBox.add(Box.createHorizontalStrut(5));
        parametersBox.add(new JLabel("Тип:"));
        parametersBox.add(Box.createHorizontalStrut(5));
        parametersBox.add(typeBox);
        parametersBox.add(Box.createHorizontalStrut(5));
        parametersBox.add(new JLabel("Наименование:"));
        parametersBox.add(Box.createHorizontalStrut(5));
        parametersBox.add(catalogField);
        parametersBox.add(Box.createHorizontalStrut(5));
        parametersBox.add(clearCatalogBtn);
        parametersBox.add(Box.createHorizontalStrut(15));
        parametersBox.add(startBtn);

        topPane.add(nameBox, BorderLayout.NORTH);
        topPane.add(parametersBox, BorderLayout.SOUTH);

        contentPane.add(topPane, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPane.add(statusLab, BorderLayout.SOUTH);
    }

    private void createActionListeners() {

        beginDatePicker.addDateChangeListener(new DateChangeListener() {
            @Override
            public void dateChanged(DateChangeEvent dateChangeEvent) {
                logRequestSettings.setBeginDate(convertLocalDateToDate(dateChangeEvent.getNewDate()));
            }
        });

        endDatePicker.addDateChangeListener(new DateChangeListener() {
            @Override
            public void dateChanged(DateChangeEvent dateChangeEvent) {
                logRequestSettings.setEndDate(convertLocalDateToDate(dateChangeEvent.getNewDate()));
            }
        });

        contractorField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1 || e.getClickCount() != 1) return;
                ContractorsElement contractorElement = seChoiсeDialog.showContractorsChoice();
                if (contractorElement == null) {
                    contractorField.setText("");
                    logRequestSettings.setContractorId(null);
                    return;
                }

                contractorField.setText(contractorElement.getName());
                logRequestSettings.setContractorId(contractorElement.getId());
            }
        });

        clearContractorBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contractorField.setText("");
                logRequestSettings.setContractorId(null);
            }
        });

        typeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectVal = typeBox.getSelectedIndex();
                if (selectVal == 0) {
                    logRequestSettings.setDocumentType(null);
                    return;
                }
                if (selectVal == 1) {
                    logRequestSettings.setDocumentType(COM);
                    return;
                }
                if (selectVal == 2) {
                    logRequestSettings.setDocumentType(CONS);
                    return;
                }
            }
        });

        catalogField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1 || e.getClickCount() != 1) return;
                CatalogElement catalogElement = seChoiсeDialog.showCatalogChoice();
                if (catalogElement == null) {
                    catalogField.setText("");
                    logRequestSettings.setCatalogId(null);
                    return;
                }

                catalogField.setText(catalogElement.getName());
                logRequestSettings.setCatalogId(catalogElement.getId());
            }
        });

        clearCatalogBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                catalogField.setText("");
                logRequestSettings.setCatalogId(null);
            }
        });

        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionHandler.showLogReportWithSettings(logRequestSettings);
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

    private Date convertLocalDateToDate(LocalDate localDate) {
        Date date = null;
        if (localDate != null) {
            int year = localDate.getYear() - 1900;
            int month = localDate.getMonthValue() - 1;
            int day = localDate.getDayOfMonth();
            date = new Date(year, month, day);
        }
        return date;
    }

}
