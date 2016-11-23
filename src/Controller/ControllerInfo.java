package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Отвечает за вывод информации на екран
 */
public class ControllerInfo {

    @FXML private TextArea textAreaInfo;

    Controller mainController = null;

    // Способ передать ссылку на клас-контроллер в главный контроллер
    // Создаем статичесскую переменную в которой будем хранить ссылку на созданый екземпляр контролера
    public static ControllerInfo instance = null;
    // Как только вызывается конструктор класа - "запоминаем" ссылку на него в статическое поле.
    // Таким образром мы сможем получить ссылку на этот контроллер в дальнейшем
    public ControllerInfo(){
        instance = this;
    }

    /**
     * Инициализируем контроллер передачей в него ссылки на главный контроллер
     * @param controller ссылка на контроллер
     */
    public void init(Controller controller){
        mainController = controller;
    }

    /**
     * выводит текст в окно и добавляет временной штамп
     * @param info
     */
    public void showInfo(String info){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();

        textAreaInfo.setText(textAreaInfo.getText() + "\n" + dateFormat.format(date).toString()+ " " + info);

    }


}
