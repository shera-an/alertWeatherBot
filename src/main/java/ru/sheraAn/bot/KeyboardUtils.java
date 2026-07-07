package ru.sheraAn.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardUtils {

    public static ReplyKeyboardMarkup getMainMenu() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Погода сейчас");
        row1.add("Прогноз");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Погода по геолокации");
        row2.add("Настройки");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Помощь");

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        return createKeyboard(keyboard);
    }

    public static ReplyKeyboardMarkup getLocationRequestKeyboard() {
        KeyboardRow row = new KeyboardRow();

        KeyboardButton locationButton = new KeyboardButton("Отправить геолокацию");
        locationButton.setRequestLocation(true);

        row.add(locationButton);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setSelective(true);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup getForecastMenu() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add("На сегодня");
        row1.add("На 3 дня");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("На неделю");
        row2.add("Назад");

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);

        return createKeyboard(keyboard);
    }

    public static ReplyKeyboardMarkup getSettingsMenu() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Изменить имя");
        row1.add("Изменить город");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Назад");

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);

        return createKeyboard(keyboard);
    }

    private static ReplyKeyboardMarkup createKeyboard(List<KeyboardRow> keyboard) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        keyboardMarkup.setSelective(true);
        return keyboardMarkup;
    }
}