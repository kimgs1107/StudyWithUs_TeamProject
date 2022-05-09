package st.project.studyWithUs.Controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import st.project.studyWithUs.argumentresolver.Login;
import st.project.studyWithUs.domain.User;
import st.project.studyWithUs.domain.UserTeam;
import st.project.studyWithUs.service.pointInfo.PointInfoService;
import st.project.studyWithUs.service.teamService.TeamService;
import st.project.studyWithUs.service.userService.UserService;
import st.project.studyWithUs.service.userTeamService.UserTeamService;
import st.project.studyWithUs.vo.UserVO;


@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PointInfoService pointInfoService;
    private final UserTeamService userTeamService;
    private final TeamService teamService;

    @GetMapping("/")
    public String home(){
        return "home";
    }

    @GetMapping("/myPage")
    public String myPage(Model model){
        User user = userService.find(1L);
        model.addAttribute("loginUser", user);
        return "myPage";
    }

    @GetMapping("/chargePoint")
    public String chargePoint(){
        return "chargePoint";
    }


    @ResponseBody
    @GetMapping("/deposit")
    public void deposit(Long point){
        userService.updatePoint(point, 1L);
        pointInfoService.deposit(point, 1L );
    }


    //포인트 충전 로직
    @ResponseBody
    @GetMapping("/studyDeposit")
    public boolean studyDeposit( @RequestParam Long tId , @RequestParam Long point, @Login User loginUser){


        if(userService.checkPoint(point, loginUser.getUID())==true){ //현재 유저가 들고있는 포인트로 참여할 수 있다면,
            teamService.increaseData(tId, point); //팀의 현재 인원 및 보증금 올리기.
            UserTeam userTeam = new UserTeam();
            userTeam.setTeam(teamService.findBytID(tId));
            userTeam.setUser(userService.find(loginUser.getUID()));
            userTeamService.save(userTeam);
            return true;
        }
        return false;
    }

    @ResponseBody
    @GetMapping("/findUser")
    public UserVO findUser(@Login User loginUser){
        UserVO uservo = new UserVO();
        uservo.setUserName(loginUser.getUserName());
        uservo.setPoint(loginUser.getPoint());
        return uservo;
    }
}
