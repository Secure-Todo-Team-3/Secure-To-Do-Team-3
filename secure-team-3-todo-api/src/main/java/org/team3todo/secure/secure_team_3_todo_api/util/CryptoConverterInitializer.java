package org.team3todo.secure.secure_team_3_todo_api.util;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component 
public class CryptoConverterInitializer {

    @Value("${db.field.encryption.key}")
    private String encryptionKey;

    @PostConstruct
    public void initialize() {
        StringCryptoConverter.setEncryptionKey(encryptionKey);
    }
}