package com.javanauta.notificacao.business;

import com.javanauta.notificacao.business.dto.RecuperarSenhaDTO;
import com.javanauta.notificacao.business.dto.StatusTarefaDTO;
import com.javanauta.notificacao.business.dto.TarefasDTO;
import com.javanauta.notificacao.business.enums.StatusNotificacao;
import com.javanauta.notificacao.business.exception.EmailException;
import com.javanauta.notificacao.business.messages.kafka.producer.KafkaProducer;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final KafkaProducer kafkaProducer;

    @Value("${envio.email.remetente}")
    public String remetente;

    @Value("${envio.email.nomeRemetente}")
    public String nomeRementente;

    public void enviaEmail(TarefasDTO dto) {
        try {
            MimeMessage mensagem = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mensagem, true, StandardCharsets.UTF_8.name());
            mimeMessageHelper.setFrom(new InternetAddress(remetente, nomeRementente));
            InternetAddress[] destinatarios = InternetAddress.parse(dto.getEmailUsuario());
            mimeMessageHelper.setTo(destinatarios);
            mimeMessageHelper.setSubject("Notificação de Tarefa");

            Context context = new Context();
            context.setVariable("nomeTarefa", dto.getNomeTarefa());
            context.setVariable("dataEvento", dto.getDataEvento());
            context.setVariable("descricao", dto.getDescricao());
            String template = templateEngine.process("email-notificacao", context);
            mimeMessageHelper.setText(template, true);
            javaMailSender.send(mensagem);
            kafkaProducer.enviarStatusTarefa(buildStatus(dto.getId(), StatusNotificacao.NOTIFICADO));

        } catch (AddressException e) {
            kafkaProducer.enviarStatusTarefa(buildStatus(dto.getId(), StatusNotificacao.FALHA));
            throw new EmailException("Erro ao enviar o email - email invalido" + e.getCause());
        } catch (MailAuthenticationException e) {
            kafkaProducer.enviarStatusTarefa(buildStatus(dto.getId(), StatusNotificacao.PENDENTE));
            throw new EmailException("Erro ao enviar o email-autenticacao smtp incorreta" + e.getCause());
        } catch (MailSendException e) {
            kafkaProducer.enviarStatusTarefa(buildStatus(dto.getId(), StatusNotificacao.FALHA));
            throw new EmailException("Erro ao enviar o email" + e.getCause());
        } catch (Exception e) {
            kafkaProducer.enviarStatusTarefa(buildStatus(dto.getId(), StatusNotificacao.PENDENTE));
            throw new EmailException("Erro ao enviar o email" + e.getCause());
        }
    }

    private StatusTarefaDTO buildStatus(String id, StatusNotificacao status) {
        return StatusTarefaDTO.builder()
                .id(id)
                .statusNotificacao(status)
                .build();
    }


    public void enviaEmailRecuperacaoSenha(RecuperarSenhaDTO dto) {
        try {
            MimeMessage mensagem = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, StandardCharsets.UTF_8.name());
            helper.setFrom(new InternetAddress(remetente, nomeRementente));
            helper.setTo(InternetAddress.parse(dto.getEmailUsuario()));
            helper.setSubject("Recuperação de senha");

            Context context = new Context();
            context.setVariable("senha", dto.getSenha());
            String template = templateEngine.process("email-recuperacao-senha", context);
            helper.setText(template, true);
            javaMailSender.send(mensagem);


        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailException("Erro ao enviar email" + e.getCause());
        }
    }

}
