package st.project.studyWithUs.service.studyingService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import st.project.studyWithUs.domain.User;
import st.project.studyWithUs.domain.UserTeam;
import st.project.studyWithUs.repository.UserRepository;
import st.project.studyWithUs.repository.UserTeamRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyingServiceImpl implements StudyingService{

    private final UserTeamRepository userTeamRepository;

    @Override
    public void resetRealTime(){
        userTeamRepository.resetRealTime();
    }

    @Override
    public void save(UserTeam ut) {
        userTeamRepository.save(ut);
    }

    @Override
    public UserTeam findUserTeam(Long uID, Long tID) {
        return userTeamRepository.findByuIDandtID(uID,tID);
    }

    @Override
    public List<UserTeam> findUserTeamByTID(Long tID){
        return userTeamRepository.findUserTeamByTID(tID);
    }
}
