package pl.better.foodzillabackend.mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.mail.model.command.ResetPasswordCommand;
import pl.better.foodzillabackend.mail.repository.RecoveryCodeRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServerService {

    private static final String TITLE = "Password Restart ~ Foodzilla";
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private final String sender;
    private final RecoveryCodeRepository codeRepository;
    private final CustomerRepository customerRepository;

    public boolean requestPasswordResetEmail(String email) {
        Optional<Customer> customer = customerRepository.findCustomerByEmail(email);
        return customer.isPresent() ? sendEmail(email) : false;
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
            codeRepository.removeByCodeAndEmail(command.resetPasswordToken(),
                    command.email());
            return true;
        } else {
            return false;
        }
    }

    private boolean sendEmail(String email) {
        try {
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(email);
            mailMessage.setText(generateBody(email));
            mailMessage.setSubject(TITLE);

            javaMailSender.send(mailMessage);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    private String generateBody(String email) {
        return email;
    }
}
