package org.team3todo.secure.secure_team_3_todo_api.config;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SanitizerConfig {

    @Bean
    public PolicyFactory sanitizerPolicy() {
        return Sanitizers.FORMATTING.and(Sanitizers.LINKS);
    }
}