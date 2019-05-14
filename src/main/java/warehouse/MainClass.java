package warehouse;

import javax.swing.*;

public class MainClass {

    private static DBHandler dbHandler;

    public static void main(String[] args) {
        //Пытаемся получить подключение к базе данных
        try {
            dbHandler = new DBHandler();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка подключения к базе данных. Приложение будет закрыто.", "", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Если подключение успешно получено, то запускаем создание интерфейса
        new GUI();
    }

    public static DBHandler getDbHandler() {
        return dbHandler;
    }

}
