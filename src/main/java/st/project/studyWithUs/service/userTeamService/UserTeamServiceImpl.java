package st.project.studyWithUs.service.userTeamService;



import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import st.project.studyWithUs.domain.Team;
import st.project.studyWithUs.domain.UserTeam;
import st.project.studyWithUs.repository.TeamRepository;
import st.project.studyWithUs.repository.UserTeamRepository;
import st.project.studyWithUs.vo.MemberInSameVO;
import st.project.studyWithUs.vo.StudyTimeVO;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserTeamServiceImpl implements UserTeamService {


    private final UserTeamRepository userTeamRepository;
    private final TeamRepository teamRepository;

    @Override
    @Transactional
    public void save(UserTeam userStudyTeam) { userTeamRepository.save(userStudyTeam);
    }

    @Override
    public List<UserTeam> findAll() {
        return userTeamRepository.findAll();
    }

    @Override
    public void dropStudyTeam(Long tId, Long uId) {
        List<UserTeam> userTeam = userTeamRepository.findAll();
        for (UserTeam ut : userTeam) {
            if(ut.getTeam().getTID()==tId&&ut.getUser().getUID()==uId)userTeamRepository.deleteById(ut.getUtID());
        }
    }

    //자신의 팀 찾기
    @Override
    public List<Team> findMyTeams(Long uid) {
        List<Team> teams = new ArrayList<>();
        List<UserTeam> userTeamList = userTeamRepository.findAll();
        for(UserTeam ut : userTeamList){
            if(ut.getUser().getUID()==uid){
                teams.add(teamRepository.findBytID(ut.getTeam().getTID()));
            }
        }
        return teams;
    }

    @Override
    public List<MemberInSameVO> completeMembers(Long tID){
        List<MemberInSameVO> members = new ArrayList<>();
        Team team = teamRepository.findBytID(tID);

        Long targetTime = team.getTargetTime();
        List<UserTeam> users = userTeamRepository.findUserTeamByTID(team.getTID());
        for(UserTeam u : users){
            Long realTime = u.getRealTime();
            if(targetTime*60 <= realTime){
                MemberInSameVO mem = new MemberInSameVO();
                mem.setTID(team.getTID());
                mem.setUuID(u.getUser().getUID());
                mem.setUserName(u.getUser().getUserName());
                mem.setUserImage(u.getUser().getUserImage());
                members.add(mem);
            }
        }

        return members;
    }

    @Override
    public List<StudyTimeVO> myStudyTime(Long uID){
        List<StudyTimeVO> studyTime = new ArrayList<>();

        List<UserTeam> userTeams = userTeamRepository.findUserTeamByUID(uID);
        for(UserTeam ut : userTeams){
            StudyTimeVO stVO = new StudyTimeVO();
            stVO.setTtID(ut.getTeam().getTID());
            stVO.setTeamName(ut.getTeam().getTeamName());
            stVO.setTargetTime(ut.getTeam().getTargetTime());
            stVO.setTotalTime(ut.getTotalTime());

            LocalDate start = ut.getTeam().getStartDate();
            LocalDate end = ut.getTeam().getEndDate();
            stVO.setPeriod(ChronoUnit.DAYS.between(start, end)+1);

            studyTime.add(stVO);
        }

        return studyTime;
    }

}
