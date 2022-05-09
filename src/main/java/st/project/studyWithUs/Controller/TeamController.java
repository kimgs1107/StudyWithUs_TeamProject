package st.project.studyWithUs.Controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import st.project.studyWithUs.argumentresolver.Login;
import st.project.studyWithUs.domain.Team;
import st.project.studyWithUs.domain.User;
import st.project.studyWithUs.domain.UserTeam;
import st.project.studyWithUs.service.teamService.TeamService;
import st.project.studyWithUs.service.userTeamService.UserTeamService;
import st.project.studyWithUs.vo.TeamVO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final UserTeamService userTeamService;

    @ResponseBody
    @GetMapping("/findAllTeams")
    public List<TeamVO> teams (@RequestParam int check, @RequestParam String teamName, @Login User loginUser){
        // 검색해서 나오는 스터디 리스트
        if(check==1) return teamService.searchTeam(teamName, loginUser);
        // 비로그인/로그인에서 볼 수 있는 모든 스터디 리스트
        else return teamService.findAllTeams(loginUser);
    }
}
