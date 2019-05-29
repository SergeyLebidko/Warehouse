package warehouse.gui_components;

import com.github.lgooddatepicker.components.DatePicker;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import warehouse.ActionHandler;
import warehouse.MainClass;
import warehouse.data_components.Document;
import warehouse.data_components.Operation;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Date;

import static warehouse.data_components.SortOrders.*;
import static warehouse.ResourcesList.*;

public class DocumentDialog {

    //Статусы, выставляемые кнопками, логика работы которых приводит к закрытию окна
    private static final int NO_BUTTON_PRESSED = 0;
    private static final int OK_BUTTON_PRESSED = 1;
    private static final int CANCEL_BUTTON_PRESSED = 2;
    private static final int EDIT_BUTTON_PRESSED = 3;

    private ActionHandler actionHandler;
    private Document currentDocument;
    private DateFormat dateFormat;
    private int buttonPressed;

    private JDialog dialog;

    private JPanel contentPane;
    private JTextField idField;
    private DatePicker datePicker;
    private JTextField dateField;
    private JTextField typeField;
    private OperationsTable operationsTable;

    private JButton editBtn;
    private JButton xlsBtn;
    private JButton addBtn;
    private JButton removeBtn;
    private JButton cancelBtn;
    private JButton okBtn;

    public DocumentDialog() {
        createFields();
        createBtnListeners();
    }

