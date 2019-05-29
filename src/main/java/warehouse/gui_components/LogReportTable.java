package warehouse.gui_components;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import warehouse.ActionHandler;
import warehouse.MainClass;
import warehouse.data_components.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import static warehouse.data_components.DocumentTypes.*;
import static warehouse.ResourcesList.*;
import static warehouse.data_components.SortOrders.*;

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
    private int sortedColumn;
    private SortOrders sortOrder;
    private LogElementComparator logElementComparator;

    private ArrayList<LogElement> content;

    private class LogElementComparator implements Comparator<LogElement> {

        @Override
        public int compare(LogElement o1, LogElement o2) {
            if (sortedColumn == 0) {
                Integer documentId1 = o1.getDocumentId();
                Integer documentId2 = o2.getDocumentId();
                return sortOrder.getMul() * documentId1.compareTo(documentId2);
            }
            if (sortedColumn == 1) {
                Date date1 = o1.getDate();
                Date date2 = o2.getDate();
                return sortOrder.getMul() * date1.compareTo(date2);
            }
            if (sortedColumn == 2) {
                String contractorName1 = o1.getContractorName();
                String contractorName2 = o2.getContractorName();
                return sortOrder.getMul() * contractorName1.compareTo(contractorName2);
            }
            if (sortedColumn == 3) {
                DocumentTypes type1 = o1.getDocumentType();
                DocumentTypes type2 = o2.getDocumentType();
                if (type1 == type2) return 0;
                if (type1 == COM & type2 == CONS) return sortOrder.getMul() * 1;
                if (type1 == CONS & type2 == COM) return sortOrder.getMul() * (-1);
            }
            if (sortedColumn == 4) {
                String catalogName1 = o1.getCatalogName();
                String catalogName2 = o2.getCatalogName();
                return sortOrder.getMul() * catalogName1.compareTo(catalogName2);
            }
            if (sortedColumn == 5) {
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
            columnCount = 6;
            statusLab.setText("Строки: " + rowCount);
        }

        public void refresh() {
            if (content == null) return;

            content.sort(logElementComparator);

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

        displayName = "";
        sortedColumn = 0;
        sortOrder = NO_ORDER;
        logElementComparator = new LogElementComparator();
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

    public void refresh(ArrayList<LogElement> list, String displayName, int sortedColumn, SortOrders sortOrder) {
        content = list;
        this.displayName = displayName;
        this.sortedColumn = sortedColumn;
        this.sortOrder = sortOrder;
        nameLab.setText(displayName);
        model.refresh();
        table.getTableHeader().repaint();
    }

    public HSSFWorkbook getExcelWorkbook() {
        //Создаем файл в памяти
        HSSFWorkbook workbook = new HSSFWorkbook();

        //Создаем лист
        HSSFSheet sheet = workbook.createSheet("Лист1");

        //Заполняем лист данными
        //Формируем ячейку с наименованием набора данных
        HSSFCellStyle nameStyle = workbook.createCellStyle();
        HSSFFont nameFont = workbook.createFont();
        nameFont.setFontHeight((short) fontFileHeaderSize);
        nameFont.setBold(true);
        nameStyle.setFont(nameFont);
        nameStyle.setAlignment(HorizontalAlignment.CENTER);

        Row row = sheet.createRow(0);

        int headerWidth = 7;
        Cell nameCell = null;
        for (int i = 0; i < headerWidth; i++) {
            if (i == 0) {
                nameCell = row.createCell(0);
                continue;
            }
            row.createCell(i);
        }

        nameCell.setCellValue(displayName);
        nameCell.setCellStyle(nameStyle);

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, headerWidth - 1);
        sheet.addMergedRegion(region);

        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);

        //Формируем заголовки столбцов
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        HSSFFont headerFont = workbook.createFont();
        headerFont.setFontHeight((short) fontColumnHeaderSize);
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        row = sheet.createRow(1);
        String[] columnNames = {"№ п/п", "№ док.", "Дата", "Контрагент", "Тип", "Наименование", "Количество"};
        Cell[] headerCells = new Cell[columnNames.length];

        for (int i = 0; i < columnNames.length; i++) {
            headerCells[i] = row.createCell(i);
            headerCells[i].setCellValue(columnNames[i]);
            headerCells[i].setCellStyle(headerStyle);
        }

        //Вносим данные
        HSSFCellStyle styleTextCell = workbook.createCellStyle();
        styleTextCell.setWrapText(true);

        HSSFCellStyle styleNumericCell = workbook.createCellStyle();
        styleNumericCell.setAlignment(HorizontalAlignment.CENTER);
        styleNumericCell.setVerticalAlignment(VerticalAlignment.CENTER);

        HSSFCellStyle styleTypeCell = workbook.createCellStyle();
        styleTypeCell.setAlignment(HorizontalAlignment.CENTER);
        styleTypeCell.setVerticalAlignment(VerticalAlignment.CENTER);

        HSSFCellStyle styleDateCell = workbook.createCellStyle();
        styleDateCell.setAlignment(HorizontalAlignment.CENTER);
        styleDateCell.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell cell;
        LogElement logElement;
        int number = 1;
        DateFormat dateFormat = DateFormat.getDateInstance();
        Date date;
        for (int index = 0; index < model.getRowCount(); index++) {
            row = sheet.createRow(index + 2);
            logElement = (LogElement)model.getValueAt(index,0);

            //Столбец № п/п
            cell = row.createCell(0);
            cell.setCellValue(number);
            cell.setCellStyle(styleNumericCell);
            number++;

            //Столбец № док.
            cell = row.createCell(1);
            cell.setCellValue(logElement.getDocumentId());
            cell.setCellStyle(styleNumericCell);

            //Столбец Дата документа
            cell = row.createCell(2);
            cell.setCellValue(dateFormat.format(logElement.getDate()));
            cell.setCellStyle(styleDateCell);

            //Столбец Контрагент
            cell = row.createCell(3);
            cell.setCellValue(logElement.getContractorName());
            cell.setCellStyle(styleTextCell);

            //Столбец Тип
            cell = row.createCell(4);
            cell.setCellValue(logElement.getDocumentType().getName());
            cell.setCellStyle(styleTypeCell);

            //Столбец Наименование
            cell = row.createCell(5);
            cell.setCellValue(logElement.getCatalogName());
            cell.setCellStyle(styleTextCell);

            //Столбец Количество
            cell = row.createCell(6);
            cell.setCellValue(logElement.getCount());
            cell.setCellStyle(styleNumericCell);
        }

        //Расширяем столбцы, чтобы данные полностью в них помещались
        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 10000);
        sheet.setColumnWidth(4, 3000);
        sheet.setColumnWidth(5, 10000);
        sheet.setColumnWidth(6, 4000);

        return workbook;
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
