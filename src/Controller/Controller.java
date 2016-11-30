package Controller;

import javafx.fxml.Initializable;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Основной контроллер
 */
public class Controller implements Initializable {


    public FTPClient ftpClient = new FTPClient();

    //переменные для ссылок на остальные контроллеры
    ControllerLogin controllerLogin;
    ControllerInfo controllerInfo;
    ControllerFileViewer controllerFileViewer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //получаем ссылку на контроллер
        //и контролеру даем ссылку на главный контроллер
        //панель логина
        controllerLogin = ControllerLogin.instance;
        controllerLogin.init(this);

        //панель информации
        controllerInfo = ControllerInfo.instance;
        controllerInfo.init(this);

        //окошко файлового менеджера
        controllerFileViewer = ControllerFileViewer.instance;
        controllerFileViewer.init(this);

        //подписка обработчика события на нажатие кнопки в контроллере панели логина
        controllerLogin.addOnConnectEvent(new IOnConnectHandler() {
            @Override
            public void run() {
                //при нажатии кнопки на окне логина - подключится к серверу используя введеные данные
                String Host = controllerLogin.getHost();
                int Port = controllerLogin.getPort();
                String Login = controllerLogin.getLogin();
                String Password = controllerLogin.getPassword();
                connectToFTP(Host, Port, Login, Password);
            }
        });
    }

    /**
     * Подключение к серверу, используя параметры подключения
     * @param Host адресс сервера
     * @param Port порт
     * @param Login имя пользователя
     * @param Password пароль
     */
    public void connectToFTP(String Host, int Port, String Login, String Password){
        int port = Port;

        try {
            ftpClient.connect(Host, port);
            showServerReply(ftpClient);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                controllerInfo.showInfo("Operation failed. Server reply code: " + replyCode);
                return;
            }
            ftpClient.enterLocalActiveMode();
            boolean success = ftpClient.login(Login, Password);
            showServerReply(ftpClient);
            if (!success) {
                controllerInfo.showInfo("Could not login to the server");
                return;
            } else {
                controllerInfo.showInfo("LOGGED IN SERVER");
            }

            controllerFileViewer.showFiles( getFiles("") );
        } catch (IOException ex) {
            controllerInfo.showInfo("Oops! Something wrong happened");
            ex.printStackTrace();
        }

    }

    /**
     * Loggout and close connection with FTP
     */
    public void disconnectFromFTP(){
        try {
            ftpClient.logout();
            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод возвращает список файлов в указаной директортт
     * @param directoryName директория на удаленном сервере
     * @return список файлов
     */
    public FTPFile[] getFiles(String directoryName) {

        //если соеденение было разорвано сервером по таймауту - переподключится
        if (!ftpClient.isConnected()){
            controllerInfo.showInfo("Reconnect!");

            String Host = controllerLogin.getHost();
            int Port = controllerLogin.getPort();
            String Login = controllerLogin.getLogin();
            String Password = controllerLogin.getPassword();
            connectToFTP(Host, Port, Login, Password);
        }

        FTPFile[] files = new FTPFile[0];
        try {
            files = ftpClient.listFiles(directoryName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return files;
    }

    /**
     * Выводит информацию о подключении
     * @param ftpClient
     */
    private void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();

        if (replies != null && replies.length > 0) {
            //for each reply output this info
            for (String aReply : replies) {
                controllerInfo.showInfo("SERVER: " + aReply);
            }
        }
    }
}
