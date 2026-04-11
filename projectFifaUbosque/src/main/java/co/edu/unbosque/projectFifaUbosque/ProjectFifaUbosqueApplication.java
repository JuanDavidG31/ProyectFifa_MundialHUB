package co.edu.unbosque.projectFifaUbosque;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ProjectFifaUbosqueApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectFifaUbosqueApplication.class, args);

	}

	@Bean
	ModelMapper getModelMapper() {
		return new ModelMapper();
	}

}
