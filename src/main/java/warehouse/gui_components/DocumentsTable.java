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

import static warehouse.ResourcesList.*;
import static warehouse.data_components.DocumentTypes.*;
import static warehouse.data_components.SortOrders.*;

public class DocumentsTable implements DataTable {

    private JPanel contentPane;
    private Model model;
    private CellRenderer cellRenderer;
    private HeaderRenderer headerRenderer;
    private JTable table;
    private DocumentComparator documentComparator;

    private JTextField idFindField;
    private DatePicker beginDate;
    private DatePicker endDate;
    private JComboBox typeBox;
    private JTextField contractorsNameFindField;
    private JButton removeFilterBtn;
    private String idFilter;
    private Date beginDateFilter;
    private Date endDateFilter;
    private DocumentTypes typeFilter;
    private String contractorNameFilter;

    private JLabel statusLab;
    private JLabel nameLab;

    private String displayName;
    private int sortedColumn;
    private SortOrders sortOrder;

    private ArrayList<Document> content;

    private class DocumentComparator implements Comparator<Document> {

        @Override
        public int compare(Document o1, Document o2) {
            if (sortedColumn == 0) {
                Integer id1 = o1.getId();
                Integer id2 = o2.getId();
                return sortOrder.getMul() * id1.compareTo(id2);
            }
            if (sortedColumn == 1) {
                Date date1 = o1.getDate();
                Date date2 = o2.getDate();
                return sortOrder.getMul() * date1.compareTo(date2);
            }
            if (sortedColumn == 2) {
                DocumentTypes type1 = o1.getType();
                DocumentTypes type2 = o2.getType();
                if (type1 == type2) return 0;
                if (type1 == COM & type2 == CONS) return sortOrder.getMul() * 1;
                if (type1 == CONS & type2 == COM) return sortOrder.getMul() * (-1);
            }
            if (sortedColumn == 3) {
                String contractor1 = o1.getContractorName();
                String contractor2 = o2.getContractorName();
                return sortOrder.getMul() * contractor1.compareTo(contractor2);
            }
            return 0;
        }

    }

    private class Model extends AbstractTableModel {

        private int rowCount;
        private int columnCount;

        public Model() {
            rowCount = 0;
            columnCount = 4;
            statusLab.setText("Строки: " + rowCount);
        }

        public void refresh() {
            if (content == null) return;

            content.sort(documentComparator);

            if (filterEmpty()) {
                rowCount = content.size();
                statusLab.setText("Строки: " + rowCount);
                fireTableDataChanged();
                return;
            }

            rowCount = 0;
            for (Document document : content) {
                if (filterCheck(document)) rowCount++;
            }
            statusLab.setText("Строки: " + rowCount);
            fireTableDataChanged();
        }

        private boolean filterCheck(Document document) {
            //Проверяем фильтр номера документа
            boolean idFilterCheck;
            String id = document.getId() + "";
            idFilterCheck = (id.indexOf(idFilter) != (-1));

            //Проверяем фильтр даты начала периода
            boolean beginDateFilterCheck = true;
            if (beginDateFilter != null) {
                Date date = document.getDate();
                beginDateFilterCheck = (date.compareTo(beginDateFilter) >= 0);
            }

            //Проверяем фильтр даты конца периода
            boolean endDateFilterCheck = true;
            if (endDateFilter != null) {
                Date date = document.getDate();
                endDateFilterCheck = (date.compareTo(endDateFilter) <= 0);
            }

            //Проверяем фильтр типов
            boolean typeFilterCheck = true;
            if (typeFilter != null) {
                DocumentTypes type = document.getType();
                typeFilterCheck = (type == typeFilter);
            }

            //Проверяем фильтр имени контрагента
            boolean nameContractorFilterCheck;
            String contractor = document.getContractorName();
            contractor=contractor.toLowerCase();
            nameContractorFilterCheck = (contractor.indexOf(contractorNameFilter.toLowerCase())!=(-1));

            return idFilterCheck & beginDateFilterCheck & endDateFilterCheck & typeFilterCheck & nameContractorFilterCheck;
        }

