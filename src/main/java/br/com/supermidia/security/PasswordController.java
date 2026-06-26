package br.com.supermidia.security;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/password")
public class PasswordController {
    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @PostMapping("/send-reset-code")
    public ResponseEntity<MessageResponseDTO> sendResetCode(@RequestBody @Valid EmailRequestDTO emailRequestDTO) {
        passwordService.sendResetCode(emailRequestDTO.getEmail());
        return ResponseEntity.ok(new MessageResponseDTO("Código de redefinição enviado para o e-mail informado."));
    }

    @PostMapping("/validate-reset-code")
    public ResponseEntity<MessageResponseDTO> validateResetCode(@RequestBody @Valid CodeValidationRequestDTO codeRequestDTO) {
        passwordService.validateCode(codeRequestDTO.getEmail(), codeRequestDTO.getCode());
        return ResponseEntity.ok(new MessageResponseDTO("Código validado com sucesso."));
    }

    @PostMapping("/update-password")
    public ResponseEntity<MessageResponseDTO> updatePassword(@RequestBody @Valid ResetPasswordRequestDTO passwordRequestDTO) {
        passwordService.resetPassword(passwordRequestDTO.getEmail(), passwordRequestDTO.getNewPassword());
        return ResponseEntity.ok(new MessageResponseDTO("Senha atualizada com sucesso."));
    }
}
