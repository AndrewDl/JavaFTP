package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.ArrayList;

/**
 * Created by Andrew on 22.11.2016.
 */
public class ControllerLogin {

    @FXML private TextField textFieldPort;
    @FXML private TextField textFieldHost;
    @FXML private TextField textFieldLogin;
    @FXML private TextField textFieldPassword;

    //список обработчиков события
    private ArrayList<IOnConnectHandler> onConnectEvent = new ArrayList();

    Controller mainController = null;

    // Способ передать ссылку на клас-контроллер в главный контроллер
    // Создаем статичесскую переменную в которой будем хранить ссылку на созданый екземпляр контролера
    public static ControllerLogin instance = null;
    // Как только вызывается конструктор класа - "запоминаем" ссылку на него в статическое поле.
    // Таким образром мы сможем получить ссылку на этот контроллер в дальнейшем
    public ControllerLogin(){
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
     * Добавить обработчик на событие
     * @param onEvent действия на событие
     */
    public void addOnConnectEvent(IOnConnectHandler onEvent){
        onConnectEvent.add(onEvent);
    }

    /**
     * Метод запускает все подписаные обработчики события
     */
    private void fireEvent(){
        for (IOnConnectHandler handler : onConnectEvent){
            handler.run();
        }
    }

    public String getHost(){
        return textFieldHost.getText();
    }

    public String getLogin()
    {
        return textFieldLogin.getText();
    }

    public String getPassword()
    {
        return textFieldPassword.getText();
    }

    public void onConnectPressed(ActionEvent actionEvent) {
        fireEvent();
    }

    public int getPort() {
        return Integer.valueOf( textFieldPort.getText() );
    }

}
