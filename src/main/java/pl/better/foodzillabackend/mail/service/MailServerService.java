package pl.better.foodzillabackend.mail.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.mail.model.command.ResetPasswordCommand;
import pl.better.foodzillabackend.mail.model.domain.RecoveryCode;
import pl.better.foodzillabackend.mail.repository.RecoveryCodeRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServerService {

    private static final String TITLE = "Password Restart ~ Foodzilla";
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;
    @Value("classpath:email.html")
    private Resource resource;
    private final RecoveryCodeRepository codeRepository;
    private final CustomerRepository customerRepository;

    public boolean requestPasswordResetEmail(String email) {
        Optional<Customer> customer = customerRepository.findCustomerByEmail(email);
        return customer.isPresent() ? sendEmail(customer.get()) : false;
    }

    @Transactional
    public boolean resetPassword(ResetPasswordCommand command) {
        if (codeRepository.existsByCodeAndEmail(command.resetPasswordToken(),
                command.email())) {
            Optional<Customer> customer = customerRepository.findCustomerByEmail(command.email());
            customer.ifPresent(customer1 -> {
                customer1.setPassword(command.newPassword());
                customerRepository.saveAndFlush(customer1);
            });
            return codeRepository.removeByCodeAndEmail(command.resetPasswordToken(),
                    command.email());
        } else {
            return false;
        }
    }

    private boolean sendEmail(Customer customer) {
        try {
            String code = generateCode();
            codeRepository.saveAndFlush(new RecoveryCode(customer.getEmail(), generateCode()));
            MimeMessage message = javaMailSender.createMimeMessage();
            message.setSubject(TITLE);
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(customer.getEmail());
            helper.setText(generateBody(code), true);

            javaMailSender.send(message);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    private String generateBody(String code) throws IOException {
        String content = Files.readString(resource.getFile().toPath());
        return content.replace("*", code);
    }

    private String generateCode() {
        return ThreadLocalRandom.current()
                .ints(10, 33, 127)
                .mapToObj(code -> String.valueOf((char) code))
                .collect(Collectors.joining());
    }
}
