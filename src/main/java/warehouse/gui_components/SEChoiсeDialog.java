package warehouse.gui_components;

import warehouse.MainClass;
import warehouse.data_components.*;

import static warehouse.ResourcesList.*;
import static warehouse.data_components.SortOrders.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class SEChoiсeDialog {

    //Статусы, выставляемые кнопками, логика работы которых приводит к закрытию окна
    private static final int NO_BUTTON_PRESSED = 0;
    private static final int OK_BUTTON_PRESSED = 1;
    private static final int CANCEL_BUTTON_PRESSED = 2;

    private DBHandler dbHandler;

    private JDialog dialog;
    private SimpleDataTable dataTable;
    private int buttonPressed;

    private JButton okBtn;
    private JButton cancelBtn;

    public SEChoiсeDialog() {
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

        JPanel bottomPane = new JPanel();
        bottomPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

        dataTable = new SimpleDataTable();
        okBtn = new JButton("Ок");
        cancelBtn = new JButton("Отмена");

        bottomPane.add(okBtn);
        bottomPane.add(cancelBtn);

        contentPane.add(dataTable.getVisualComponent(), BorderLayout.CENTER);
        contentPane.add(bottomPane, BorderLayout.SOUTH);
        dialog.setContentPane(contentPane);

        buttonPressed = NO_BUTTON_PRESSED;
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

    public CatalogElement showCatalogChoice() {
        ArrayList<SimpleDataElement> list;
        try {
            list = dbHandler.getCatalog();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, failCatalogAccess + " " + e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        dataTable.refresh(list, "Выберите элемент каталога", 1, TO_UP);
        buttonPressed = NO_BUTTON_PRESSED;
        SimpleDataElement element = showDialog();
        if (element!=null)return (CatalogElement)element;
        return null;
    }

    public ContractorsElement showContractorsChoice() {
        ArrayList<SimpleDataElement> list;
        try {
            list = dbHandler.getContractors();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, failContractorsAccess + " " + e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        dataTable.refresh(list, "Выберите контрагента", 1, TO_UP);
        buttonPressed = NO_BUTTON_PRESSED;
        SimpleDataElement element = showDialog();
        if (element!=null)return (ContractorsElement)element;
        return null;
    }

    private SimpleDataElement showDialog() {
        dialog.setVisible(true);
        SimpleDataElement element = dataTable.getSelectedElement();
        if (buttonPressed == NO_BUTTON_PRESSED || buttonPressed == CANCEL_BUTTON_PRESSED) element = null;
        return element;
    }

}
