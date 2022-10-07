package ru.todolist.javafx.utils;

import ru.todolist.javafx.objects.Language;

import java.util.Locale;

public class ManagerLocale {

    public static final Locale RU_LOCALE = new Locale("ru");
    public static final Locale EN_LOCALE = new Locale("en");

    private static Language currentLang;

    public static Language getCurrentLang(){
        return currentLang;
    }

    public static void setCurrentLang(Language currentLang){
        ManagerLocale.currentLang = currentLang;
    }
}
