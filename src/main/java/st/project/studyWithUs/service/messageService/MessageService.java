package st.project.studyWithUs.service.messageService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageService {
    List<Long> findTid(String event);

    String findEvent(Long t);

    List<String> findPhoneNumber(Long t);

    String findTitle(Long t);
}
