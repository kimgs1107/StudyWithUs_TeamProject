package st.project.studyWithUs.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import st.project.studyWithUs.domain.User;
import st.project.studyWithUs.service.userService.UserService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FindController {

    private final UserService userService;

    @GetMapping(value="/findID")
    public String findId() {
        return "findID";
    }

    @ResponseBody
    @PostMapping(value="/findID")
    public String findIdRes(@RequestParam("name") String name, @RequestParam("email") String email) {

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!" + userService.findByNameAndEmail(name, email));

        User user = userService.findByNameAndEmail(name, email);
        if( user == null){
            return "no";
        }else{
            return user.getUserID();
        }
    }

}