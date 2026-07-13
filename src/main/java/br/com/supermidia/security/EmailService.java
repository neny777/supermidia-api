package br.com.supermidia.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private static final Logger log = LoggerFactory.getLogger(EmailService.class);

	private final JavaMailSender mailSender;

	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	/**
	 * Envia um e-mail simples com assunto e corpo.
	 * 
	 * @param to      O endereço de e-mail do destinatário.
	 * @param subject O assunto do e-mail.
	 * @param text    O corpo do e-mail.
	 */
	// @Async: a conversa SMTP pode levar vários segundos (timeouts de 20s) e não
	// pode segurar a requisição HTTP — o frontend desiste em poucos segundos e
	// acusava "Servidor indisponível" em rede lenta. A resposta imediata também
	// não denuncia se o e-mail existe. Falha de envio fica no log do servidor.
	@Async
	public void sendEmail(String to, String subject, String text) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			message.setFrom("suporte@supermidiaalfenas.com.br");
			mailSender.send(message);
			log.info("E-mail enviado para {}", to);
		} catch (Exception e) {
			log.error("Erro ao enviar e-mail para {}", to, e);
		}
	}
}
