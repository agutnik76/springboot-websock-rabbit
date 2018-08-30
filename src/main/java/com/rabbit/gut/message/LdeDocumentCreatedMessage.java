package com.rabbit.gut.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto для отправки сообщенияя о создании документа в редакторе Luxsoft в очередь сообщений
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LdeDocumentCreatedMessage {

    /**
     * id сотрудника для уведомления
     */
    private Long employeeId;

    /**
     * Id документа в npa
     */
    private Long documentId;

    /**
     * Id документа в lde
     */
    private String ldeDocumentId;
}
