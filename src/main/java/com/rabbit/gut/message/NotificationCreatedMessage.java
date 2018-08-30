package com.rabbit.gut.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Dto для отправки уведомления о новом оповещении")
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreatedMessage {

    /**
     * Идентификатор сотрудика
     */
    private Long employeeId;

    /**
     * Заголовок оповещения
     */
    private String header;

    /**
     * Основной текст оповещения
     */
    private String text;

    /**
     * Фио отправителя уведомления (Автора задачи)
     */
    private String author;

    /**
     * Предельный срок задачи по которой сформированно уведомление
     */
    private ZonedDateTime date;

    /**
     * Идентификатор нотификации для перехода
     */
    private Long notificationId;
}
