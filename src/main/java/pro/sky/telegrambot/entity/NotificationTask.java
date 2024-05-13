package pro.sky.telegrambot.entity;


import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class NotificationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    private long userId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "notification_date_time")
    private LocalDateTime notificationDateTime;

    public NotificationTask() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getNotificationDateTime() {
        return notificationDateTime;
    }

    public void setNotificationDateTime(LocalDateTime notificationDateTime) {
        this.notificationDateTime = notificationDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return id == that.id && userId == that.userId && Objects.equals(message, that.message) && Objects.equals(notificationDateTime, that.notificationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, message, notificationDateTime);
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                ", notificationDateTime=" + notificationDateTime +
                '}';
    }

    public NotificationTask(long id, long userId, String message, LocalDateTime notificationDateTime) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.notificationDateTime = notificationDateTime;
    }
}
