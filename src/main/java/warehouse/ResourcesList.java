package warehouse;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;

public class ResourcesList {

    //Параметры подключения к базе данных
    public static final String jdbcClassName = "org.sqlite.JDBC";
    public static final String databaseConnectionString = "jdbc:sqlite:database\\database.db";

    //Параметры файла для экспорта данных в excel
    public static final String exportFolder = "export";

    //Ресурсы для кнопок
    private static ImageLoader imageLoader = new ImageLoader();
    public static final Image logoImage = imageLoader.getImage("logo");
    public static final ImageIcon openIcon = imageLoader.getImageIcon("open");
    public static final ImageIcon addIcon = imageLoader.getImageIcon("add");
    public static final ImageIcon removeIcon = imageLoader.getImageIcon("remove");
    public static final ImageIcon editIcon = imageLoader.getImageIcon("edit");
    public static final ImageIcon reportIcon = imageLoader.getImageIcon("report");
    public static final ImageIcon excelIcon = imageLoader.getImageIcon("excel");
    public static final ImageIcon removeFilterIcon = imageLoader.getImageIcon("remove_filter");
    public static final ImageIcon toUpIcon = imageLoader.getImageIcon("to_up");
    public static final ImageIcon toDownIcon = imageLoader.getImageIcon("to_down");
    public static final ImageIcon noOrderIcon = imageLoader.getImageIcon("no_order");
    public static final ImageIcon editIconSmall = imageLoader.getImageIcon("edit_small");
    public static final ImageIcon excelIconSmall = imageLoader.getImageIcon("excel_small");
    public static final ImageIcon addIconSmall = imageLoader.getImageIcon("add_small");
    public static final ImageIcon removeIconSmall = imageLoader.getImageIcon("remove_small");

    public static final String openBtnText = "";
    public static final String openBtnToolTip = "Открыть";

    public static final String addBtnText = "";
    public static final String addBtnToolTip = "Создать";

    public static final String editBtnText = "";
    public static final String editBtnToolTip = "Изменить";

    public static final String removeBtnText = "";
    public static final String removeBtnToolTip = "Удалить";

    public static final String xlsBtnText = "";
    public static final String xlsBtnToolTip = "Экспорт в Excel";

    public static final String reportBtnText = "";
    public static final String reportBtnToolTip = "Отчет";

    public static final String removeFilterBtnText = "";
    public static final String removeFilterToolTip = "Очистить фильтр";

    //Размеры главного окна
    public static final String frmTitle = "Warehouse";
    public static final int FRM_WIDTH = 1200;
    public static final int FRM_HEIGHT = 900;
    public static final int MIN_FRM_WIDTH = 800;
    public static final int MIN_FRM_HEIGHT = 600;

    //Размеры диалогового окна Документы
    public static final int DOCUMENT_DIALOG_WIDTH = 800;
    public static final int DOCUMENT_DIALOG_HEIGHT = 600;

    //Русские варианты надписей в стандартных диалоговых окнах
    public static final String yesButtonText = "Да";
    public static final String noButtonText = "Нет";
    public static final String cancelButtonText = "Отмена";
    public static final String inputDialogTitle = "";

    //Названия пунктов всплывающих меню
    public static final String openCatalogItemText = "Каталог";
    public static final String openContractorsItemText = "Контрагенты";
    public static final String openDocumentsItemText = "Документы";

    //Общие параметры для табличных компонентов
    public static final int rowHeight = 40;
    public static final Color gridColor = Color.LIGHT_GRAY;
    public static final Color headerColor = new Color(230, 230, 230);
    public static final Color evenCellsColor = new Color(240, 240, 240);
    public static final Color notEvenCellsColor = new Color(255, 255, 255);

    //Параметры ширины столбцов для отображения документов
    public static final int maxWidthDocumentNumberColumn = 100;
    public static final int minWidthDocumentNumberColumn = 50;
    public static final int maxWidthDocumentDateColumn = 220;
    public static final int minWidthDocumentDateColumn = 120;
    public static final int maxWidthDocumentTypeColumn = 120;
    public static final int minWidthDocumentTypeColumn = 100;

    //Параметры ширины столбцов для отображения простых списоков (Каталог и Контрагенты)
    public static final int maxWidthSimpleTableNumberColumn = 100;
    public static final int minWidthSimpleTableNumberColumn = 50;

    //Параметры ширины столбцов для отображения списка операций
    public static final int maxWidthOperationNumberColumn = 150;
    public static final int minWidthOperationNumberColumn = 100;

    //Сообщения об ошибках
    public static final String failCatalogAccess = "Не удалось получить содержимое каталога. Ошибка:";
    public static final String failContractorsAccess = "Не удалось получить доступ к списку контрагентов. Ошибка:";
    public static final String failExportFolderCreate = "Не удалось создать папку для экспорта. Ошибка:";
    public static final String failOpenExportXLSFile = "Не удалось открыть созданный файл. Ошибка:";
    public static final String failDocumentsAccess = "Не удалось получить список документов. Ошибка:";
    public static final String failExportXLSFile = "Не удалось записать файл на диск. Ошибка:";

    //Главный шрифт приложения (для таблиц, полей ввода и меню)
    public static final Font mainFont = new Font("Arial", Font.PLAIN, 16);

    //Параметры выгрузки в excel
    public static final int fontFileHeaderSize = 256;
    public static final int fontColumnHeaderSize = 200;

    private static class ImageLoader {

        private static final String[] imageNamesList = {
                "logo",
                "open",
                "add",
                "remove",
                "edit",
                "report",
                "remove_filter",
                "excel",
                "to_up",
                "to_down",
                "no_order",
                "edit_small",
                "excel_small",
                "add_small",
                "remove_small"
        };

        private HashMap<String, Image> imageMap = new HashMap<>();

        public ImageLoader() {
            ClassLoader classLoader = getClass().getClassLoader();
            Image image;
            for (String name : imageNamesList) {
                try {
                    image = ImageIO.read(classLoader.getResourceAsStream("images/" + name + ".png"));
                } catch (IOException e) {
                    image = null;
                }
                imageMap.put(name, image);
            }
        }

        public Image getImage(String name) {
            return imageMap.get(name);
        }

        public ImageIcon getImageIcon(String name) {
            Image image;
            image = imageMap.get(name);
            if (image == null) return null;
            return new ImageIcon(image);
        }

    }

}
