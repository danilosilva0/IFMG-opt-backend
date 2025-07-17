package edu.ifmg.produtos.resources;

import edu.ifmg.produtos.dtos.NewPasswordDTO;
import edu.ifmg.produtos.dtos.RequestTokenDTO;
import edu.ifmg.produtos.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth")
public class AuthResource {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "recover-token")
    public ResponseEntity<Void> createRecoverToken(@Valid @RequestBody RequestTokenDTO dto){

        authService.createRecoverToken(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "new-password")
    public ResponseEntity<Void> saveNewPassword(@Valid @RequestBody NewPasswordDTO dto){

        authService.saveNewPassword(dto);
        return ResponseEntity.noContent().build();
    }
}
