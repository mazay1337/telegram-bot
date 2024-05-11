package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.ParameterResolutionDelegate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.service.NotificationTaskService;
import pro.sky.telegrambot.service.TelegramBotClient;

import javax.annotation.PostConstruct;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private static final Logger LOG = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final Pattern NOTIFICATION_TASK_PATTERN = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2})(\\s)([А-яA-z\\s\\d]+)");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm");
    private final TelegramBot telegramBot;
    private final TelegramBotClient telegramBotClient;
    private final NotificationTaskService notificationTaskService;


    public TelegramBotUpdatesListener(TelegramBot telegramBot,
                                      TelegramBotClient telegramBotClient,
                                      NotificationTaskService notificationTaskService) {
        this.telegramBot = telegramBot;
        this.telegramBotClient = telegramBotClient;
        this.notificationTaskService = notificationTaskService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        try {
            updates.forEach(update -> {
                LOG.info("Processing update: {}", update);
                String text = update.message().text();
                long chatId = update.message().from().id();
                if ("/start".equals(text)) {
                    telegramBotClient.sendMessage(chatId,
                            "Для планирования задачи отправьте ее в формате:\\n*01.01.2022 20:00 Сделать домашнюю работу*");
                } else if (text != null) {
                    Matcher matcher = NOTIFICATION_TASK_PATTERN.matcher(text);



                    if (!matcher.find()) {
                        telegramBotClient.sendMessage(chatId,
                                "Задача отправлена не по формату:\\n*01.01.2022 20:00 Сделать домашнюю работу*");
                    } else {
                        LocalDateTime notificationDateTime;
                        String message = matcher.group(3);
                        if ((notificationDateTime = parse(matcher.group(1))) != null) {
                            notificationTaskService.save(message, chatId, notificationDateTime);
                            telegramBotClient.sendMessage(chatId, "Ваша задача запланирована");
                        } else {
                            telegramBotClient.sendMessage(chatId,
                                    "Неверный формат даты:\\n*01.01.2022 20:00*");
                        }
                    }


                } else {
                    telegramBotClient.sendMessage(chatId, "Я понимаю только текст");
                }

            });
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Nullable
    private LocalDateTime parse(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

}