        private boolean filterEmpty() {
            boolean idFilterEmpty = idFilter.equals("");
            boolean beginDateFilterEmpty = (beginDateFilter == null);
            boolean endDateFilterEmpty = (endDateFilter == null);
            boolean typeFilterEmpty = (typeFilter == null);
            boolean contractorsNameFilterEmpty = (contractorNameFilter.equals(""));
            return (idFilterEmpty & beginDateFilterEmpty & endDateFilterEmpty & typeFilterEmpty & contractorsNameFilterEmpty);
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
            for (Document document : content) {
                if (filterCheck(document)) {
                    index++;
                    if (index == rowIndex) return document;
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

            Document document = (Document) value;
            if (column == 0) {
                lab.setText(document.getId() + "");
                lab.setHorizontalAlignment(SwingConstants.CENTER);
            }
            if (column == 1) {
                DateFormat dateFormat = DateFormat.getDateInstance();
                lab.setText(dateFormat.format(document.getDate()));
                lab.setHorizontalAlignment(SwingConstants.CENTER);

            }
            if (column == 2) {
                lab.setText(document.getType().getName());
                lab.setHorizontalAlignment(SwingConstants.CENTER);
            }
            if (column == 3) {
                lab.setText(document.getContractorName());
                lab.setHorizontalAlignment(SwingConstants.LEFT);
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
                lab.setText("Дата");
            }
            if (column == 2) {
                lab.setText("Тип");
            }
            if (column == 3) {
                lab.setText("Контрагент");
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
        table.getColumnModel().getColumn(0).setMaxWidth(maxWidthNumberColumn);
        table.getColumnModel().getColumn(0).setMinWidth(minWidthNumberColumn);
        table.getColumnModel().getColumn(1).setMaxWidth(maxWidthDateColumn);
        table.getColumnModel().getColumn(1).setMinWidth(minWidthDateColumn);
        table.getColumnModel().getColumn(2).setMaxWidth(maxWidthTypeColumn);
        table.getColumnModel().getColumn(2).setMinWidth(minWidthTypeColumn);

        JPanel topPane = new JPanel(new BorderLayout(5, 5));

        Box nameBox = Box.createHorizontalBox();
        nameLab = new JLabel(displayName);
        nameLab.setFont(mainFont);
        nameBox.add(nameLab);
        nameBox.add(Box.createHorizontalGlue());

        idFindField = new JTextField(5);
        idFindField.setFont(mainFont);

        beginDate = new DatePicker();
        beginDate.getComponentDateTextField().setFont(mainFont);
        beginDate.getComponentDateTextField().setEditable(false);

        endDate = new DatePicker();
        endDate.getComponentDateTextField().setFont(mainFont);
        endDate.getComponentDateTextField().setEditable(false);

        typeBox = new JComboBox(new Object[]{"Все", COM.getName(), CONS.getName()});
        typeBox.setFont(mainFont);

        contractorsNameFindField = new JTextField(50);
        contractorsNameFindField.setFont(mainFont);

        removeFilterBtn = new JButton(removeFilterBtnText, removeFilterIcon);
        removeFilterBtn.setToolTipText(removeFilterToolTip);

        Box filterBox = Box.createHorizontalBox();
        filterBox.add(new JLabel("Номер:"));
        filterBox.add(Box.createHorizontalStrut(5));
        filterBox.add(idFindField);
        filterBox.add(Box.createHorizontalStrut(5));
        filterBox.add(new JLabel("С:"));
        filterBox.add(Box.createHorizontalStrut(5));
        filterBox.add(beginDate);
        filterBox.add(Box.createHorizontalStrut(5));
        filterBox.add(new JLabel("По:"));
        filterBox.add(Box.createHorizontalStrut(5));
        filterBox.add(endDate);
        filterBox.add(Box.createHorizontalStrut(5));
        filterBox.add(new JLabel("Тип:"));
        filterBox.add(Box.createHorizontalStrut(5));
        filterBox.add(typeBox);
        filterBox.add(Box.createHorizontalStrut(5));
        filterBox.add(new JLabel("Контрагент:"));
        filterBox.add(Box.createHorizontalStrut(5));
        filterBox.add(contractorsNameFindField);
        filterBox.add(Box.createHorizontalStrut(5));
        filterBox.add(removeFilterBtn);

        topPane.add(nameBox, BorderLayout.NORTH);
        topPane.add(filterBox, BorderLayout.SOUTH);

        contentPane.add(topPane, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPane.add(statusLab, BorderLayout.SOUTH);

        documentComparator = new DocumentComparator();
        content = null;
        displayName = "";
        sortedColumn = 1;
        sortOrder = NO_ORDER;
        idFilter = "";
        beginDateFilter = null;
        endDateFilter = null;
        typeFilter = null;
        contractorNameFilter = "";
    }

    private void createAtionListeners() {
        //Обработчик строки поиска по номеру
        idFindField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                setIdFilter(idFindField.getText());
            }
        });

        //Обработчик изменения даты начала временного интервала
        beginDate.addDateChangeListener(new DateChangeListener() {
            @Override
            public void dateChanged(DateChangeEvent event) {
                LocalDate localDate = event.getNewDate();
                setBeginDateFilter(convertLocalDateToDate(localDate));
            }
        });

        //Обработчик изменения даты окончания временного интервала
        endDate.addDateChangeListener(new DateChangeListener() {
            @Override
            public void dateChanged(DateChangeEvent event) {
                LocalDate localDate = event.getNewDate();
                setEndDateFilter(convertLocalDateToDate(localDate));
            }
        });

        //Обработчик изменения типа документа
        typeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DocumentTypes nextType = null;
                if (typeBox.getSelectedIndex() == 1) nextType = COM;
                if (typeBox.getSelectedIndex() == 2) nextType = CONS;
                setTypeFilter(nextType);
            }
        });

