package st.project.studyWithUs.controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import st.project.studyWithUs.argumentresolver.Login;
import st.project.studyWithUs.domain.Team;
import st.project.studyWithUs.domain.User;
import st.project.studyWithUs.service.teamService.TeamService;
import st.project.studyWithUs.service.userTeamService.UserTeamService;
import st.project.studyWithUs.vo.MemberInSameVO;
import st.project.studyWithUs.vo.StudyTimeVO;
import st.project.studyWithUs.vo.TeamVO;
import st.project.studyWithUs.websocketHandler.ExistWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserTeamController {


    private final UserTeamService userTeamService;
    private final TeamService teamService;
    private final ExistWebSocketHandler handler;

    @ResponseBody
    @GetMapping("/dropStudyTeam")
    public boolean dropTeam(@RequestParam Long tId, @Login User loginUser) throws Exception{
        handler.noticeLeave(loginUser.getUID(), tId);

        userTeamService.dropStudyTeam(tId, loginUser.getUID());
        teamService.decreaseCurrentCount(tId);
        return true;
    }

    @ResponseBody
    @GetMapping("findMyTeams")
    public List<TeamVO> findMyTeams (@Login User loginUser){


        List <Team> teams = userTeamService.findMyTeams(loginUser.getUID());
        List <TeamVO> tvo = new ArrayList<>();

        for(Team t : teams){
            TeamVO teamVO = new TeamVO();
            teamVO.setTeamName(t.getTeamName());
            teamVO.setTeamDesc(t.getTeamDesc());
            teamVO.setCurrentCount(t.getCurrentCount());
            teamVO.setHeadCount(t.getHeadCount());
            teamVO.setTotalDepositPoint(t.getTotalDepositPoint());
            teamVO.setTtID(t.getTID());
            teamVO.setEndDate(t.getEndDate());
            teamVO.setStartDate(t.getStartDate());
            teamVO.setTeamImage(t.getTeamImage());
            teamVO.setTargetTime(t.getTargetTime());
            tvo.add(teamVO);
        }
        return tvo;
    }

    @ResponseBody
    @GetMapping("/completeMembers")
    public List<MemberInSameVO> completeMembers(@RequestParam Long tID){
        return userTeamService.completeMembers(tID);
    }

    @ResponseBody
    @GetMapping("/studyTime")
    public List<StudyTimeVO> myStudyTime(@Login User loginUser){
        return userTeamService.myStudyTime(loginUser.getUID());
    }
}
