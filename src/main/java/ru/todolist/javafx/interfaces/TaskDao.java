package ru.todolist.javafx.interfaces;

import javafx.collections.ObservableList;
import ru.todolist.javafx.objects.Task;

public interface TaskDao {

    boolean addTask(Task task);

    boolean deleteTask(Task task);

    boolean updateTask(Task task);

    boolean completeTask(Task task, String s);

    ObservableList<Task> findAll();

    ObservableList<Task> find(String text);

}
