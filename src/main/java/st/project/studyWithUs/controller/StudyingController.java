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
import java.util.HashMap;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class StudyingController {

    private final StudyingService studyingService;
    private final UserTeamService userTeamService;
    private final ExistWebSocketHandler handler;

    @PostMapping("/updateUserTeam")
    @ResponseBody
    public void updateUserTeam(@RequestBody HashMap<String, String> data, @Login User user) throws Exception{
        UserTeam ut = studyingService.findUserTeam(user.getUID(), Long.parseLong(data.get("tID")));
        ut.setExist(Boolean.valueOf(data.get("data")));
        ut.setRealTime(Long.parseLong(data.get("realTime"))/100);
        ut.setTotalTime(Long.parseLong(data.get("totalTime"))/100);

        studyingService.save(ut);
        handler.noticeExist(ut);
    }

    @PostMapping("/getTotalTime")
    @ResponseBody
    public int getTotalTime(@Login User user, @RequestBody HashMap<String, String> data) {
        UserTeam ut = studyingService.findUserTeam(user.getUID(), Long.parseLong(data.get("tID")));
        if(ut.getTotalTime()==null){
            return 0;
        }else {
            return ut.getTotalTime().intValue();
        }
    }

    @PostMapping("/getRealTime")
    @ResponseBody
    public int getRealTime(@Login User user, @RequestBody HashMap<String, String> data) {
        UserTeam ut = studyingService.findUserTeam(user.getUID(), Long.parseLong(data.get("tID")));

        if(ut.getRealTime()==null){
            return 0;
        }else{
            return ut.getRealTime().intValue();
        }
    }

    @PostMapping("/members")
    @ResponseBody
    public List<MemberInSameVO> members(@RequestBody HashMap<String, String> data){
        List<UserTeam> userTeams = studyingService.findUserTeamByTID(Long.parseLong(data.get("tID")));
        List<MemberInSameVO> members = new ArrayList<>();

        for(UserTeam ut : userTeams){
            MemberInSameVO mem = new MemberInSameVO();
            mem.setUuID(ut.getUser().getUID());
            mem.setUserImage(ut.getUser().getUserImage());
            mem.setUserName(ut.getUser().getUserName());
            if(ut.getExist() == true)
                mem.setExist("ON");
            else
                mem.setExist("OFF");
            members.add(mem);
        }

        return members;
    }
    @PostMapping("/checkExist")
    @ResponseBody
    public boolean checkExist(@Login User loginUser, @RequestBody HashMap<String, String> data) {
        UserTeam ut = userTeamService.findByUIDAndTID(loginUser.getUID(), Long.parseLong(data.get("tID")));
        if (ut != null) {
            return ut.getExist();
        }else{
            return true;
    }
}

    @PostMapping("/getLoginUser")
    @ResponseBody
    public Long getLoginUser(@Login User loginUser){
        Long uID = loginUser.getUID();
        return uID;
    }
}
