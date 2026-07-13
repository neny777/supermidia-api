package br.com.supermidia.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Habilita @Async — usado no envio de e-mail (recuperação de senha), que
 * conversa com SMTP externo e não pode segurar a requisição HTTP.
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {
}
