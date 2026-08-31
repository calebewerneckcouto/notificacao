package com.javanauta.notificacao.controller;

import com.javanauta.notificacao.business.EmailService;
import com.javanauta.notificacao.business.dto.TarefasDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<Void>enviarEmail(@RequestBody TarefasDTO tarefasDTO){
        emailService.enviaEmail(tarefasDTO);
        return ResponseEntity.ok().build();

    }

}
