package pl.better.foodzillabackend.mail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.better.foodzillabackend.mail.model.command.ResetPasswordCommand;

@Service
@RequiredArgsConstructor
public class MailServerService {
    public boolean requestPasswordResetEmail(String email) {
        return false;
    }

    public boolean resetPassword(ResetPasswordCommand command) {
        return false;
    }
}
