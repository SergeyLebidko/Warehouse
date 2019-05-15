package warehouse;

import javax.swing.*;
import java.awt.*;

import static warehouse.ResourcesList.*;


public class GUI {

    private JFrame frm;
    private JPanel contentPane;

    private ActionHandler actionHandler;

    private JButton openBtn;
    private JButton addBtn;
    private JButton editBtn;
    private JButton removeBtn;
    private JButton xlsBtn;
    private JButton reportBtn;

    public GUI() {
        localizationStandartDialog();
        createFrm();
        createActionHandlerState();
        createToolbar();

        test();

        showFrm();
    }

    private void localizationStandartDialog() {
        UIManager.put("OptionPane.yesButtonText", yesButtonText);
        UIManager.put("OptionPane.noButtonText", noButtonText);
        UIManager.put("OptionPane.cancelButtonText", cancelButtonText);
        UIManager.put("OptionPane.inputDialogTitle", inputDialogTitle);
    }

    private void createFrm() {
        frm = new JFrame(frmTitle);
        frm.setIconImage(logoImage);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setSize(FRM_WIDTH, FRM_HEIGHT);
        frm.setMinimumSize(new Dimension(MIN_FRM_WIDTH, MIN_FRM_HEIGHT));
        int xPos = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - FRM_WIDTH / 2;
        int yPos = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - FRM_HEIGHT / 2;
        frm.setLocation(xPos, yPos);
        contentPane = new JPanel(new BorderLayout(5, 5));
        frm.setContentPane(contentPane);
    }

    private void createActionHandlerState() {
        actionHandler = MainClass.getActionHandler();
        actionHandler.setContentPane(contentPane);
    }

    private void createToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        openBtn = new JButton(openBtnText, openIcon);
        openBtn.setToolTipText(openBtnToolTip);

        addBtn = new JButton(addBtnText, addIcon);
        addBtn.setToolTipText(addBtnToolTip);

        editBtn = new JButton(editBtnText, editIcon);
        editBtn.setToolTipText(editBtnToolTip);

        removeBtn = new JButton(removeBtnText, removeIcon);
        removeBtn.setToolTipText(removeBtnToolTip);

        xlsBtn = new JButton(xlsBtnText, excelIcon);
        xlsBtn.setToolTipText(xlsBtnToolTip);

        reportBtn = new JButton(reportBtnText, reportIcon);
        reportBtn.setToolTipText(reportBtnToolTip);

        toolBar.add(openBtn);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(addBtn);
        toolBar.add(Box.createHorizontalStrut(3));
        toolBar.add(editBtn);
        toolBar.add(Box.createHorizontalStrut(3));
        toolBar.add(removeBtn);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(xlsBtn);
        toolBar.add(Box.createHorizontalStrut(3));
        toolBar.add(reportBtn);

        frm.add(toolBar, BorderLayout.NORTH);
    }

    private void showFrm() {
        frm.setVisible(true);
    }

    private void test(){
        actionHandler.commandHandler(ActionHandler.OPEN_CATALOG_COMMAND);
    }

}
