package warehouse.gui_components.dialog_components;

import warehouse.MainClass;
import warehouse.data_components.DBHandler;
import warehouse.data_components.data_elements.CatalogElement;
import warehouse.data_components.data_elements.Operation;
import warehouse.data_components.data_elements.SimpleDataElement;
import warehouse.gui_components.SimpleDataTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import static warehouse.ResourcesList.*;
import static warehouse.data_components.SortOrders.*;

public class OperationDialog {

    //Статусы, выставляемые кнопками, логика работы которых приводит к закрытию окна
    private static final int NO_BUTTON_PRESSED = 0;
    private static final int OK_BUTTON_PRESSED = 1;
    private static final int CANCEL_BUTTON_PRESSED = 2;

    private DBHandler dbHandler;

    private JDialog dialog;
    private JTextField countField;
    private SimpleDataTable dataTable;
    private int buttonPressed;

    private JButton okBtn;
    private JButton cancelBtn;

    public OperationDialog() {
        createFields();
        createBtnListeners();
    }

    private void createFields() {
        dbHandler = MainClass.getDbHandler();

        JFrame frm = MainClass.getGui().getFrm();
        dialog = new JDialog(frm, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
        dialog.setResizable(false);
        int xPos = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - DIALOG_WIDTH / 2;
        int yPos = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - DIALOG_HEIGHT / 2;
        dialog.setLocation(xPos, yPos);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        JPanel topPane = new JPanel();
        topPane.setLayout(new FlowLayout(FlowLayout.LEFT));

        countField = new JTextField(5);
        countField.setFont(mainFont);
        countField.setAlignmentX(SwingConstants.RIGHT);

        topPane.add(new JLabel("Количество:"));
        topPane.add(Box.createHorizontalStrut(5));
        topPane.add(countField);

        okBtn = new JButton("Ок");
        cancelBtn = new JButton("Отмена");

        JPanel btnPane = new JPanel();
        btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

        btnPane.add(okBtn);
        btnPane.add(cancelBtn);

        dataTable = new SimpleDataTable();

        contentPane.add(topPane, BorderLayout.NORTH);
        contentPane.add(dataTable.getVisualComponent(), BorderLayout.CENTER);
        contentPane.add(btnPane, BorderLayout.SOUTH);

        dialog.setContentPane(contentPane);
    }

    private void createBtnListeners() {
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonPressed = OK_BUTTON_PRESSED;
                dialog.setVisible(false);
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonPressed = CANCEL_BUTTON_PRESSED;
                dialog.setVisible(false);
            }
        });
    }

    public Operation showInputOperationDialog() {
        ArrayList<SimpleDataElement> list;
        try {
            list = dbHandler.getCatalog();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, failCatalogAccess + " " + e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        dataTable.refresh(list,"Выберите элемент каталога", 1, TO_UP);

        Operation operation;
        int count;
        while (true){
            buttonPressed = NO_BUTTON_PRESSED;
            operation = null;
            countField.setText("");

            dialog.setVisible(true);
            if (buttonPressed == NO_BUTTON_PRESSED || buttonPressed == CANCEL_BUTTON_PRESSED) break;

            CatalogElement catalogElement = (CatalogElement) dataTable.getSelectedElement();
            if (catalogElement==null){
                JOptionPane.showMessageDialog(dialog, "Выберите элемент каталога", "", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            try{
                count = Integer.parseInt(countField.getText());
                if (count<=0)throw new NumberFormatException();
            }catch (NumberFormatException e){
                JOptionPane.showMessageDialog(dialog, "Введите корректное значение количества", "", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            operation = new Operation(null, catalogElement.getId(), catalogElement.getName(), count);
            break;
        }
        return operation;
    }

}
