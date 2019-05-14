package warehouse;

import javax.swing.*;
import java.awt.*;

import static warehouse.ResourcesList.*;

public class GUI {

    private JFrame frm;

    public GUI() {
        localizationStandartDialog();
        createFrm();
        showFrm();
    }

    private void localizationStandartDialog(){
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
        JPanel contentPane = new JPanel(new BorderLayout(5, 5));
        frm.setContentPane(contentPane);
    }

    private void showFrm() {
        frm.setVisible(true);
    }

}
