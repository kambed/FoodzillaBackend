package pl.better.foodzillabackend.email.logic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.email.logic.service.MailServerService;
import pl.better.foodzillabackend.email.logic.model.command.ResetPasswordCommand;

@Controller
@RequiredArgsConstructor
public class MailServerController {

    private final MailServerService service;

    @MutationMapping
    public boolean requestPasswordResetEmail(@Argument String email) {
        return service.requestPasswordResetEmail(email);
    }

    @MutationMapping
    public boolean resetPassword(@Argument ResetPasswordCommand resetPassword) {
        return service.resetPassword(resetPassword);
    }
}
