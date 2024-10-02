package fall24.swp391.g1se1868.koiauction.model;

import lombok.Builder;

@Builder
public record MailBody(String to, String subject, String text) {
}
