package st.project.studyWithUs.service.studyingService;


import st.project.studyWithUs.domain.UserTeam;

import java.util.List;

public interface StudyingService {
    void resetRealTime();
    void save(UserTeam ut);
    UserTeam findUserTeam(Long uID, Long tID);

    List<UserTeam> findUserTeamByTID(Long tID);
}
