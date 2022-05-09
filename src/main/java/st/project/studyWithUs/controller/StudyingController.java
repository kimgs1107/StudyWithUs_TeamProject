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


@Controller
@RequiredArgsConstructor
public class StudyingController {

    private final StudyingService studyingService;

    @GetMapping("/studying")
    public String studying(){
        return "studying";
    }

    @PostMapping("/updateUserTeam")
    @ResponseBody
    public void updateUserTeam(@RequestParam("data") boolean data, @RequestParam("realTime") int realTime, @RequestParam("totalTime") int totalTime, @Login User user) {

        Long tID = 1l;

        UserTeam ut = studyingService.findUserTeam(user.getUID(), tID);
        ut.setExist(data);
        ut.setRealTime(((long)realTime)/100);
        ut.setTotalTime(((long)totalTime)/100);
        studyingService.save(ut);
    }

    @PostMapping("/getTotalTime")
    @ResponseBody
    public int getTotalTime(@Login User user) {

        Long tID = 1l;

        UserTeam ut = studyingService.findUserTeam(user.getUID(),tID);
        return ut.getTotalTime().intValue();
    }

    @PostMapping("/getRealTime")
    @ResponseBody
    public int getRealTime(@Login User user) {

        Long tID = 1l;

        UserTeam ut = studyingService.findUserTeam(user.getUID(),tID);
        return ut.getRealTime().intValue();
    }



}
