package pl.better.foodzillabackend.utils.rabbitmq.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.mail.model.domain.RecoveryCode;
import pl.better.foodzillabackend.mail.repository.RecoveryCodeRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailConsumer {

    private static final String TITLE = "Password Restart ~ Foodzilla";
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final JavaMailSender javaMailSender;
    private final RecoveryCodeRepository codeRepository;
    @Value("${spring.mail.username}")
    private String sender;
    @Value("classpath:email.html")
    private Resource resource;

    @RabbitListener(queues = "email")
    @Async("rabbitMqTaskExecutor")
    @Transactional
    public void send(Customer customer) {
        try {
            String code = saveNewCustomerCode(customer);

            MimeMessage message = javaMailSender.createMimeMessage();
            message.setSubject(TITLE);
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(customer.getEmail());
            helper.setText(generateBody(code), true);
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private String generateBody(String code) throws IOException {
        String content = Files.readString(resource.getFile().toPath());
        return content.replace("*", code);
    }

    private String saveNewCustomerCode(Customer customer) {
        String code = generateCode();
        if (codeRepository.existsByEmail(customer.getEmail())) {
            codeRepository.removeByEmail(customer.getEmail());
        }
        codeRepository.saveAndFlush(new RecoveryCode(customer.getEmail(), code));
        return code;
    }

    private String generateCode() {
        return ThreadLocalRandom.current()
                .ints(10, 0, CHARACTERS.length())
                .mapToObj(CHARACTERS::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }
}