    private void createFields() {
        actionHandler = MainClass.getActionHandler();
        dateFormat = DateFormat.getDateInstance();
        buttonPressed = NO_BUTTON_PRESSED;

        //Создаем диалоговое окно
        JFrame frm = MainClass.getGui().getFrm();
        dialog = new JDialog(frm, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
        dialog.setResizable(false);
        int xPos = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - DIALOG_WIDTH / 2;
        int yPos = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - DIALOG_HEIGHT / 2;
        dialog.setLocation(xPos, yPos);

        //Создаем элементы диалогового окна
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        editBtn = new JButton(editIconSmall);
        xlsBtn = new JButton(excelIconSmall);

        idField = new JTextField();
        idField.setFont(mainFont);
        idField.setHorizontalAlignment(SwingConstants.RIGHT);

        datePicker = new DatePicker();
        datePicker.getComponentDateTextField().setEditable(false);
        datePicker.getComponentDateTextField().setHorizontalAlignment(SwingConstants.RIGHT);

        dateField = new JTextField();
        dateField.setFont(mainFont);
        dateField.setHorizontalAlignment(SwingConstants.RIGHT);

        typeField = new JTextField();
        typeField.setFont(mainFont);
        typeField.setHorizontalAlignment(SwingConstants.RIGHT);

        addBtn = new JButton(addIconSmall);
        removeBtn = new JButton(removeIconSmall);

        operationsTable = new OperationsTable();

        okBtn = new JButton("Oк");
        cancelBtn = new JButton("Отмена");

        //Создаем вспомогательные панели
        Box topBox = Box.createVerticalBox();

        JPanel topBtnPane = new JPanel();
        topBtnPane.setLayout(new FlowLayout(FlowLayout.LEFT));

        Box idBox = Box.createHorizontalBox();
        idBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Box dateBox = Box.createHorizontalBox();
        dateBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Box typeBox = Box.createHorizontalBox();
        typeBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel middlePane = new JPanel();
        middlePane.setLayout(new FlowLayout(FlowLayout.LEFT));

        JPanel bottomBtnPane = new JPanel();
        bottomBtnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

        //Добавляем созданные элементы в панели содержимого
        topBtnPane.add(editBtn);
        topBtnPane.add(xlsBtn);

        idBox.add(new JLabel("№ документа:"));
        idBox.add(Box.createHorizontalStrut(5));
        idBox.add(idField);
        idBox.add(Box.createHorizontalStrut((int) (DIALOG_WIDTH * 0.6)));

        dateBox.add(new JLabel("Дата:"));
        dateBox.add(Box.createHorizontalStrut(5));
        dateBox.add(datePicker);
        dateBox.add(dateField);
        dateBox.add(Box.createHorizontalStrut((int) (DIALOG_WIDTH * 0.6)));

        typeBox.add(new JLabel("Тип:"));
        typeBox.add(Box.createHorizontalStrut(5));
        typeBox.add(typeField);
        typeBox.add(Box.createHorizontalStrut((int) (DIALOG_WIDTH * 0.6)));

        middlePane.add(addBtn);
        middlePane.add(removeBtn);

        bottomBtnPane.add(okBtn);
        bottomBtnPane.add(cancelBtn);

        topBox.add(topBtnPane);
        topBox.add(idBox);
        topBox.add(dateBox);
        topBox.add(typeBox);
        topBox.add(middlePane);

        contentPane.add(topBox, BorderLayout.NORTH);
        contentPane.add(operationsTable.getVisualComponent(), BorderLayout.CENTER);
        contentPane.add(bottomBtnPane, BorderLayout.SOUTH);

        //Добавляем созданные элементы в окно
        dialog.setContentPane(contentPane);
    }

    private void createBtnListeners() {
        //Добавляем слушателя кнопке Ок
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonPressed = OK_BUTTON_PRESSED;
                dialog.setVisible(false);
            }
        });

        //Добавляем слушателя кнопке Экпорт
        xlsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String docName = "Документ";
                docName += " №" + (currentDocument.getId() == null ? "-" : (currentDocument.getId() + "")) + " от " + dateFormat.format(currentDocument.getDate());
                actionHandler.saveAndOpenExcelWorkbook(createExcelWorkbook(currentDocument), docName);
            }
        });
    }

    public void showDocument(Document document) {
        this.currentDocument = document;

        //Настраиваем поля для диалога вывода документа
        idField.setEditable(false);
        dateField.setEditable(false);
        typeField.setEditable(false);
        datePicker.setVisible(false);

        //Заполняем поля содержимым
        idField.setText((currentDocument.getId() == null) ? "..." : (document.getId() + ""));
        DateFormat dateFormat = DateFormat.getDateInstance();
        dateField.setText(dateFormat.format(currentDocument.getDate()));
        typeField.setText(currentDocument.getType().getName());
        operationsTable.refresh(currentDocument.getOperationList(), 1, TO_UP);

        //Настраиваем видимость кнопок
        okBtn.setVisible(true);
        cancelBtn.setVisible(false);
        addBtn.setVisible(false);
        removeBtn.setVisible(false);

        //Выводим окно диалога на экран
        dialog.setVisible(true);
    }

    private HSSFWorkbook createExcelWorkbook(Document document) {
        //Создаем файл в памяти
        HSSFWorkbook workbook = new HSSFWorkbook();

        //Создаем лист
        HSSFSheet sheet = workbook.createSheet("Лист1");

        //Создаем стиль для заголовка
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        HSSFFont headerFont = workbook.createFont();
        headerFont.setFontHeight((short) fontColumnHeaderSize);

        //Формируем заголовок документа
        Cell cell;
        Row row = sheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue("№ док.:");
        cell.setCellStyle(headerStyle);
        cell = row.createCell(1);
        cell.setCellValue(document.getId() == null ? "..." : (document.getId() + ""));
        cell.setCellStyle(headerStyle);

        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue("Дата:");
        cell.setCellStyle(headerStyle);
        cell = row.createCell(1);
        DateFormat dateFormat = DateFormat.getDateInstance();
        cell.setCellValue(dateFormat.format(document.getDate()));
        cell.setCellStyle(headerStyle);

        row = sheet.createRow(2);
        cell = row.createCell(0);
        cell.setCellValue("Тип:");
        cell.setCellStyle(headerStyle);
        cell = row.createCell(1);
        cell.setCellValue(document.getType().getName());
        cell.setCellStyle(headerStyle);

        //Формируем заголовок таблицы операций
        CellStyle operationHeaderStyle = workbook.createCellStyle();
        operationHeaderStyle.setFont(headerFont);
        operationHeaderStyle.setWrapText(true);
        operationHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
        operationHeaderStyle.setBorderBottom(BorderStyle.THIN);

        row = sheet.createRow(4);

        String[] columnNames = {"№ п/п", "Номер в каталоге", "Наименование", "Количество"};
        Cell[] headerCells = new Cell[columnNames.length];

        for (int i = 0; i < columnNames.length; i++) {
            headerCells[i] = row.createCell(i);
            headerCells[i].setCellValue(columnNames[i]);
            headerCells[i].setCellStyle(operationHeaderStyle);
        }

        //Вносим данные операций
        //Вначале создаем стили для ячеек
        HSSFCellStyle styleNumericCell = workbook.createCellStyle();
        styleNumericCell.setAlignment(HorizontalAlignment.CENTER);
        styleNumericCell.setVerticalAlignment(VerticalAlignment.CENTER);

        HSSFCellStyle styleTextCell = workbook.createCellStyle();
        styleTextCell.setWrapText(true);

        int number = 1;
        for (Operation operation: currentDocument.getOperationList()){
            row = sheet.createRow(number+4);

            cell = row.createCell(0);
            cell.setCellValue(number++);
            cell.setCellStyle(styleNumericCell);

            cell = row.createCell(1);
            cell.setCellValue(operation.getCatalogId());
            cell.setCellStyle(styleNumericCell);

            cell = row.createCell(2);
            cell.setCellValue(operation.getCatalogName());
            cell.setCellStyle(styleTextCell);

            cell = row.createCell(3);
            cell.setCellValue(operation.getCount());
            cell.setCellStyle(styleNumericCell);
        }

        //Выставляем ширину столбцов
        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 10000);
        sheet.setColumnWidth(3, 3000);

        return workbook;
    }

    private LocalDate convertDateToLocalDate(Date date) {
        int year = date.getYear() + 1900;
        int month = date.getMonth() + 1;
        int day = date.getDay();
        LocalDate localDate = LocalDate.of(year, month, day);
        return localDate;
    }

}
