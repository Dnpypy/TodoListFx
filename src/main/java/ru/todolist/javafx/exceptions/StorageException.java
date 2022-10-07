package ru.todolist.javafx.exceptions;

import java.io.IOException;

public class StorageException extends IOException {

    private static final long serialVersionUID = 1L;

    public StorageException(String string) {
        super(string);
    }
}
