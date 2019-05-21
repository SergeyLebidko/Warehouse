package warehouse;

import warehouse.data_components.DBHandler;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class MainClass {

    private static DBHandler dbHandler;
    private static ActionHandler actionHandler;
    private static GUI gui;

    public static void main(String[] args) {
        //Пытаемся получить подключение к базе данных
        try {
            dbHandler = new DBHandler();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка подключения к базе данных. Приложение будет закрыто.", "", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        //Создаем класс реализующий логику приложения
        actionHandler = new ActionHandler();

        //Если подключение успешно получено, то запускаем создание интерфейса
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    gui = new GUI();
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Не удалось создать главное окно. Приложение будет закрыто.", "", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        //После создания интерфейса инициализируем объект actionHandler
        actionHandler.init();
    }

    public static DBHandler getDbHandler() {
        return dbHandler;
    }

    public static ActionHandler getActionHandler() {
        return actionHandler;
    }

    public static GUI getGui() {
        return gui;
    }

}
