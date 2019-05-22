package warehouse.gui_components;

import com.github.lgooddatepicker.components.DatePicker;
import warehouse.MainClass;
import warehouse.data_components.Document;

import static warehouse.data_components.SortOrders.*;
import static warehouse.ResourcesList.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Date;

public class DocumentDialog {

    private JDialog dialog;

    private JPanel contentPane;
    private JTextField idField;
    private DatePicker datePicker;
    private JTextField dateField;
    private JTextField typeField;
    private OperationsTable operationsTable;

    private JButton okBtn;

    public DocumentDialog() {
        //Создаем диалоговое окно
        JFrame frm = MainClass.getGui().getFrm();
        dialog = new JDialog(frm, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(new Dimension(DOCUMENT_DIALOG_WIDTH, DOCUMENT_DIALOG_HEIGHT));
        dialog.setMinimumSize(new Dimension(MIN_DOCUMENT_DIALOG_WIDTH, MIN_DOCUMENT_DIALOG_HEIGHT));
        int xPos = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - DOCUMENT_DIALOG_WIDTH / 2;
        int yPos = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - DOCUMENT_DIALOG_HEIGHT / 2;
        dialog.setLocation(xPos, yPos);

        //Создаем элементы диалогового окна
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
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
        operationsTable = new OperationsTable();
        okBtn = new JButton("Oк");

        //Создаем вспомогательные панели
        Box topBox = Box.createVerticalBox();
        Box idBox = Box.createHorizontalBox();
        idBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        Box dateBox = Box.createHorizontalBox();
        dateBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        Box typeBox = Box.createHorizontalBox();
        typeBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JPanel btnPane = new JPanel();
        btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

        //Добавляем созданные элементы в панели содержимого
        idBox.add(new JLabel("№ документа:"));
        idBox.add(Box.createHorizontalStrut(5));
        idBox.add(idField);
        idBox.add(Box.createHorizontalStrut((int) (DOCUMENT_DIALOG_WIDTH * 0.6)));
        dateBox.add(new JLabel("Дата:"));
        dateBox.add(Box.createHorizontalStrut(5));
        dateBox.add(datePicker);
        dateBox.add(dateField);
        dateBox.add(Box.createHorizontalStrut((int) (DOCUMENT_DIALOG_WIDTH * 0.6)));
        typeBox.add(new JLabel("Тип:"));
        typeBox.add(Box.createHorizontalStrut(5));
        typeBox.add(typeField);
        typeBox.add(Box.createHorizontalStrut((int) (DOCUMENT_DIALOG_WIDTH * 0.6)));
        btnPane.add(okBtn);

        topBox.add(idBox);
        topBox.add(dateBox);
        topBox.add(typeBox);

        contentPane.add(topBox, BorderLayout.NORTH);
        contentPane.add(operationsTable.getVisualComponent(), BorderLayout.CENTER);
        contentPane.add(btnPane, BorderLayout.SOUTH);

        //Добавляем созданные элементы в окно
        dialog.setContentPane(contentPane);
    }

    public void showDocument(Document document) {
        //Настраиваем поля для диалога вывода документа
        idField.setEditable(false);
        dateField.setEditable(false);
        typeField.setEditable(false);
        datePicker.setVisible(false);

        //Заполняем поля содержимым
        idField.setText(document.getId() + "");
        DateFormat dateFormat = DateFormat.getDateInstance();
        dateField.setText(dateFormat.format(document.getDate()));
        typeField.setText(document.getType().getName());
        operationsTable.refresh(document.getOperationList(), 1, TO_UP);

        //Настраиваем кнопки
        okBtn.setVisible(true);
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        });

        //Выводим окно диалога на экран
        dialog.setVisible(true);
    }

    private LocalDate convertDateToLocalDate(Date date) {
        int year = date.getYear() + 1900;
        int month = date.getMonth() + 1;
        int day = date.getDay();
        LocalDate localDate = LocalDate.of(year, month, day);
        return localDate;
    }

}
