package com.rabbit.gut.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto для отправки уведомления о новом сообщении")
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
}
