package Controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Клас описывает правила работы с файлами FTP сервера
 * Основные функции. Скачивание/Загрузка/Удаление/Переходы между директориями
 */
public class ControllerFileViewer implements Initializable {

    //содержит путь к директории в которую будут загружатся файлы
    @FXML private TextField textFieldDownloadDirectory;
    //полный путь к файлу, который нужно загрузить на сервер
    @FXML private TextField textFieldUploadFilePath;
    //путь файла на сервере
    @FXML private TextField textFieldFilePath;

    //визуальный елемент для отображения файлов в виде дерева
    @FXML private TreeView<String> treeViewServerFiles;

    //отдельная переменная для сохранения пути к директории
    private String DirectoryPath = "";
    //отдельная переменная для сохранения названия файла
    private String FilePath = "";

    private Node rootIcon = null;
    private Node fileIcon = null;

    Controller mainController = null;

    // Способ передать ссылку на клас-контроллер в главный контроллер
    // Создаем статичесскую переменную в которой будем хранить ссылку на созданый екземпляр контролера
    public static ControllerFileViewer instance = null;
    // Как только вызывается конструктор класа - "запоминаем" ссылку на него в статическое поле.
    // Таким образром мы сможем получить ссылку на этот контроллер в дальнейшем
    public ControllerFileViewer(){
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
     * Отображает список файлов как дерево
     * @param files
     */
    public void showFiles(FTPFile[] files){

        TreeItem<String> mainRoot = new TreeItem<String> ("");
        mainRoot.setExpanded(true);

        for (FTPFile f: files) {
            //если перед нами директория - задать ей значек папки
            if (f.isDirectory())
            {
                rootIcon =  new ImageView(
                    new Image(getClass().getResourceAsStream("folder.png"))
                );
                TreeItem<String> rootItem = new TreeItem<String> (f.getName(), rootIcon);
                rootItem.setExpanded(false);
                mainRoot.getChildren().add(rootItem);
            }
            else {
                fileIcon =  new ImageView(
                        new Image(getClass().getResourceAsStream("file.png"))
                );
                TreeItem<String> item = new TreeItem<String>(f.getName(),fileIcon);
                mainRoot.getChildren().add(item);
            }
        }

        treeViewServerFiles.setRoot(mainRoot);
    }

    /**
     * Формирует путь к указаному елементу дерева
     * @param item елемент для которого необходимо узнать путь
     * @return путь к указаному елементу на сервере
     */
    private String getPath(TreeItem<String> item){
        String path;
        if (item != null) {
            path = item.getValue();
            if (item.getParent() != null)
                path = getPath(item.getParent()) + "/" + path;
        }
        else{
            path = "";
        }

        return path;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        treeViewServerFiles.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue, TreeItem<String> newValue) {
                if (newValue != null) {
                    TreeItem<String> selectedItem = (TreeItem<String>) newValue;

                    FilePath = selectedItem.getValue();
                    textFieldFilePath.setText(DirectoryPath + getPath(selectedItem));
                }
            }
        });
    }

    /**
     * Обработка нажатия кнопки "вперед" определяет новый путь для получения файлов, которые нужно отобразить
     * @param actionEvent
     */
    public void onForwardClick(ActionEvent actionEvent) {
        showFiles(mainController.getFiles(textFieldFilePath.getText()));
        DirectoryPath = textFieldFilePath.getText(); //запоминаем текущую директорию
    }

    /**
     * Вычисляем адрес предыдущей директории
     * @param actionEvent
     */
    public void onBackwardClick(ActionEvent actionEvent) {

        //разбиваем адре по по разделителю
        String[] directories = DirectoryPath.split("/");

        //новый адрес будет такой же как и старый, но нужно убрать последнюю директорию из него
        String lastAppendedDirectory = directories[directories.length-1];
        DirectoryPath = DirectoryPath.substring(0,DirectoryPath.length() - lastAppendedDirectory.length() - 1 );

        //выводим получившийся адрес
        textFieldFilePath.setText(DirectoryPath);

        //обновляем список файов в окне
        showFiles(mainController.getFiles(DirectoryPath));
    }

    public void onDownloadClick(ActionEvent actionEvent) {

        String remoteFile = textFieldFilePath.getText();
        File localFile = new File(textFieldDownloadDirectory.getText()+FilePath);
        OutputStream outputStream = null;
        boolean success = false;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(localFile));
            success = mainController.ftpClient.retrieveFile(remoteFile, outputStream);

            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (success) {
            mainController.controllerInfo.showInfo("File has been downloaded successfully.");
        } else {
            mainController.controllerInfo.showInfo("File has not been downloaded.");
        }


        showFiles(mainController.getFiles(DirectoryPath));
    }

    public void onUploadClick(ActionEvent actionEvent) {

        File firstLocalFile = new File(textFieldUploadFilePath.getText());

        String firstRemoteFile = DirectoryPath + "/" + firstLocalFile.getName();

        mainController.controllerInfo.showInfo("Start uploading file...");
        boolean done = false;
        try {
            InputStream inputStream = new FileInputStream(firstLocalFile);
            done = mainController.ftpClient.storeFile(firstRemoteFile, inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (done) {
            mainController.controllerInfo.showInfo("The file is uploaded successfully.");
        } else {
            mainController.controllerInfo.showInfo("File has not been uploaded.");
        }

        showFiles(mainController.getFiles(DirectoryPath));
    }

    public void onDeleteClick(ActionEvent actionEvent) {

        boolean done = false;

        try {
            done = mainController.ftpClient.deleteFile(textFieldFilePath.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (done) {
            mainController.controllerInfo.showInfo("The file is deleted successfully.");
        } else {
            mainController.controllerInfo.showInfo("File has not been deleted.");
        }

        showFiles(mainController.getFiles(DirectoryPath));
    }
}

