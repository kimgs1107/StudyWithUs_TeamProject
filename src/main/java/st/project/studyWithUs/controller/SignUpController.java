package st.project.studyWithUs.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import st.project.studyWithUs.domain.User;
import st.project.studyWithUs.form.UserForm;
import st.project.studyWithUs.service.userService.UserService;

import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SignUpController {

    private final UserService userService;

    @GetMapping("/signUp")
    public String createForm() {
        return "joinUser";
    }

    @PostMapping("/signUp")
    public String create(@Valid @ModelAttribute UserForm form, BindingResult result) {
        // @Valid 는 BindingResult result 검증작업에 사용하는 애노테이션이다.
        // @ModelAttribute는 html에서 받아온 데이터 값들을 UserForm form에 고대로 넣어준다.
        if (result.hasErrors()) {
            //result에 에러가 있다면 다시 createUserForm.html로 보낸다. 이때 form에서 message 문장들을 출력해준다.
            return "joinUser";
        }

        // 에러가 없다면 아래와 같이 도메인의 User에 폼에서 받아온 데이터들을 주입한다.
        User user = new User();
        user.setUserID(form.getId());
        user.setPassword(form.getPassWord());
        user.setUserName(form.getName());
        user.setEmail(form.getEmail());
        user.setRole("custom");
        user.setPoint(0l);

        userService.save(user);

        return "redirect:/";
    }

    @ResponseBody
    @PostMapping(value = "/validateMem")
    public int validateMem(@RequestParam("sendData") String id){
        if(id==""){
            return -1;
        }else{
            return userService.findByUserID(id).size();
        }
    }

    @ResponseBody
    @PostMapping(value = "/validateEmail")
    public int validateEmail(@RequestParam("sendData") String email){
        if(email==""){
            return -1;
        }else{
            return userService.findByUserEmail(email).size();
        }
    }

}
