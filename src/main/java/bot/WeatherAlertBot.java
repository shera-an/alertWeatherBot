package bot;
import model.User;
import service.UserService;
import config.BotConfig;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class WeatherAlertBot extends TelegramLongPollingBot {
    private final UserService userService = new UserService();
    //Храним состояние диалога с каждым пользователем
    private final Map<Long, String> userStates = new HashMap<>();


    //Токен передаем в конструктор
    public WeatherAlertBot() {
        super(BotConfig.getBotToken());
    }
    //Переопределяем метод и передаем имя бота
    @Override
    public String getBotUsername() {
        return BotConfig.getBotName();
    }
    @Override
    public void onUpdateReceived(Update update){
        if(update.hasMessage() && update.getMessage().hasText()){
            String chatIdStr = update.getMessage().getChatId().toString();
            Long ctaId = update.getMessage().getChatId();
            String text = update.getMessage().getText();

            try {
                // Проверяем есть ли полльзователь
                User user = userService.getUser(ctaId);
                String state = userStates.get(ctaId);

                //Если комнда /start
                if(text.equals("/start")){
                    if(user != null){
                        sendMessage(chatIdStr,"✅ Вы уже зарегистрированы, " + user.getFirstName() + "!");
                    }else{
                        userStates.put(ctaId, "AWAITING_NAME");
                        sendMessage(chatIdStr,"👋 Привет! Как тебя зовут?");
                    }
                    return;
                    }
                if(user == null){
                    sendMessage(chatIdStr, "❌ Пожалуйста, сначала отправь /start для регистрации.");
                    return;
                }
                //обработка диалога регистрации
                if("AWAITING_NAME".equals(state)){
                    userStates.put(ctaId,"AWAITING_CITY");
                    user.setFirstName(text);
                    sendMessage(chatIdStr,"Отлично, " + text + "! Из какого ты города?");
                    return;
                }
                if("AWAITING_CITY".equals(state)){
                    user.setCity(text);
                    user.setUserName(update.getMessage().getFrom().getUserName());
                    user.setRegisteredAt(LocalDateTime.now());

                    userService.saveUser(user);
                    userStates.remove(ctaId);

                    sendMessage(chatIdStr, "✅ Регистрация завершена! Твой город: " + text +
                            "\nТеперь я буду присылать тебе погоду. Используй /weather, чтобы узнать прогноз.");
                    return;
                }
                //обычые команды
                if(text.equals("/weather")){
                    String city = user.getCity();
                    if(city == null || city.isEmpty()){
                        sendMessage(chatIdStr,"❌ Ты не указал город. Отправь /start и укажи город.");
                    }else {
                        sendMessage(chatIdStr, "🌤️ Погода в " + city + ": Функционал в разработке (заглушка)");
                    }
                    return;
                }
                if (text.equals("/help")){
                    sendMessage(chatIdStr,"Доступные комады:\n +" +
                            "/start - регистрация\n" +
                            "/weather - погода в твоем городе\n" +
                            "/subcribe 9:00 - ежедневные уведомления\n" +
                            "/help - помощь");
                    return;
                }
                //Любой другой текст
                sendMessage(chatIdStr,"ℹ️ Я не знаю такой команды. Напиши /help для списка команд.");
                }catch (SQLException e){
                e.printStackTrace();
                sendMessage(chatIdStr, "❌ Ошибка базы данных. Попробуй позже.");
            }
        }
    }
    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
