package st.project.studyWithUs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class StudyWithUsApplication {
	public static void main(String[] args) {
		SpringApplication.run(StudyWithUsApplication.class, args);
	}
}