        //Обработчик строки поиска по наименованию контрагента
        contractorsNameFindField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                setContractorNameFilter(contractorsNameFindField.getText());
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
                    sortedColumn = columnNumber;
                    revertSortOrder();
                }
            }
        });
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
        nextFilter = nextFilter.trim();
        if (nextFilter.equals(idFilter)) return;
        if (nextFilter.equals("")) {
            idFilter = "";
            model.refresh();
            return;
        }

        //Проверяем возможность конвертирования строки с номером в число
        try {
            Integer.parseInt(nextFilter);
        } catch (Exception ex) {
            return;
        }

        idFilter = nextFilter;

        //Уведомляем модель таблицы о произошедших изменениях
        model.refresh();
    }

    private void setBeginDateFilter(Date nextDate) {
        if (beginDateFilter == null & nextDate == null) return;
        beginDateFilter = nextDate;
        model.refresh();
    }

    private void setEndDateFilter(Date nextDate) {
        if (endDateFilter == null & nextDate == null) return;
        endDateFilter = nextDate;
        model.refresh();
    }

    private void setTypeFilter(DocumentTypes nextType) {
        if (typeFilter == nextType) return;
        typeFilter = nextType;
        model.refresh();
    }

    private void setContractorNameFilter(String nextFilter) {
        nextFilter = nextFilter.trim();
        if (nextFilter.equals(contractorNameFilter)) return;
        contractorNameFilter = nextFilter;

        //Уведомляем модель таблицы о произошедших изменениях
        model.refresh();
    }

    private void removeFilter() {
        idFindField.setText("");
        beginDate.clear();
        endDate.clear();
        typeBox.setSelectedIndex(0);
        contractorsNameFindField.setText("");

        if (idFilter.equals("") &
                beginDateFilter == null &
                endDateFilter == null &
                typeFilter == null &
                contractorNameFilter.equals("")) return;

        idFilter = "";
        beginDateFilter = null;
        endDateFilter = null;
        typeFilter = null;
        contractorNameFilter = "";

        //Уведомляем модель таблицы о произошедших изменениях
        model.refresh();
    }

    @Override
    public void refresh(ArrayList<? extends DataElement> list, String displayName, int sortedColumn, SortOrders sortOrder) {
        content = new ArrayList<>();

        Document document;
        for (DataElement element : list) {
            document = (Document) element;
            content.add(document);
        }

        this.displayName = displayName;
        this.sortedColumn = sortedColumn;
        this.sortOrder = sortOrder;

        nameLab.setText(displayName);

        idFindField.setText("");
        beginDate.clear();
        endDate.clear();
        typeBox.setSelectedIndex(0);
        contractorsNameFindField.setText("");

        idFilter = "";
        beginDateFilter = null;
        endDateFilter = null;
        typeFilter = null;
        contractorNameFilter = "";

        model.refresh();
    }

    @Override
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

        Cell nameCell = row.createCell(0);
        row.createCell(1);
        row.createCell(2);
        row.createCell(3);
        row.createCell(4);

        nameCell.setCellValue(displayName);
        nameCell.setCellStyle(nameStyle);

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 4);
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
        Cell cell1 = row.createCell(0);
        Cell cell2 = row.createCell(1);
        Cell cell3 = row.createCell(2);
        Cell cell4 = row.createCell(3);
        Cell cell5 = row.createCell(4);

        cell1.setCellValue("№ п/п");
        cell2.setCellValue("Номер");
        cell3.setCellValue("Дата");
        cell4.setCellValue("Тип");
        cell5.setCellValue("Контрагент");

        cell1.setCellStyle(headerStyle);
        cell2.setCellStyle(headerStyle);
        cell3.setCellStyle(headerStyle);
        cell4.setCellStyle(headerStyle);
        cell5.setCellStyle(headerStyle);

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
        int number = 1;
        DateFormat dateFormat = DateFormat.getDateInstance();
        Date date;
        for (int index = 0; index < model.getRowCount(); index++) {
            row = sheet.createRow(index + 2);

            //Столбец № п/п
            cell = row.createCell(0);
            cell.setCellValue(number);
            cell.setCellStyle(styleNumericCell);
            number++;

            //Столбец Номер
            cell = row.createCell(1);
            cell.setCellValue(((Document) model.getValueAt(index, 0)).getId());
            cell.setCellStyle(styleNumericCell);

            //Столбец Дата
            cell = row.createCell(2);
            date = ((Document) model.getValueAt(index, 0)).getDate();
            cell.setCellValue(dateFormat.format(date));
            cell.setCellStyle(styleDateCell);

            //Столбец Тип
            cell = row.createCell(3);
            cell.setCellValue(((Document)model.getValueAt(index,0)).getType().getName());
            cell.setCellStyle(styleTypeCell);

            //Столбец Контрагент
            cell = row.createCell(4);
            cell.setCellValue(((Document)model.getValueAt(index,0)).getContractorName());
            cell.setCellStyle(styleTextCell);
        }

        //Расширяем столбцы, чтобы данные полностью в них помещались
        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 10000);

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
        int year = localDate.getYear() - 1900;
        int month = localDate.getMonthValue() - 1;
        int day = localDate.getDayOfMonth();
        return new Date(year, month, day);
    }

}
