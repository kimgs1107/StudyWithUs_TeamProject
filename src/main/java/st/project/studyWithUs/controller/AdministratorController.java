package st.project.studyWithUs.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import st.project.studyWithUs.argumentresolver.Login;
import st.project.studyWithUs.domain.PointInfo;
import st.project.studyWithUs.domain.RefundUserAccount;
import st.project.studyWithUs.domain.Team;
import st.project.studyWithUs.domain.User;
import st.project.studyWithUs.service.pointInfo.PointInfoService;
import st.project.studyWithUs.service.refundUserAccountService.RefundUserAccountService;
import st.project.studyWithUs.service.teamService.TeamService;
import st.project.studyWithUs.service.userService.UserService;
import st.project.studyWithUs.vo.AccountVO;
import st.project.studyWithUs.vo.TeamVO;
import st.project.studyWithUs.vo.UserVO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdministratorController {

    private final PointInfoService pointInfoService;
    private final RefundUserAccountService refundUserAccountService;
    private final UserService userService;
    private final TeamService teamService;

    @GetMapping("/adminPage")
    public String myPage(){
        return "adminPage";
    }

    @GetMapping("/userList")
    public String userList(){
        return "userList";
    }

    @ResponseBody
    @GetMapping("/userListInfo")
    public List<UserVO> userListInfo(@RequestParam int check, @RequestParam String userID){
        if(check==1){
            log.info("userID : {}", userID) ;
            return userService.searchUserInfo(userID);
        }

        List<User> users = userService.findAll();
        List<UserVO> userVOS = new ArrayList<>();
        for(User u : users){
            if(u.getRole().equals("admin"))continue;
            UserVO user = new UserVO();
            user.setUuID(u.getUID());
            user.setUserID(u.getUserID());
            user.setUserName(u.getUserName());
            user.setPw(u.getPassword());
            user.setPoint(u.getPoint());
            userVOS.add(user);
        }
        return userVOS;
    }

    @ResponseBody
    @GetMapping("/teamListInfo")
    public List<TeamVO> teamListInfo(@RequestParam int check, @RequestParam String teamName){
        if(check==1){
            return teamService.findSearchTeam(teamName);
        }


        List<Team> teams = teamService.findAll();
        List<TeamVO> tvos = new ArrayList<>();
        for(Team t : teams){
            TeamVO teamVO = new TeamVO();
            teamVO.setTtID(t.getTID());
            teamVO.setTeamName(t.getTeamName());
            teamVO.setHeadCount(t.getHeadCount());
            teamVO.setCurrentCount(t.getCurrentCount());
            teamVO.setTotalDepositPoint(t.getTotalDepositPoint());
            tvos.add(teamVO);
        }
        return tvos;


    }

    @GetMapping("/teamList")
    public String teamList(){
        return "teamList";
    }

    @ResponseBody
    @GetMapping("/pointInfo")
    public Long pointInfo(){
        PointInfo pointInfo = pointInfoService.find(1L);
        return pointInfo.getBalance();
    }

    @ResponseBody
    @GetMapping("/accountRefunds")
    public List<AccountVO> accountRefunds (){

        List<RefundUserAccount> accounts = refundUserAccountService.findRefundAccount();
        List<AccountVO> accountVO = new ArrayList<>();
        for(RefundUserAccount rua : accounts){
            AccountVO a = new AccountVO();
            a.setRrID(rua.getRID());
            a.setUserName(rua.getUserName());
            a.setBankName(rua.getBankName());
            a.setAccount(rua.getAccount());
            a.setPoint(rua.getPoint());
            a.setFlag(rua.isFlag());
            accountVO.add(a);
        }
        return accountVO;
    }


    @ResponseBody
    @GetMapping("/changeFlag")
    public boolean accountRefunds (@RequestParam Long rID, @RequestParam Long point) {
        pointInfoService.changePoint(point);
        refundUserAccountService.changeFlag(rID);
        return true;
    }




    @GetMapping("/refunds")
    public String refunds(){
        return "refunds";
    }


    //환급 요청 페이지
    @PostMapping("/refunds")
    @ResponseBody
    public String refundsCheck(@RequestParam String name,
                               @RequestParam String account,
                               @RequestParam String bank,
                               @RequestParam Long point,
                               @Login User user){

        //계좌번호 존재하는지 일치여부

        //환급받은 포인트 만큼 자신의 포인트 삭감
        if(userService.checkPoint(point, user.getUID())==false){
            return "포인트가 부족합니다.";
        }
        //관리자 페이지에서 환급 리스트 모아놓기
        RefundUserAccount refundUserAccount = new RefundUserAccount();
        refundUserAccount.setBankName(bank);
        refundUserAccount.setPoint(point);
        refundUserAccount.setUserName(name);
        refundUserAccount.setAccount(account);
        refundUserAccount.setUser(user);
        refundUserAccount.setRequestDate(LocalDate.now());
        refundUserAccount.setFlag(false);
        pointInfoService.addRefundUserAccount(refundUserAccount);
        return "환급요청이 완료되었습니다.";
    }

    //회원 삭제 로직
    @ResponseBody
    @GetMapping("/deleteUser")
    public boolean deleteUser(@RequestParam Long uID){
        userService.deleteUser(uID);
        return true;
    }

    //팀 삭제 로직
    @ResponseBody
    @GetMapping("/deleteTeam")
    public boolean deleteTeam(@RequestParam Long tID){
        teamService.deleteTeam(tID);
        return true;
    }


    //환급 완료된 리스트 지우기
    @ResponseBody
    @GetMapping("/deleteCompleteAccounts")
    public boolean deleteCompleteAccounts(){
        return refundUserAccountService.deleteCompleteAccounts();
    }


    @PostMapping("/currentPoint")
    @ResponseBody
    public Long currentPoint(@Login User user){
        return userService.find(user.getUID()).getPoint();
    }

    @ResponseBody
    @PostMapping("/refundList")
    public  List<AccountVO> refundList(@Login User user){

        List<RefundUserAccount> list = refundUserAccountService.findAllByuID(user.getUID());
        List<AccountVO> voList = new ArrayList<>();
        for(RefundUserAccount li : list){
            AccountVO vo = new AccountVO();
            vo.setUserName(li.getUserName());
            vo.setBankName(li.getBankName());
            vo.setAccount(li.getAccount());
            vo.setRequestDate(li.getRequestDate());
            vo.setFlag(li.isFlag());
            vo.setPoint(li.getPoint());
            voList.add(vo);
        }
        return voList;
    }
}
