package pl.better.foodzillabackend.mail.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.mail.model.command.ResetPasswordCommand;
import pl.better.foodzillabackend.mail.model.domain.RecoveryCode;
import pl.better.foodzillabackend.mail.repository.RecoveryCodeRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServerService {

    private static final String TITLE = "Password Restart ~ Foodzilla";
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Value("${spring.mail.username}")
    private String sender;
    @Value("classpath:email.html")
    private Resource resource;
    private final RecoveryCodeRepository codeRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public boolean requestPasswordResetEmail(String email) {
        Optional<Customer> customer = customerRepository.findCustomerByEmail(email);
        return customer.isPresent() ? sendEmail(customer.get()) : false;
    }

    @Transactional
    public boolean resetPassword(ResetPasswordCommand command) {
        Optional<RecoveryCode> recoveryCode = codeRepository.findByCodeAndEmail(command.resetPasswordToken(),
                command.email());
        if (recoveryCode.isPresent()) {
            long days = ChronoUnit.DAYS.between(recoveryCode.get().getDate(),
                    LocalDateTime.now());
            if (days > 1) {
                codeRepository.removeByCodeAndEmail(command.resetPasswordToken(),
                        command.email());
                return false;
            }

            Optional<Customer> customer = customerRepository.findCustomerByEmail(command.email());
            customer.ifPresent(customer1 -> {
                customer1.setPassword(passwordEncoder.encode(command.newPassword()));
                customerRepository.saveAndFlush(customer1);
            });
            codeRepository.removeByCodeAndEmail(command.resetPasswordToken(),
                    command.email());
            return true;
        } else {
            return false;
        }
    }

    private boolean sendEmail(Customer customer) {
        try {
            String code = saveNewCustomerCode(customer);

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
