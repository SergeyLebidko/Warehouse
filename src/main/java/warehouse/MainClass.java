package warehouse;

import warehouse.data_components.DBHandler;

import javax.swing.*;

public class MainClass {

    private static DBHandler dbHandler;
    private static ActionHandler actionHandler;

    public static void main(String[] args) {
        //Пытаемся получить подключение к базе данных
        try {
            dbHandler = new DBHandler();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка подключения к базе данных. Приложение будет закрыто.", "", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Создаем класс логики приложения
        actionHandler = new ActionHandler();

        //Если подключение успешно получено, то запускаем создание интерфейса
        new GUI();
    }

    public static DBHandler getDbHandler() {
        return dbHandler;
    }

    public static ActionHandler getActionHandler(){return actionHandler;};

}
