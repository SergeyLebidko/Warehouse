package warehouse.gui_components;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import warehouse.data_components.DataElement;
import warehouse.data_components.SimpleDataElement;
import warehouse.data_components.SortOrders;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Comparator;

import static warehouse.ResourcesList.*;
import static warehouse.data_components.SortOrders.*;

public class SimpleDataTable implements DataTable {

    private JPanel contentPane;
    private Model model;
    private CellRenderer cellRenderer;
    private HeaderRenderer headerRenderer;
    private JTable table;
    private SimpleElementComparator simpleElementComparator;

    private JTextField idFindField;
    private JTextField nameFindField;
    private JButton removeFilterBtn;
    private String nameFilter;
    private String idFilter;

    private JLabel statusLab;
    private JLabel nameLab;

    private String displayName;
    private int sortedColumn;
    private SortOrders sortOrder;

    private ArrayList<SimpleDataElement> content;

    private class SimpleElementComparator implements Comparator<SimpleDataElement> {

        @Override
        public int compare(SimpleDataElement o1, SimpleDataElement o2) {
            if (sortedColumn == 0) {
                Integer id1 = o1.getId();
                Integer id2 = o2.getId();
                return sortOrder.getMul() * id1.compareTo(id2);
            }
            if (sortedColumn == 1) {
                String name1 = o1.getName();
                String name2 = o2.getName();
                return sortOrder.getMul() * name1.compareTo(name2);
            }
            return 0;
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

            content.sort(simpleElementComparator);

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
            String id = dataElement.getId() + "";
            String name = dataElement.getName();

            name = name.toLowerCase();

            return (id.indexOf(idFilter) != (-1)) & (name.indexOf(nameFilter.toLowerCase()) != (-1));
        }

        private boolean filterEmpty() {
            return idFilter.equals("") & nameFilter.equals("");
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

        JPanel topPane = new JPanel(new BorderLayout(5,5));

        Box nameBox = Box.createHorizontalBox();
        nameLab = new JLabel(displayName);
        nameLab.setFont(mainFont);
        nameBox.add(nameLab);
        nameBox.add(Box.createHorizontalGlue());

        idFindField = new JTextField(5);
        idFindField.setMaximumSize(new Dimension(preferredWidthNumberColumn, 100));
        idFindField.setFont(mainFont);

        nameFindField = new JTextField();
        nameFindField.setFont(mainFont);

        removeFilterBtn = new JButton(removeFilterBtnText, removeFilterIcon);
        removeFilterBtn.setToolTipText(removeFilterToolTip);

        Box filterBox = Box.createHorizontalBox();
        filterBox.add(new JLabel("Номер:"));
        filterBox.add(Box.createHorizontalStrut(5));
        filterBox.add(idFindField);
        filterBox.add(Box.createHorizontalStrut(5));
        filterBox.add(new JLabel("Наименование:"));
        filterBox.add(Box.createHorizontalStrut(5));
        filterBox.add(nameFindField);
        filterBox.add(Box.createHorizontalStrut(5));
        filterBox.add(removeFilterBtn);

        topPane.add(nameBox, BorderLayout.NORTH);
        topPane.add(filterBox, BorderLayout.SOUTH);

        contentPane.add(topPane, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPane.add(statusLab, BorderLayout.SOUTH);

        simpleElementComparator = new SimpleElementComparator();
        content = null;
        displayName = "";
        sortedColumn = 1;
        sortOrder = NO_ORDER;
        idFilter = "";
        nameFilter = "";
    }

    private void createActionListeners() {
        //Обработчик событий строки поиска по номеру
        idFindField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                setIdFilter(idFindField.getText());
            }
        });

        //Обработчик событий строки поиска по наименованию
        nameFindField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                setNameFilter(nameFindField.getText());
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
    public SimpleDataElement getSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == (-1)) return null;
        SimpleDataElement selectedElement = (SimpleDataElement) model.getValueAt(selectedRow, 1);
        return selectedElement;
    }

    @Override
    public void setIdFilter(String nextFilter) {
        nextFilter = nextFilter.trim();
        if (nextFilter.equals("")) {
            if (idFilter.equals("")) return;
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

    public void setNameFilter(String nextFilter) {
        nextFilter = nextFilter.trim();
        if (nextFilter.equals(nameFilter)) return;
        nameFilter = nextFilter;

        //Уведомляем модель таблицы о произошедших изменениях
        model.refresh();
    }

    public void removeFilter() {
        idFindField.setText("");
        nameFindField.setText("");

        if (idFilter.equals("") & nameFilter.equals("")) return;

        nameFilter = "";
        idFilter = "";

        //Уведомляем модель таблицы о произошедших изменениях
        model.refresh();
    }

    @Override
    public void refresh(ArrayList<? extends DataElement> list, String displayName, int sortedColumn, SortOrders sortOrder) {
        content = new ArrayList<>();

        SimpleDataElement simpleDataElement;
        for (DataElement element: list){
            simpleDataElement = (SimpleDataElement)element;
            content.add(simpleDataElement);
        }

        this.sortOrder = sortOrder;
        this.sortedColumn = sortedColumn;
        this.displayName = displayName;
        nameLab.setText(displayName);
        idFindField.setText("");
        nameFindField.setText("");
        nameFilter = "";
        idFilter = "";
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
        nameFont.setFontHeight((short) 256);
        nameFont.setBold(true);
        nameStyle.setFont(nameFont);
        nameStyle.setAlignment(HorizontalAlignment.CENTER);

        Row row = sheet.createRow(0);

        Cell nameCell = row.createCell(0);
        row.createCell(1);
        row.createCell(2);

        nameCell.setCellValue(displayName);
        nameCell.setCellStyle(nameStyle);

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 2);
        sheet.addMergedRegion(region);

        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);

        //Формируем заголовки столбцов
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        HSSFFont headerFont = workbook.createFont();
        headerFont.setFontHeight((short) 200);
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        row = sheet.createRow(1);
        Cell cell1 = row.createCell(0);
        Cell cell2 = row.createCell(1);
        Cell cell3 = row.createCell(2);

        cell1.setCellValue("№ п/п");
        cell2.setCellValue("Номер");
        cell3.setCellValue("Наименование");

        cell1.setCellStyle(headerStyle);
        cell2.setCellStyle(headerStyle);
        cell3.setCellStyle(headerStyle);

        //Вносим данные
        HSSFCellStyle styleTextCell = workbook.createCellStyle();
        styleTextCell.setWrapText(true);

        HSSFCellStyle styleNumericCell = workbook.createCellStyle();
        styleNumericCell.setAlignment(HorizontalAlignment.CENTER);
        styleNumericCell.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell cell;
        int number = 1;
        for (int index = 0; index < model.getRowCount(); index++) {
            row = sheet.createRow(index + 2);

            //Столбец № п/п
            cell = row.createCell(0);
            cell.setCellValue(number);
            cell.setCellStyle(styleNumericCell);
            number++;

            //Столбец Номер
            cell = row.createCell(1);
            cell.setCellValue(((SimpleDataElement) model.getValueAt(index, 0)).getId());
            cell.setCellStyle(styleNumericCell);

            //Столбец Наименование
            cell = row.createCell(2);
            cell.setCellValue(((SimpleDataElement) model.getValueAt(index, 0)).getName());
            cell.setCellStyle(styleTextCell);
        }

        //Расширяем столбцы, чтобы данные полностью в них помещались
        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 10000);

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

}
