package pl.better.foodzillabackend.mail.model.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "recoveryCode")
public class RecoveryCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String code;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    public RecoveryCode(String email, String code) {
        this.email = email;
        this.code = code;
    }

    @PrePersist
    private void onCreate() {
        date = LocalDateTime.now();
    }
}
