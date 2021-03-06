package warehouse.gui_components.dialog_components;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import warehouse.ActionHandler;
import warehouse.MainClass;
import warehouse.data_components.data_elements.ContractorsElement;
import warehouse.data_components.data_elements.Document;
import warehouse.data_components.data_elements.Operation;
import warehouse.gui_components.OperationsTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Date;

import static warehouse.data_components.SortOrders.*;
import static warehouse.ResourcesList.*;
import static warehouse.data_components.DocumentTypes.*;

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
    private SEChoiсeDialog seChoiсeDialog;
    private OperationDialog operationDialog;

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

    //Блок полей, необходимых для диалога создания документа
    private JDialog changeDialog;

    private JPanel contentPaneCD;
    private JTextField idFieldCD;
    private DatePicker datePickerCD;
    private JTextField contractorFieldCD;
    private JComboBox<String> typeBoxCD;
    private OperationsTable operationsTableCD;

    private JButton addOperationBtnCD;
    private JButton removeOperationBtnCD;
    private JButton okBtnCD;
    private JButton cancelBtnCD;

    public DocumentDialog() {
        actionHandler = MainClass.getActionHandler();
        dateFormat = DateFormat.getDateInstance();
        operationDialog = new OperationDialog();
        seChoiсeDialog = new SEChoiсeDialog();
        buttonPressed = NO_BUTTON_PRESSED;

        makeViewDialog();
        makeChangeDialog();
    }

    private void makeViewDialog() {
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

    private void makeChangeDialog() {
        //Создаем диалоговое окно
        JFrame frm = MainClass.getGui().getFrm();
        changeDialog = new JDialog(frm, true);
        changeDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        changeDialog.setSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
        changeDialog.setResizable(false);
        int xPos = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - DIALOG_WIDTH / 2;
        int yPos = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - DIALOG_HEIGHT / 2;
        changeDialog.setLocation(xPos, yPos);

        //Создаем элементы диалогового окна
        contentPaneCD = new JPanel();
        contentPaneCD.setLayout(new BorderLayout());

        idFieldCD = new JTextField();
        idFieldCD.setFont(mainFont);
        idFieldCD.setEditable(false);
        idFieldCD.setHorizontalAlignment(SwingConstants.RIGHT);

        DatePickerSettings datePickerSettings = new DatePickerSettings();
        datePickerSettings.setAllowEmptyDates(false);
        datePickerCD = new DatePicker(datePickerSettings);
        datePickerCD.setFont(mainFont);
        datePickerCD.getComponentDateTextField().setEditable(false);
        datePickerCD.getComponentDateTextField().setHorizontalAlignment(SwingConstants.RIGHT);

        contractorFieldCD = new JTextField();
        contractorFieldCD.setFont(mainFont);
        contractorFieldCD.setEditable(false);
        contractorFieldCD.setHorizontalAlignment(SwingConstants.RIGHT);

        typeBoxCD = new JComboBox<>(new String[]{COM.getName(), CONS.getName()});
        typeBoxCD.setFont(mainFont);

        addOperationBtnCD = new JButton(addIconSmall);
        addOperationBtnCD.setToolTipText(addBtnToolTip);

        removeOperationBtnCD = new JButton(removeIconSmall);
        removeOperationBtnCD.setToolTipText(removeBtnToolTip);

        operationsTableCD = new OperationsTable();

        okBtnCD = new JButton("Ок");
        cancelBtnCD = new JButton("Отмена");

        //Создаем вспомогательные панели
        Box topBox = Box.createVerticalBox();

        Box idBox = Box.createHorizontalBox();
        idBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Box dateBox = Box.createHorizontalBox();
        dateBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Box contractorBox = Box.createHorizontalBox();
        contractorBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Box typeBox = Box.createHorizontalBox();
        typeBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel topBtnPane = new JPanel();
        topBtnPane.setLayout(new FlowLayout(FlowLayout.LEFT));

        JPanel bottomBtnPane = new JPanel();
        bottomBtnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

        //Добавляем компонеты во вспомогательные панели
        idBox.add(new JLabel("№ документа:"));
        idBox.add(Box.createHorizontalStrut(5));
        idBox.add(idFieldCD);
        idBox.add(Box.createHorizontalStrut((int) (DIALOG_WIDTH * 0.6)));

        dateBox.add(new JLabel("Дата:"));
        dateBox.add(Box.createHorizontalStrut(5));
        dateBox.add(datePickerCD);
        dateBox.add(Box.createHorizontalStrut((int) (DIALOG_WIDTH * 0.6)));

        contractorBox.add(new JLabel("Контрагент:"));
        contractorBox.add(Box.createHorizontalStrut(5));
        contractorBox.add(contractorFieldCD);
        contractorBox.add(Box.createHorizontalStrut((int) (DIALOG_WIDTH * 0.6)));

        typeBox.add(new JLabel("Тип:"));
        typeBox.add(Box.createHorizontalStrut(5));
        typeBox.add(typeBoxCD);
        typeBox.add(Box.createHorizontalStrut((int) (DIALOG_WIDTH * 0.6)));

        topBox.add(idBox);
        topBox.add(dateBox);
        topBox.add(contractorBox);
        topBox.add(typeBox);

        topBtnPane.add(addOperationBtnCD);
        topBtnPane.add(removeOperationBtnCD);

        topBox.add(topBtnPane);

        bottomBtnPane.add(okBtnCD);
        bottomBtnPane.add(cancelBtnCD);

        //Добавляем созданные панели в панель содержимого
        contentPaneCD.add(topBox, BorderLayout.NORTH);
        contentPaneCD.add(operationsTableCD.getVisualComponent(), BorderLayout.CENTER);
        contentPaneCD.add(bottomBtnPane, BorderLayout.SOUTH);

        //Добавляем все созданные компоненты в окно
        changeDialog.setContentPane(contentPaneCD);

        //Добавляем слушателей кнопкам
        changeDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                buttonPressed = CLOSE_BUTTON_PRESSED;
            }
        });

        datePickerCD.addDateChangeListener(new DateChangeListener() {
            @Override
            public void dateChanged(DateChangeEvent event) {
                currentDocument.setDate(convertLocalDateToDate(event.getNewDate()));
            }
        });

        contractorFieldCD.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 & e.getButton() == MouseEvent.BUTTON1) {
                    ContractorsElement contractorsElement = seChoiсeDialog.showContractorsChoice();
                    if (contractorsElement == null) {
                        return;
                    }

                    contractorFieldCD.setText(contractorsElement.getName());
                    currentDocument.setContractorId(contractorsElement.getId());
                    currentDocument.setContractorName(contractorsElement.getName());
                }
            }
        });

        typeBoxCD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedItem = typeBoxCD.getSelectedIndex();
                if (selectedItem == 0) {
                    currentDocument.setType(COM);
                    return;
                }
                if (selectedItem == 1) {
                    currentDocument.setType(CONS);
                    return;
                }
            }
        });

        addOperationBtnCD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Operation operation = operationDialog.showInputOperationDialog();
                if (operation == null) return;

                //Проверяем, чтобы одно и то же наименование не было внесено в список операций дважды
                for (Operation opElement: currentDocument.getOperationList()){
                    if (opElement.getCatalogId()==operation.getCatalogId()){
                        JOptionPane.showMessageDialog(changeDialog, opElement.getCatalogName() + " уже есть в списке", "", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                //Добавляем введенную операцию в список операций текущего документа
                currentDocument.getOperationList().add(operation);
                operationsTableCD.refresh(currentDocument.getOperationList(), 1, TO_UP);
            }
        });

        removeOperationBtnCD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Operation operation = operationsTableCD.getSelectedRow();
                if (operation == null) return;

                currentDocument.getOperationList().remove(operation);
                operationsTableCD.refresh(currentDocument.getOperationList(), 1, TO_UP);
            }
        });

        okBtnCD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonPressed = OK_BUTTON_PRESSED;
                changeDialog.setVisible(false);
            }
        });

        cancelBtnCD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonPressed = CANCEL_BUTTON_PRESSED;
                changeDialog.setVisible(false);
            }
        });
    }

    public void showViewDocumentDialog(Document document) {
        currentDocument = document;

        //Заполняем поля содержимым
        idFieldVD.setText((currentDocument.getId() == null) ? "..." : (document.getId() + ""));
        dateFieldVD.setText(dateFormat.format(currentDocument.getDate()));
        contractorFieldVD.setText(document.getContractorName());
        typeFieldVD.setText(currentDocument.getType().getName());
        operationsTableVD.refresh(currentDocument.getOperationList(), 1, TO_UP);

        //Выводим окно диалога на экран
        viewDialog.setVisible(true);
    }

    public Document showEditDocumentDialog(Document document){
        currentDocument = document;

        //Заполняем поля содержимым
        idFieldCD.setText(document.getId()+"");
        datePickerCD.setDate(convertDateToLocalDate(document.getDate()));
        contractorFieldCD.setText(document.getContractorName());
        if (document.getType()==COM){
            typeBoxCD.setSelectedIndex(0);
        }
        if (document.getType()==CONS){
            typeBoxCD.setSelectedIndex(1);
        }
        operationsTableCD.refresh(document.getOperationList(), 1, TO_UP);

        Document editedDocument = showChangeDocumentDialog();
        return editedDocument;
    }

    public Document showCreateDocumentDialog(){
        currentDocument = new Document();
        currentDocument.setType(COM);
        currentDocument.setDate(new Date());

        //Заполняем поля содержимым
        idFieldCD.setText("...");
        datePickerCD.setDateToToday();
        contractorFieldCD.setText("");
        typeBoxCD.setSelectedIndex(0);
        operationsTableCD.refresh(currentDocument.getOperationList(), 0, NO_ORDER);

        Document document = showChangeDocumentDialog();
        return document;
    }

    private Document showChangeDocumentDialog() {
        //Подготавливаем вспомогательные переменные и выводим окно диалога на экран
        Date currentDate = new Date();
        Date docDate;
        while (true) {
            changeDialog.setVisible(true);

            //Если пользователь отказался от ввода - возвращаем null
            if (buttonPressed == CLOSE_BUTTON_PRESSED || buttonPressed == CANCEL_BUTTON_PRESSED) return null;

            //Если пользователь подтверждает ввод - проверяем правильность заполнения полей
            if (buttonPressed == OK_BUTTON_PRESSED) {
                //Проверка выбора контрагента
                if (currentDocument.getContractorId() == null) {
                    JOptionPane.showMessageDialog(changeDialog, "Выберите контрагента", "", JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                //Проверка даты. Документы не могут иметь дату больше текущей
                docDate = currentDocument.getDate();
                if (docDate.compareTo(currentDate) == 1) {
                    JOptionPane.showMessageDialog(changeDialog, "Дата документа не может быть больше текущей", "", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
            }

            break;
        }

        return currentDocument;
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

    private Date convertLocalDateToDate(LocalDate localDate) {
        return java.sql.Date.valueOf(localDate);
    }

    private LocalDate convertDateToLocalDate(Date date) {
        return new java.sql.Date(date.getTime()).toLocalDate();
    }

}
