package pl.better.foodzillabackend.mail.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.mail.model.command.ResetPasswordCommand;
import pl.better.foodzillabackend.mail.service.MailServerService;

@Controller
@RequiredArgsConstructor
public class MailController {

    private final MailServerService service;

    @MutationMapping
    public boolean requestPasswordResetEmail(@Argument String email) {
        return service.requestPasswordResetEmail(email);
    }

    @MutationMapping
    public boolean resetPassword(@Argument ResetPasswordCommand command) {
        return service.resetPassword(command);
    }
}
