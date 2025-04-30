package ptzt.f1Hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class F1HubApplication {

	public static void main(String[] args) {
		SpringApplication.run(F1HubApplication.class, args);
	}

}
