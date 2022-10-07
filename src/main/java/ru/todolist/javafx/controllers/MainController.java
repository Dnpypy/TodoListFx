package ru.todolist.javafx.controllers;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.todolist.javafx.interfaces.impls.TaskHibernateImpl;
import ru.todolist.javafx.objects.Language;
import ru.todolist.javafx.objects.Task;
import ru.todolist.javafx.time.CurrentTime;
import ru.todolist.javafx.utils.DialogManager;
import ru.todolist.javafx.utils.ManagerLocale;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Observable;
import java.util.ResourceBundle;


public class MainController extends Observable implements Initializable {

    /**
     * TaskDbDaoImpl todoListImpl = new TaskDbDaoImpl(); -> реализация БД отключена
     * Hibernate реализация, название переменной такое же, как у реализации базы данных
     */
    private static TaskHibernateImpl todoListImpl = new TaskHibernateImpl();
    @FXML
    public Button btnNew;
    @FXML
    public Button btnDelete;
    @FXML
    public Button btnSave;
    @FXML
    public Button btnUpdate;
    @FXML
    public Button btnComplete;
    @FXML
    public Button btnExit;
    @FXML
    public Button btnClear;

    @FXML
    public TextField txtSearch;
    @FXML
    public Button btnSearch;
    @FXML
    public TableView<Task> txtTodoList;

    @FXML
    public TableColumn<Task, String> mainTask;

    @FXML
    public TableColumn<Task, String> timeTask;

    @FXML
    public TableColumn<Task, Boolean> statusTask;

    @FXML
    public Label labelCount;

    @FXML
    public Label labelTime;

    @FXML
    private ComboBox changeLocaleBox;

    private Parent fxmlEdit;
    private final FXMLLoader fxmlLoader = new FXMLLoader();
    private EditDialogController editDialogController;
    private Stage editDialogStage;

    private Stage mainStage;

    private ResourceBundle resourceBundle;

    private static final String RU_CODE = "ru";
    private static final String EN_CODE = "en";

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        mainTask.setCellValueFactory(new PropertyValueFactory<Task, String>("task"));
        timeTask.setCellValueFactory(new PropertyValueFactory<Task, String>("time"));
        statusTask.setCellValueFactory(new PropertyValueFactory<Task, Boolean>("status"));
        setupClearButtonField(txtSearch);


