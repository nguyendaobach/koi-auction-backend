package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.MailBody;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendSimpleMessage(MailBody mailBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailBody.to());
        message.setFrom("nguyendaobach@gmail.com");
        message.setSubject(mailBody.subject());
        message.setText(mailBody.text());
        javaMailSender.send(message);
    }

    String emailTemplate = """
            <!DOCTYPE html>
            <html lang="vi">
            <head>
                <meta charset="UTF-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <title>KOIAUCTION - Thông báo đấu giá</title>
                <style>
                    h1, p, div {
                        margin: 0;
                        padding: 0;
                        font-family: "Arial", sans-serif;
                        box-sizing: border-box;
                    }
                    .box {
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        height: 100vh;
                        width: 100%;
                    }
                    .container {
                        max-width: 600px;
                        width: 100%;
                        background-color: white;
                        border-radius: 8px;
                        box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1);
                        padding: 24px;
                    }
                    .header {
                        text-align: center;
                    }
                    .header h1 {
                        font-size: 28px;
                        font-weight: bold;
                        color: #b41712;
                    }
                    .header p {
                        font-size: 14px;
                        color: #6b7280;
                        margin-top: 8px;
                    }
                    .content {
                        margin-top: 24px;
                    }
                    .content p {
                        color: #374151;
                        font-size: 16px;
                        margin-bottom: 16px;
                    }
                    .footer {
                        font-size: 12px;
                        color: #9ca3af;
                        text-align: center;
                        margin-top: 24px;
                        border-top: 1px solid #e5e7eb;
                        padding-top: 16px;
                    }
                    .footer p {
                        margin-bottom: 8px;
                        font-size: 14px;
                        opacity: 0.5;
                    }
                </style>
            </head>
            <body>
                <div class="box">
                    <div class="container">
                        <div class="header">
                            <h1>KOIAUCTION</h1>
                            <p>Nền tảng đấu giá cá Koi trực tuyến</p>
                        </div>
                        <div class="content">
                            <p>Xin chào,</p>
                            <p>%s</p> <!-- Nội dung thông báo sẽ thay đổi tại đây -->
                        </div>
                        <div class="footer">
                            <p>Email này được gửi tự động, vui lòng không trả lời.</p>
                            <p>© 2024 KOIAUCTION. Tất cả các quyền được bảo lưu.</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """;

    public void sendHtmlMessage(MailBody mailBody) {
        try {
            // Sử dụng MimeMessage để gửi email HTML
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            // Ghép chuỗi để tạo nội dung email HTML
            StringBuilder sb = new StringBuilder(emailTemplate);
            int startIndex = sb.indexOf("%s");
            sb.replace(startIndex, startIndex + 2, mailBody.text());
            String formattedHtmlContent = sb.toString();
            helper.setTo(mailBody.to());
            helper.setFrom("nguyendaobach@gmail.com");
            helper.setSubject(mailBody.subject());
            helper.setText(formattedHtmlContent, true); // true để chỉ định đây là HTML

            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

