package fall24.swp391.g1se1868.koiauction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KoiauctionApplication {

	public static void main(String[] args) {
		SpringApplication.run(KoiauctionApplication.class, args);
	}

}
