package warehouse.gui_components.report_components;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
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
import warehouse.data_components.data_elements.CatalogElement;
import warehouse.data_components.data_elements.RemaindElement;
import warehouse.data_components.SortOrders;
import warehouse.gui_components.dialog_components.SEChoiсeDialog;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

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

    private DatePicker endDatePicker;
    private JTextField catalogNameField;

    private Date endDate;
    private Integer catalogId;
    private JButton clearCatalogNameBtn;

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

        DatePickerSettings datePickerSettings = new DatePickerSettings();
        datePickerSettings.setAllowEmptyDates(false);
        endDatePicker = new DatePicker(datePickerSettings);
        endDatePicker.setDateToToday();
        endDatePicker.getComponentDateTextField().setEditable(false);

        catalogNameField = new JTextField(50);
        catalogNameField.setFont(mainFont);
        catalogNameField.setEditable(false);

        clearCatalogNameBtn = new JButton(removeFilterIcon);

        startBtn = new JButton(toFormBtnText, toFormIcon);
        startBtn.setToolTipText(getToFormBtnToolTip);

        parametersBox.add(new JLabel("На дату:"));
        parametersBox.add(Box.createHorizontalStrut(5));
        parametersBox.add(endDatePicker);
        parametersBox.add(Box.createHorizontalStrut(5));
        parametersBox.add(new JLabel("Наименование:"));
        parametersBox.add(Box.createHorizontalStrut(5));
        parametersBox.add(catalogNameField);
        parametersBox.add(Box.createHorizontalStrut(5));
        parametersBox.add(clearCatalogNameBtn);
        parametersBox.add(Box.createHorizontalStrut(15));
        parametersBox.add(startBtn);

        topPane.add(nameBox, BorderLayout.NORTH);
        topPane.add(parametersBox, BorderLayout.SOUTH);

        contentPane.add(topPane, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPane.add(statusLab, BorderLayout.SOUTH);

        displayName = "";
        sortedColumn = 0;
        catalogId = null;
        endDate = new Date();
        sortOrder = NO_ORDER;
        remaindElementComparator = new RemaindElementComparator();
    }

    private void createActionListeners() {

        //Обработчик изменения даты
        endDatePicker.addDateChangeListener(new DateChangeListener() {
            @Override
            public void dateChanged(DateChangeEvent event) {
                endDate = convertLocalDateToDate(event.getNewDate());
            }
        });

        //Обработчик щелчка по полю с именем из Каталога
        catalogNameField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1 || e.getClickCount() != 1) return;
                CatalogElement catalogElement = seChoiсeDialog.showCatalogChoice();
                if (catalogElement == null) {
                    catalogNameField.setText("");
                    catalogId = null;
                    return;
                }
                catalogNameField.setText(catalogElement.getName());
                catalogId = catalogElement.getId();
            }
        });

        //Обработчик щелчка по кнопке очистки
        clearCatalogNameBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                catalogNameField.setText("");
                catalogId = null;
            }
        });

        //Обработчик щелчка по кнопке Сформировать
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionHandler.showRemaindReportWithSettings(catalogId, endDate);
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

        int headerWidth = 4;
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
        String[] columnNames = {"№ п/п", "№ в кат.", "Наименование", "Остаток"};
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

        HSSFCellStyle styleRemaindCell = workbook.createCellStyle();
        styleRemaindCell.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell cell;
        int number = 1;
        RemaindElement element;
        for (int index = 0; index < model.getRowCount(); index++) {
            row = sheet.createRow(index + 2);
            element = (RemaindElement) model.getValueAt(index, 0);

            //Столбец № п/п
            cell = row.createCell(0);
            cell.setCellValue(number);
            cell.setCellStyle(styleNumericCell);
            number++;

            //Столбец Номер в каталоге
            cell = row.createCell(1);
            cell.setCellValue(element.getCatalogId());
            cell.setCellStyle(styleNumericCell);

            //Столбец Наименование
            cell = row.createCell(2);
            cell.setCellValue(element.getCatalogName());
            cell.setCellStyle(styleTextCell);

            //Столбец Остаток
            cell = row.createCell(3);
            cell.setCellValue(element.getCount());
            cell.setCellStyle(styleRemaindCell);
        }

        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 10000);
        sheet.setColumnWidth(3, 3000);

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
        if (localDate == null) return null;
        return java.sql.Date.valueOf(localDate);
    }


}
