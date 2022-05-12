package st.project.studyWithUs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import st.project.studyWithUs.argumentresolver.Login;
import st.project.studyWithUs.domain.User;
import st.project.studyWithUs.domain.UserTeam;
import st.project.studyWithUs.service.studyingService.StudyingService;
import st.project.studyWithUs.service.userTeamService.UserTeamService;
import st.project.studyWithUs.vo.MemberInSameVO;
import st.project.studyWithUs.websocketHandler.ExistWebSocketHandler;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class StudyingController {

    private final StudyingService studyingService;
    private final UserTeamService userTeamService;
    private final ExistWebSocketHandler handler;

    @GetMapping("/studying/{tID}")
    public String studying(){
        return "studying";
    }

    @PostMapping("/updateUserTeam")
    @ResponseBody
    public void updateUserTeam(@RequestParam("data") boolean data, @RequestParam("realTime") String realTime, @RequestParam("totalTime") String totalTime, @RequestParam("tID") String tID, @Login User user) throws Exception{

        System.out.println("!!!!!updateUserTeam 함수");
        System.out.println(user.getUID());
        System.out.println(tID);
        UserTeam ut = studyingService.findUserTeam(user.getUID(), Long.parseLong(tID));
        ut.setExist(data);
        ut.setRealTime(Long.parseLong(realTime)/100);
        ut.setTotalTime(Long.parseLong(totalTime)/100);
        studyingService.save(ut);
        handler.noticeExist(ut, Long.parseLong(tID));
    }

    @PostMapping("/getTotalTime")
    @ResponseBody
    public int getTotalTime(@Login User user, @RequestParam("tID") String tID) {

        UserTeam ut = studyingService.findUserTeam(user.getUID(),Long.parseLong(tID));
        if(ut.getTotalTime()==null){
            return 0;
        }else {
            return ut.getTotalTime().intValue();
        }
    }

    @PostMapping("/getRealTime")
    @ResponseBody
    public int getRealTime(@Login User user, @RequestParam("tID") String tID) {

        UserTeam ut = studyingService.findUserTeam(user.getUID(),Long.parseLong(tID));
        if(ut.getRealTime()==null){
            return 0;
        }else{
            return ut.getRealTime().intValue();
        }
    }

    @PostMapping("/members")
    @ResponseBody
    public List<MemberInSameVO> members(@RequestParam("tID") String tID){
        List<UserTeam> userTeams = studyingService.findUserTeamByTID(Long.parseLong(tID));
        List<MemberInSameVO> members = new ArrayList<>();

        for(UserTeam ut : userTeams){
            MemberInSameVO mem = new MemberInSameVO();
            mem.setUuID(ut.getUser().getUID());
            mem.setUserImage(ut.getUser().getUserImage());
            mem.setUserName(ut.getUser().getUserName());
            members.add(mem);
        }

        return members;
    }
    @PostMapping("/checkExist")
    @ResponseBody
    public boolean checkExist(@RequestParam("tID") String tID,@Login User loginUser) {
        UserTeam ut = userTeamService.findByUIDAndTID(loginUser.getUID(), Long.parseLong(tID));
        if (ut != null) {
            return ut.getExist();
        }else{
            return true;
    }
}


//    @PostMapping("/getLoginUser")
//    @ResponseBody
//    public Long getLoginUser(@Login User loginUser){
//        Long uID = loginUser.getUID();
//        return uID;
//    }
}
