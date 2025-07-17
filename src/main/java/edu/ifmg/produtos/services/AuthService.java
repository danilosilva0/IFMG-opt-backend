package edu.ifmg.produtos.services;

import edu.ifmg.produtos.dtos.EmailDTO;
import edu.ifmg.produtos.dtos.NewPasswordDTO;
import edu.ifmg.produtos.dtos.RequestTokenDTO;
import edu.ifmg.produtos.entities.PasswordRecover;
import edu.ifmg.produtos.entities.User;
import edu.ifmg.produtos.repository.PasswordRecoveryRepository;
import edu.ifmg.produtos.repository.UserRepository;
import edu.ifmg.produtos.services.exceptions.ResourceNotFound;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${email.password-recover.token.minutes}")
    private int tokenMinutes;

    @Value("${email.password-recover.uri}")
    private String uri;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordRecoveryRepository passwordRecoveryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createRecoverToken(RequestTokenDTO dto) {

        User user = userRepository.findByEmail(dto.getEmail());
        if (user == null) {
            throw new ResourceNotFound("Email not found");
        }

        String token = UUID.randomUUID().toString();

        PasswordRecover passwordRecover = new PasswordRecover();
        passwordRecover.setToken(token);
        passwordRecover.setEmail(user.getEmail());
        passwordRecover.setExpiration(Instant.now().plusSeconds(this.tokenMinutes * 60L));

        passwordRecoveryRepository.save(passwordRecover);

        String body = "Acesse o link para definir uma nova senha (válido por:" + tokenMinutes + ")" + "\n\n" + uri+token;
        emailService.sendMail(new EmailDTO(user.getEmail(), "Recuperação de senha", body));
    }

    public void saveNewPassword(NewPasswordDTO dto) {

        List<PasswordRecover> list = passwordRecoveryRepository.searchValidToken(dto.getToken(), Instant.now());

        if (list.isEmpty()) {
            throw new ResourceNotFound("Token not found");
        }

        User user = userRepository.findByEmail(list.getFirst().getEmail());

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

    }
}
