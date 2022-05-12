package st.project.studyWithUs.service.userTeamService;



import org.springframework.stereotype.Service;
import st.project.studyWithUs.domain.Team;
import st.project.studyWithUs.domain.UserTeam;
import st.project.studyWithUs.vo.MemberInSameVO;
import st.project.studyWithUs.vo.StudyTimeVO;

import java.util.List;

@Service
public interface UserTeamService {

    void save(UserTeam userStudyTeam);

    List<UserTeam> findAll();

    void dropStudyTeam(Long tId, Long uId);


    List<Team> findMyTeams(Long uid);

    List<MemberInSameVO> completeMembers(Long tID);

    List<StudyTimeVO> myStudyTime(Long uID);

    UserTeam findByUIDAndTID(Long uid, Long tid);
}
