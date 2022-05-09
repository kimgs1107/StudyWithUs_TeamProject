package st.project.studyWithUs.service.studyingService;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyingSchedule {

    private final StudyingService studyingService;

    @Scheduled(cron = "0 0 0 * * *")
    public void resetRealTime(){
        studyingService.resetRealTime();
    }
}
