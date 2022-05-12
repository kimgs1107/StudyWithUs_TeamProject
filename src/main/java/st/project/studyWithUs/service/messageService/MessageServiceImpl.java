package st.project.studyWithUs.service.messageService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import st.project.studyWithUs.domain.Team;
import st.project.studyWithUs.repository.TeamRepository;
import st.project.studyWithUs.repository.UserRepository;
import st.project.studyWithUs.repository.UserTeamRepository;

import java.util.ArrayList;
import java.util.List;



@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {


    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final UserTeamRepository userTeamRepository;


    @Override
    public List<Long> findTid(String event) {
        List <Long> tid = new ArrayList<>();
        List<Team> teams = teamRepository.findAll();
        for(Team t : teams){
            String s = t.getStartDate().toString();
            s = s.substring(0, 10);
            if (s.equals(event)) {
                tid.add(t.getTID());
            }
        }
        return tid;


    }

    @Override
    public String findEvent(Long t) {
        return null;
    }

    @Override
    public List<String> findPhoneNumber(Long t) {


        return userRepository.findPhoneNumber(t);

    }

    @Override
    public String findTitle(Long t) {
        return teamRepository.findBytID(t).getTeamName();
    }
}
