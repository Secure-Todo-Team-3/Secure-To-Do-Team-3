package org.team3todo.secure.secure_team_3_todo_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy // need this to enable Aspect for DB stuff.
public class SecureTeam3TodoApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureTeam3TodoApiApplication.class, args);
	}

}
