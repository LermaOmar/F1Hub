package ptzt.f1Hub;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class F1HubApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.load();
		System.setProperty("KEY", dotenv.get("KEY"));
		System.setProperty("EXPIRATION_TIME", dotenv.get("EXPIRATION_TIME"));

		SpringApplication.run(F1HubApplication.class, args);
	}

}
