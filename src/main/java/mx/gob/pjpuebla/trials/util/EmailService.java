package mx.gob.pjpuebla.trials.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSenderImpl mailSender;
    private final Configuration freemarkerConfig;

    public void sendMail(List<String> to, List<String> cc, List<String> bcc,
                         String subject, String template, Map<String, Object> model) {
        try {
            Template tpl = freemarkerConfig.getTemplate(template);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(tpl, model);
            sendMail(to, cc, bcc, subject, html);
        } catch (IOException | TemplateException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public void sendMail(List<String> to, List<String> cc, List<String> bcc,
                         String subject, String message, String... filename) {
        try {
            if (to.isEmpty() && cc.isEmpty() && bcc.isEmpty()) {
                throw new IllegalArgumentException("No recipients found to send");
            }
            MimeMessage mm = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mm, true, "UTF-8");
            helper.setFrom("notificaciones@pjpuebla.gob.mx");
            helper.setTo(to.toArray(new String[0]));
            helper.setCc(cc.toArray(new String[0]));
            helper.setBcc(bcc.toArray(new String[0]));
            helper.setReplyTo("no-reply-to@gmail.com");
            helper.setSubject(subject);
            helper.setText(message, true);

            if (filename != null && filename.length > 0) {//si vienen archivo adjuntos
                FileSystemResource file;
                for (String f : filename) {//para cada adjunto
                    file = new FileSystemResource(new File(f));
                    helper.addAttachment(f, file);
                }
            }
            mailSender.send(mm);
        } catch (IllegalArgumentException | MessagingException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

}