        initListeners();
        try {
            filldata();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            fxmlLoader.setLocation(getClass().getResource("/fxml/todolistEdit.fxml"));
            fxmlLoader.setResources(ResourceBundle.getBundle("TestBundle", new Locale("ru")));
            fxmlEdit = fxmlLoader.load();
            editDialogController = fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     *  по-умолчанию показывать выбранный русский язык (можно текущие настройки языка сохранять в файл)
     */
    private void fillLangComboBox() {
        Language langRU = new Language(0, RU_CODE, resourceBundle.getString("ru"), ManagerLocale.RU_LOCALE);
        Language langEN = new Language(1, EN_CODE, resourceBundle.getString("en"), ManagerLocale.EN_LOCALE);

        changeLocaleBox.getItems().add(langRU);
        changeLocaleBox.getItems().add(langEN);

        if (ManagerLocale.getCurrentLang() == null) {
            ManagerLocale.setCurrentLang(langRU);
            changeLocaleBox.getSelectionModel().select(0);
        } else {
            changeLocaleBox.getSelectionModel().select(ManagerLocale.getCurrentLang().getIndex());
        }
    }

    private void filldata() throws IOException, ClassNotFoundException {
        fillTable();
        fillLangComboBox();
    }

    /**
     * todoListImpl.findAll(); заполнение листа из таблицы
     * setItems(list); -> заполнение tableview
     * txtTodoList.refresh(); -> обновление tableView
     */
    private void fillTable() {
        ObservableList<Task> list = todoListImpl.findAll();
        txtTodoList.setItems(list);
        txtTodoList.refresh();
    }

    private void setupClearButtonField(TextField txtSearch) {
        txtSearch.clear();
    }

    /**
     * слушатели событий
     * todoListImpl.getTasksList() -> слушатель изменений в таблице
     * updateCountLabel(); -> выводит количество задач
     * timeCurrent(); -> отображения времени
     * txtTodoList.setOnMouseClicked -> двойное нажатие мыши на строку
     * changeLocaleBox -> слушает изменение языка
     * txtTodoList.setRowFactory -> Слушатель изменяет цвета статуса состояния задачи
     * setChanged(); notifyObservers(selectedLang); -> уведомить всех слушателей, что произошла смена языка
     */
    private void initListeners() {

        todoListImpl.getTasksList().addListener(new ListChangeListener<Task>() {
            @Override
            public void onChanged(Change<? extends Task> change) {
                updateCountLabel();
                timeCurrent();
            }
        });


        txtTodoList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    editDialogController.setTaskManagement((Task) txtTodoList.getSelectionModel().getSelectedItem());
                    showDialog();
                    todoListImpl.updateTask((Task) txtTodoList.getSelectionModel().getSelectedItem());
                }
            }
        });


        changeLocaleBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Language selectedLang = (Language) changeLocaleBox.getSelectionModel().getSelectedItem();
                ManagerLocale.setCurrentLang(selectedLang);

                setChanged();
                notifyObservers(selectedLang);
            }
        });


        txtTodoList.setRowFactory(row -> new TableRow<Task>() {
            public void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.toString().contains("не выполнена")) {
                    row.getColumns().get(2).setId("error");
                } else {
                    row.getColumns().get(2).setId("not-error");
                }
            }
        });
    }

    /**
     * отображение текущего количество задач
     */
    private void updateCountLabel() {
        labelCount.setText(resourceBundle.getString("Number_tasks") + ": " + todoListImpl.getTasksList().size());
    }

    /**
     * отображение текущего времени
     */
    private void timeCurrent() {
        labelTime.setText(resourceBundle.getString("Time_tasks") + ": " + CurrentTime.currentTime());
    }

    /**
     * (!(source instanceof Button)) -> если нажата не кнопка выходим из метода
     */
    public void actionButtonPressed(ActionEvent event) throws Exception {

        Object source = event.getSource();
        boolean research = false;
        if (!(source instanceof Button)) {
            return;
        }
        Task selectedTask = (Task) txtTodoList.getSelectionModel().getSelectedItem();
        Button clickedButton = (Button) source;

        switch (clickedButton.getId()) {

            case "btnNew":
                editDialogController.setTaskManagement(new Task());
                showDialog();
                System.out.println("editDialogController.isSaveClicked() = " + editDialogController.isSaveClicked());

                if (editDialogController.isSaveClicked()) {
                    System.out.println("inner");
                    todoListImpl.addTask(editDialogController.getTask());
                }
                // fillTable();
                break;

            case "btnDelete":
                if (!taskIsSelected(selectedTask) || !(confirmDelete())) {
                    return;
                }
                todoListImpl.deleteTask(txtTodoList.getSelectionModel().getSelectedItem());
//                fillTable();
                break;

            case "btnUpdate":
                if (!taskIsSelected(selectedTask)) {
                    return;
                }
                editDialogController.setTaskManagement((Task) txtTodoList.getSelectionModel().getSelectedItem());
                showDialog();
                if (editDialogController.isSaveClicked()) {
                    todoListImpl.updateTask(selectedTask);
                }
                break;


            /**
             * меняется статус задачи выполнена или не выполнена
             * txtTodoList.refresh(); -> обновляет состояние таблицы tableview при выборе статуса
             * btn.exit -> выход из программы
             * btn.clear -> очищает поисковое окно
             */
            case "btnComplete":
                if (!taskIsSelected(selectedTask)) {
                    return;
                }
                var unComplete = "не выполнена";
                var complete = "выполнена";
                if (!(txtTodoList.getSelectionModel().getSelectedItem().getStatus().equals("не выполнена"))) {
                    txtTodoList.getSelectionModel().getSelectedItem().setStatus(unComplete);
                    todoListImpl.completeTask(selectedTask, unComplete);
                } else {
                    txtTodoList.getSelectionModel().getSelectedItem().setStatus(complete);
                    todoListImpl.completeTask(selectedTask, complete);
                }
                txtTodoList.refresh();
                break;

            case "btnExit":
                System.exit(1); //выход из программы
                break;

            case "btnClear":
                txtSearch.clear();
                break;
        }
        fillTable();
    }

    /**
     * проверяет выбрана задача или нет, возврашает статус
     * @param task текущая задача(выбрана)
     * @return выбрана задача или нет
     */
    private boolean taskIsSelected(Task task) {
        if (task == null) {
            DialogManager.showInfoDialog(resourceBundle.getString("error"), resourceBundle.getString("select_person"));
            return false;
        }
        return true;
    }

    /**
     * editDialogStage.showAndWait(); -> для ожидания закрытия окна
     */
    private void showDialog() {

        if (editDialogStage == null) {
            editDialogStage = new Stage();
            editDialogStage.setTitle(resourceBundle.getString("Edit"));
            editDialogStage.setMinHeight(150);
            editDialogStage.setMinWidth(300);
            editDialogStage.setResizable(false);
            editDialogStage.setScene(new Scene(fxmlEdit));
            editDialogStage.initModality(Modality.WINDOW_MODAL);
            editDialogStage.initOwner(mainStage);
        }

        editDialogStage.showAndWait();


    }

    /**
     * поиск по вхождению текста и по второму столбцу если ввели время или месяц
     * если пустая строка, то возвращается текущий список.
     *
     * @param actionEvent текущее событие
     */
    public void actionSearch(ActionEvent actionEvent) {

        if (txtSearch.getText().trim().length() == 0) {
            todoListImpl.findAll();
        } else {
            todoListImpl.find(txtSearch.getText());
        }
    }

    /**
     * подтверждает удаление задачи
     * @return удаляем, если нажата кнопка ок, иначе отмена
     */
    private boolean confirmDelete() {
        if (DialogManager.showConfirmDialog(resourceBundle.getString("confirm"), resourceBundle.getString("confirm_delete")).get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }

    }

}




