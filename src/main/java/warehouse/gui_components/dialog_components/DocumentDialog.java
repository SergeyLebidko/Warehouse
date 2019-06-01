package warehouse.gui_components.dialog_components;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import warehouse.ActionHandler;
import warehouse.MainClass;
import warehouse.data_components.data_elements.Document;
import warehouse.data_components.data_elements.Operation;
import warehouse.gui_components.OperationsTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Date;

import static warehouse.data_components.SortOrders.*;
import static warehouse.ResourcesList.*;

public class DocumentDialog {
    //Статусы, выставляемые кнопками, логика работы которых приводит к закрытию окна
    private static final int NO_BUTTON_PRESSED = 0;
    private static final int CLOSE_BUTTON_PRESSED = 1;
    private static final int OK_BUTTON_PRESSED = 2;
    private static final int CANCEL_BUTTON_PRESSED = 3;
    private static final int EDIT_BUTTON_PRESSED = 4;

    private ActionHandler actionHandler;
    private DateFormat dateFormat;
    private int buttonPressed;
    private Document currentDocument;

    //Блок полей, необходимых для отображения диалога просмотра
    private JDialog viewDialog;

    private JPanel contentPaneVD;
    private JTextField idFieldVD;
    private JTextField dateFieldVD;
    private JTextField contractorFieldVD;
    private JTextField typeFieldVD;
    private OperationsTable operationsTableVD;

    private JButton xlsBtnVD;
    private JButton okBtnVD;

    public DocumentDialog() {
        actionHandler = MainClass.getActionHandler();
        dateFormat = DateFormat.getDateInstance();
        buttonPressed = NO_BUTTON_PRESSED;

        createViewDialog();
    }

    private void createViewDialog() {
        //Создаем диалоговое окно
        JFrame frm = MainClass.getGui().getFrm();
        viewDialog = new JDialog(frm, true);
        viewDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        viewDialog.setSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
        viewDialog.setResizable(false);
        int xPos = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - DIALOG_WIDTH / 2;
        int yPos = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - DIALOG_HEIGHT / 2;
        viewDialog.setLocation(xPos, yPos);

        //Создаем элементы диалогового окна
        contentPaneVD = new JPanel();
        contentPaneVD.setLayout(new BorderLayout());

        xlsBtnVD = new JButton(excelIconSmall);

        idFieldVD = new JTextField();
        idFieldVD.setEditable(false);
        idFieldVD.setFont(mainFont);
        idFieldVD.setHorizontalAlignment(SwingConstants.RIGHT);

        dateFieldVD = new JTextField();
        dateFieldVD.setFont(mainFont);
        dateFieldVD.setEditable(false);
        dateFieldVD.setHorizontalAlignment(SwingConstants.RIGHT);

        contractorFieldVD = new JTextField();
        contractorFieldVD.setFont(mainFont);
        contractorFieldVD.setEditable(false);
        contractorFieldVD.setHorizontalAlignment(SwingConstants.RIGHT);

        typeFieldVD = new JTextField();
        typeFieldVD.setEditable(false);
        typeFieldVD.setFont(mainFont);
        typeFieldVD.setHorizontalAlignment(SwingConstants.RIGHT);

        operationsTableVD = new OperationsTable();

        okBtnVD = new JButton("Oк");

        //Создаем вспомогательные панели
        Box topBox = Box.createVerticalBox();

        JPanel topBtnPane = new JPanel();
        topBtnPane.setLayout(new FlowLayout(FlowLayout.LEFT));

        Box idBox = Box.createHorizontalBox();
        idBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Box dateBox = Box.createHorizontalBox();
        dateBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Box contractorBox = Box.createHorizontalBox();
        contractorBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Box typeBox = Box.createHorizontalBox();
        typeBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel middlePane = new JPanel();
        middlePane.setLayout(new FlowLayout(FlowLayout.LEFT));

        JPanel bottomBtnPane = new JPanel();
        bottomBtnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

        //Добавляем созданные элементы в панели содержимого
        topBtnPane.add(xlsBtnVD);

        idBox.add(new JLabel("№ документа:"));
        idBox.add(Box.createHorizontalStrut(5));
        idBox.add(idFieldVD);
        idBox.add(Box.createHorizontalStrut((int) (DIALOG_WIDTH * 0.6)));

        dateBox.add(new JLabel("Дата:"));
        dateBox.add(Box.createHorizontalStrut(5));
        dateBox.add(dateFieldVD);
        dateBox.add(Box.createHorizontalStrut((int) (DIALOG_WIDTH * 0.6)));

        contractorBox.add(new JLabel("Контрагент:"));
        contractorBox.add(Box.createHorizontalStrut(5));
        contractorBox.add(contractorFieldVD);
        contractorBox.add(Box.createHorizontalStrut((int) (DIALOG_WIDTH * 0.6)));

        typeBox.add(new JLabel("Тип:"));
        typeBox.add(Box.createHorizontalStrut(5));
        typeBox.add(typeFieldVD);
        typeBox.add(Box.createHorizontalStrut((int) (DIALOG_WIDTH * 0.6)));

        bottomBtnPane.add(okBtnVD);

        topBox.add(topBtnPane);
        topBox.add(idBox);
        topBox.add(dateBox);
        topBox.add(contractorBox);
        topBox.add(typeBox);

        contentPaneVD.add(topBox, BorderLayout.NORTH);
        contentPaneVD.add(operationsTableVD.getVisualComponent(), BorderLayout.CENTER);
        contentPaneVD.add(bottomBtnPane, BorderLayout.SOUTH);

        //Добавляем созданные элементы в окно
        viewDialog.setContentPane(contentPaneVD);

        //Добавляем слушателей
        //Добавляем слушателя кнопке Ок
        okBtnVD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewDialog.setVisible(false);
            }
        });

        //Добавляем слушателя кнопке Экпорт
        xlsBtnVD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String docName = "Документ";
                docName += " №" + (currentDocument.getId() == null ? "-" : (currentDocument.getId() + "")) + " от " + dateFormat.format(currentDocument.getDate());
                actionHandler.saveAndOpenExcelWorkbook(createExcelWorkbook(currentDocument), docName);
            }
        });
    }

    public void showViewDocumentDialog(Document document) {
        this.currentDocument = document;

        //Заполняем поля содержимым
        idFieldVD.setText((currentDocument.getId() == null) ? "..." : (document.getId() + ""));
        dateFieldVD.setText(dateFormat.format(currentDocument.getDate()));
        contractorFieldVD.setText(document.getContractorName());
        typeFieldVD.setText(currentDocument.getType().getName());
        operationsTableVD.refresh(currentDocument.getOperationList(), 1, TO_UP);

        //Выводим окно диалога на экран
        viewDialog.setVisible(true);
    }

    public Document showCreateDocumentDialog(){
        return null;
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
        cell.setCellValue("Контрагент");
        cell.setCellStyle(headerStyle);
        cell = row.createCell(1);
        cell.setCellValue(document.getContractorName());
        cell.setCellStyle(headerStyle);

        row = sheet.createRow(3);
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

        row = sheet.createRow(5);

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
        for (Operation operation : currentDocument.getOperationList()) {
            row = sheet.createRow(number + 5);

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
