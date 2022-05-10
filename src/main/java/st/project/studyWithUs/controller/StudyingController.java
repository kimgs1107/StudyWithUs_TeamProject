package st.project.studyWithUs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import st.project.studyWithUs.argumentresolver.Login;
import st.project.studyWithUs.domain.User;
import st.project.studyWithUs.domain.UserTeam;
import st.project.studyWithUs.service.studyingService.StudyingService;
import st.project.studyWithUs.websocketHandler.ExistWebSocketHandler;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class StudyingController {

    private final StudyingService studyingService;
    private final ExistWebSocketHandler handler;

    @GetMapping("/studying")
    public String studying(){
        return "studying";
    }

    @PostMapping("/updateUserTeam")
    @ResponseBody
    public void updateUserTeam(@RequestParam("data") boolean data, @RequestParam("realTime") String realTime, @RequestParam("totalTime") String totalTime, @Login User user) throws Exception{

        Long tID = 1l;

        UserTeam ut = studyingService.findUserTeam(user.getUID(), tID);
        ut.setExist(data);
        ut.setRealTime(Long.parseLong(realTime)/100);
        ut.setTotalTime(Long.parseLong(totalTime)/100);
        studyingService.save(ut);
        handler.noticeExist(ut);
    }

    @PostMapping("/getTotalTime")
    @ResponseBody
    public int getTotalTime(@Login User user) {

        Long tID = 1l;

        UserTeam ut = studyingService.findUserTeam(user.getUID(),tID);
        if(ut.getTotalTime()==null){
            return 0;
        }else {
            return ut.getTotalTime().intValue();
        }
    }

    @PostMapping("/getRealTime")
    @ResponseBody
    public int getRealTime(@Login User user) {

        Long tID = 1l;

        UserTeam ut = studyingService.findUserTeam(user.getUID(),tID);
        if(ut.getRealTime()==null){
            return 0;
        }else{
            return ut.getRealTime().intValue();
        }
    }

    @PostMapping("/members")
    @ResponseBody
    public List<Long> members(@RequestParam("tID") String tID){
        List<UserTeam> userTeams = studyingService.findUserTeamByTID(Long.parseLong(tID));
        List<Long> membersID = new ArrayList<>();

        for(UserTeam ut : userTeams){
            membersID.add(ut.getUser().getUID());
        }

        return membersID;
    }

    @PostMapping("/getLoginUser")
    @ResponseBody
    public Long getLoginUser(@Login User loginUser){
        Long uID = loginUser.getUID();
        return uID;
    }

}
