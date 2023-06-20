package pl.better.foodzillabackend.email.logic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.email.logic.model.command.ResetPasswordCommand;
import pl.better.foodzillabackend.email.logic.model.domain.RecoveryCode;
import pl.better.foodzillabackend.email.logic.repository.RecoveryCodeRepository;
import pl.better.foodzillabackend.utils.rabbitmq.email.EmailProducer;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServerService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final RecoveryCodeRepository codeRepository;
    private final CustomerRepository customerRepository;
    protected final EmailProducer producer;

    @Transactional
    public boolean requestPasswordResetEmail(String email) {
        Optional<Customer> customer = customerRepository.findCustomerByEmail(email);
        customer.ifPresent(this::sendEmail);
        return customer.isPresent();
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

    private void sendEmail(Customer customer) {
        producer.send(customer);
    }
}
