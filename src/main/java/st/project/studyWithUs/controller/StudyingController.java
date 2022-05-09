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
import st.project.studyWithUs.interceptor.SessionConst;
import st.project.studyWithUs.service.StudyingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Controller
@RequiredArgsConstructor
public class StudyingController {

    private final StudyingService studyingService;

    @GetMapping("/studying")
    public String studying(){
        return "studying";
    }

    @PostMapping("/updateExist")
    @ResponseBody
    public void updateExist(HttpServletRequest request,@RequestParam("data") boolean data, @RequestParam("realTime") int realTime, @RequestParam("totalTime") int totalTime, @Login User user) {

        Long tID = 1l;

        UserTeam ut = studyingService.findUserTeam(user.getUID(), tID);
        ut.setExist(data);
        ut.setRealTime(((long)realTime)/100);
        ut.setTotalTime(((long)totalTime)/100);
        studyingService.save(ut);
    }

    @PostMapping("/updateTotalTime")
    @ResponseBody
    public int updateTotalTime(@Login User user) {

        Long tID = 1l;

        UserTeam ut = studyingService.findUserTeam(user.getUID(),tID);
        return ut.getTotalTime().intValue();
    }

}
