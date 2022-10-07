package ru.todolist.javafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.todolist.javafx.objects.Task;
import ru.todolist.javafx.utils.DialogManager;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;


public class EditDialogController implements Initializable {

    @FXML
    public TextField txtTask;
    @FXML
    public TextField txtTime;
    @FXML
    public TextField txtStatus;
    @FXML
    public Button btnOk;
    @FXML
    public Button btnCancel;

    @FXML
    public Label labelError;

    private Task task;

    /**
     * для определения нажатой кнопки
     */
    public boolean saveClicked = false;
    private ResourceBundle resourceBundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    public Task getTask() {
        return task;
    }

    /**
     * Возвращает true, если пользователь кликнул Ок, в другом случае false
     */
    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }

    public void setTaskManagement(Task task) {
        if (task == null) {
            return;
        }
        this.task = task;
        txtTask.setText(task.getTask());
        txtTime.setText(task.getTime());
        txtStatus.setText(task.getStatus());
        saveClicked = false;
    }

    /**
     * Окно закроется когда будут все данные введены в поля
     * Для этого надо заполнить поля
     * @param actionEvent событие
     */

    public void actionSave(ActionEvent actionEvent) {
        if (!checkValues()){
            return;
        }
        task.setTask(txtTask.getText());
        task.setTime(txtTime.getText());
        task.setStatus(txtStatus.getText());
        saveClicked = true;
        actionClose(actionEvent);

    }

    /**
     * проверяет строки заполнены ли они текстом
     */
    private boolean checkValues() {
        if (txtTask.getText().trim().length()==0 ||
                txtTime.getText().trim().length()==0){
            DialogManager.showInfoDialog(resourceBundle.getString("error"), resourceBundle.getString("fill_field"));
            return false;
        }
        return true;
    }
}

