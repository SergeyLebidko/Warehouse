package warehouse.gui_components;

import com.github.lgooddatepicker.components.DatePicker;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import warehouse.data_components.DataElement;
import warehouse.data_components.Document;
import warehouse.data_components.DocumentTypes;
import warehouse.data_components.SortOrders;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
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
            return 0;
        }

    }

    private class Model extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return 0;
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return null;
        }

    }

    private class CellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

    }

    private class HeaderRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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
        table.getColumnModel().getColumn(0).setMaxWidth(preferredWidthNumberColumn);
        table.getColumnModel().getColumn(1).setMaxWidth(preferredWidthDateColumn);
        table.getColumnModel().getColumn(2).setMaxWidth(preferredWidthTypeColumn);

        JPanel topPane = new JPanel(new BorderLayout(5, 5));

        Box nameBox = Box.createHorizontalBox();
        nameLab = new JLabel(displayName);
        nameLab.setFont(mainFont);
        nameBox.add(nameLab);
        nameBox.add(Box.createHorizontalGlue());

        idFindField = new JTextField(5);
        beginDate = new DatePicker();
        endDate = new DatePicker();
        typeBox = new JComboBox(new Object[]{"Все", COM.getName(), CONS.getName()});
        contractorsNameFindField = new JTextField();
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
        contentPane.add(new JScrollPane(table));
        contentPane.add(statusLab);

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

    }

    @Override
    public void refresh(ArrayList<? extends DataElement> list, String displayName, int sortedColumn, SortOrders sortOrder) {

    }

    @Override
    public HSSFWorkbook getExcelWorkbook() {
        return null;
    }
}
