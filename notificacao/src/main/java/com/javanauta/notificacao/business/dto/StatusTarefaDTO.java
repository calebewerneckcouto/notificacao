package com.javanauta.notificacao.business.dto;

import com.javanauta.notificacao.business.enums.StatusNotificacao;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatusTarefaDTO {
    private String id;
    private StatusNotificacao statusNotificacao;

}
