package st.project.studyWithUs.service.teamService;


import org.springframework.stereotype.Service;
import st.project.studyWithUs.domain.Team;
import st.project.studyWithUs.domain.User;
import st.project.studyWithUs.vo.TeamVO;

import java.util.List;

@Service
public interface TeamService {

    List<Team> findMyTeam(Long uId);

    List<Team> findAll();

    void increaseData(Long tId, Long point);

    void decreaseCurrentCount(Long tId);

    Team findBytID(Long tId);

    void deleteTeam(Long tID);

    List<TeamVO> searchTeam(String teamName, User user);

    List<TeamVO> findAllTeams(User user);

    List<TeamVO> findSearchTeam(String teamID);

    void saveTeam(Team team);

    Team findByTeamName(String teamName);

    Boolean checkTeamPassword(Long tID, String password);

    List<Team> findAllEnd(String event);
}
