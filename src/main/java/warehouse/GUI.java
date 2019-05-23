package warehouse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static warehouse.ResourcesList.*;

public class GUI {

    private JFrame frm;
    private JPanel cardPane;

    private ActionHandler actionHandler;

    private JButton openBtn;
    private JButton addBtn;
    private JButton editBtn;
    private JButton removeBtn;
    private JButton xlsBtn;
    private JButton reportBtn;

    JPopupMenu openMenu;
    JPopupMenu reportMenu;

    public GUI() {
        localizationStandartDialog();
        createFrm();
        createCardPane();
        createActionHandler();
        createToolbar();
        createOpenMenu();
        createReportMenu();
        createBtnListeners();
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
        JPanel contentPane = new JPanel(new BorderLayout(5, 5));
        frm.setContentPane(contentPane);
    }

    private void createCardPane() {
        cardPane = new JPanel();
        cardPane.setLayout(new CardLayout(5, 5));
        frm.getContentPane().add(cardPane, BorderLayout.CENTER);
    }

    private void createActionHandler() {
        actionHandler = MainClass.getActionHandler();
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

    private void createOpenMenu() {
        openMenu = new JPopupMenu();
        JMenuItem openCatalogItem = new JMenuItem(openCatalogItemText);
        openCatalogItem.setFont(mainFont);
        JMenuItem openContractorsItem = new JMenuItem(openContractorsItemText);
        openContractorsItem.setFont(mainFont);
        JMenuItem openDocumentsItem = new JMenuItem(openDocumentsItemText);
        openDocumentsItem.setFont(mainFont);

        openMenu.add(openCatalogItem);
        openMenu.add(openContractorsItem);
        openMenu.add(openDocumentsItem);

        openCatalogItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionHandler.showCatalog();
            }
        });

        openContractorsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionHandler.showContractors();
            }
        });

        openDocumentsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionHandler.showDocumentList();
            }
        });
    }

    private void createReportMenu() {
        reportMenu = new JPopupMenu();
        JMenuItem reportLogItem = new JMenuItem(reportLogItemText);
        reportLogItem.setFont(mainFont);

        reportMenu.add(reportLogItem);

        reportLogItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionHandler.showLogReport();
            }
        });
    }

    private void createBtnListeners() {
        //Кнопка Открыть
        openBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                openMenu.show(openBtn, e.getX(), e.getY());
            }
        });

        //Кнопка Экспорт
        xlsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionHandler.exportToExcelFromCurrentComponent();
            }
        });

        //Кнопка Отчет
        reportBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                reportMenu.show(reportBtn, e.getX(), e.getY());
            }
        });
    }

    private void showFrm() {
        frm.setVisible(true);
    }

    public JPanel getCardPane() {
        return cardPane;
    }

    public JFrame getFrm() {
        return frm;
    }

}
