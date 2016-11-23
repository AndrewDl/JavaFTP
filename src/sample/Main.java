package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;

public class Main extends Application {

    static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("JavaFTP");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        stage = primaryStage;

        setTrayIcon(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static void showStage() {
        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }

    private static void setTrayIcon(Stage stage) {
        java.awt.Toolkit.getDefaultToolkit();

        if(! SystemTray.isSupported() ) {
            return;
        }

        //создем елемент всплывающего меню иконки трея
        PopupMenu trayMenu = new PopupMenu();
        MenuItem itemExit = new MenuItem("Exit");
        itemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        //добавляем созданый элемент в меню
        trayMenu.add(itemExit);

        //устанавливаем иконку
        URL imageURL = Main.class.getResource("file.png");

        Image icon = Toolkit.getDefaultToolkit().getImage(imageURL);
        TrayIcon trayIcon = new TrayIcon(icon, "JavaFTP", trayMenu);
        trayIcon.setImageAutoSize(true);

        SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        trayIcon.displayMessage("JavaFTP", "Application started!", TrayIcon.MessageType.INFO);
    }
}
