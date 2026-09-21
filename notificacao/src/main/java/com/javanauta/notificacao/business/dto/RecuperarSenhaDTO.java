package com.javanauta.notificacao.business.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecuperarSenhaDTO {

    private String emailUsuario;
    private String senha;
}
