package warehouse.gui_components;

import warehouse.data_components.Document;
import static warehouse.data_components.SortOrders.*;
import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;

public class DocumentDialog {

    private JDialog dialog;

    private JPanel contentPane;
    private JTextField idField;
    private JTextField dateField;
    private JTextField typeField;
    private OperationsTable operationsTable;

    public DocumentDialog() {
        dialog = new JDialog();
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(5,5));
        idField = new JTextField(5);
        dateField = new JTextField(15);
        typeField = new JTextField(10);
        operationsTable = new OperationsTable();

        Box topBox = Box.createVerticalBox();
        Box idBox = Box.createHorizontalBox();
        Box dateBox = Box.createHorizontalBox();
        Box typeBox = Box.createHorizontalBox();

        idBox.add(new JLabel("№ документа:"));
        idBox.add(Box.createHorizontalStrut(5));
        idBox.add(idField);

        dateBox.add(new JLabel("Дата:"));
        dateBox.add(Box.createHorizontalStrut(5));
        dateBox.add(dateField);

        typeBox.add(new JLabel("Тип:"));
        typeBox.add(Box.createHorizontalStrut(5));
        typeBox.add(typeField);

        topBox.add(idBox);
        topBox.add(dateBox);
        topBox.add(typeBox);

        contentPane.add(topBox, BorderLayout.NORTH);
        contentPane.add(operationsTable.getVisualComponent(), BorderLayout.CENTER);

        dialog.setContentPane(contentPane);
    }

    public void showDocument(Document document){
        idField.setText(document.getId()+"");
        DateFormat dateFormat = DateFormat.getDateInstance();
        dateField.setText(dateFormat.format(document.getDate()));
        typeField.setText(document.getType().getName());
        operationsTable.refresh(document.getOperationList(), 1, TO_UP);


    }

}
