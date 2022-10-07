package ru.todolist.javafx.start;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.todolist.javafx.controllers.MainController;
import ru.todolist.javafx.objects.Language;
import ru.todolist.javafx.utils.ManagerLocale;

import java.io.IOException;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

@SuppressWarnings("deprecation")
public class Main extends Application implements Observer {

    private static final String FXML_MAIN = "/fxml/todolist.fxml";
    public static final String BUNDLES_FOLDER = "TestBundle";

    private Stage primaryStage;

    private Parent fxmlMain;

    private MainController mainController;
    private FXMLLoader fxmlLoader;

    private VBox currentRoot;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        createGUI(ManagerLocale.RU_LOCALE);
    }

    private void createGUI(Locale ruLocale) {
        currentRoot = loadFXML(ruLocale);
        Scene scene = new Scene(currentRoot, 1000, 750);
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(650);
        primaryStage.setMinWidth(750);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    /**
     * loadFXML(lang.getLocale()); -> получить новое дерево компонетов с нужной локалью
     *         currentRoot.getChildren().setAll(newNode.getChildren()); -> заменить старые дочерник компонента на новые - с другой локалью
     */
    @Override
    public void update(Observable o, Object arg) {
        Language lang = (Language) arg;
        VBox newNode = loadFXML(lang.getLocale());
        currentRoot.getChildren().setAll(newNode.getChildren());
    }

    /**
     * загружает дерево компонентов и возвращает в виде VBox (корневой элемент в FXML)
     */
    private VBox loadFXML(Locale locale) {
        fxmlLoader = new FXMLLoader();

        fxmlLoader.setLocation(getClass().getResource(FXML_MAIN));
        fxmlLoader.setResources(ResourceBundle.getBundle(BUNDLES_FOLDER, locale));

        VBox node = null;

        try {
            node = (VBox) fxmlLoader.load();

            mainController = fxmlLoader.getController();
            mainController.addObserver( this);
            primaryStage.setTitle(fxmlLoader.getResources().getString("todo_list"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return node;
    }

}
