package st.project.studyWithUs.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import st.project.studyWithUs.argumentresolver.Login;
import st.project.studyWithUs.domain.User;
import st.project.studyWithUs.domain.UserTeam;
import st.project.studyWithUs.interceptor.SessionConst;
import st.project.studyWithUs.service.pointInfoService.PointInfoService;
import st.project.studyWithUs.service.teamService.TeamService;
import st.project.studyWithUs.service.userService.UserService;
import st.project.studyWithUs.service.userTeamService.UserTeamService;
import st.project.studyWithUs.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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
    public String myPage(Model model, @Login User loginUser,HttpServletRequest request){
        User user = userService.find(loginUser.getUID());
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_USER, user);
        model.addAttribute("loginUser", user);
        return "myPage";
    }

    @GetMapping("/chargePoint")
    public String chargePoint(){
        return "chargePoint";
    }


    @ResponseBody
    @GetMapping("/deposit")
    public void deposit(Long point, @Login User loginUser){
        userService.updatePoint(point, loginUser.getUID());
        pointInfoService.deposit(point, 1L );
    }

    @GetMapping("/editUser")
    public String editUser(Model model, @Login User loginUser){
        model.addAttribute("userProfileImage",loginUser.getUserImage());
        return "editUser";
    }

    @ResponseBody
    @PostMapping("/saveEditUser")
    public boolean saveEditUser(@RequestParam String name,
                                @RequestParam String id,
                                @RequestParam String pw,
                                @RequestParam String email,
                                @Login User loginUser,
                                HttpServletRequest request){


        userService.saveEditUser(loginUser.getUID(), name, id, pw, email);
        loginUser = userService.findByuID(loginUser.getUID());

        HttpSession session = request.getSession();
        // 세션에 LOGIN_USER라는 이름(SessionConst.class에 LOGIN_USER값을 "loginUser")을 가진 상자에 loginUser 객체를 담음.
        // 즉, 로그인 회원 정보를 세션에 담아놓는다.
        session.setAttribute(SessionConst.LOGIN_USER, loginUser);
        return true;

    }
    @PostMapping("/uploadUserImage")
    public String saveFile(@Login User loginUser, @RequestParam MultipartFile file, Model model
    ) throws IOException {
        if(!file.isEmpty()){
            // ========================== 파일 업로드 ========================== //
            // 업로드된 파일의 파일명을 변경 ( 중복 파일명이 될 수 있으므로, 중복되지 않을 문자로 변경해준다. )
            String fileName = renameFiles(file);
            // 실제 업로드 될 파일의 경로를 지정해준다.
            String fullPath = new File("").getAbsolutePath()+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"static"+File.separator+"userImage"+File.separator+ fileName;
            // 해당 경로에 파일을 업로드 한다.
            file.transferTo(new File(fullPath));

            User userInfo = userService.find(loginUser.getUID());
            // DB에 올릴 경로를 짧게 잡아준다. ( static/example.jpg )
            fullPath = File.separator + "userImage" + File.separator + fileName;

            // 팀 테이블에 이미지 경로 저장
            userInfo.setUserImage(fullPath);
            // 팀 테이블 업데이트
            userService.save(userInfo);
            model.addAttribute("img",fullPath);
        }

        return "home";
    }
    // 기능 _ 파일 업로드시 파일명 재정의하는 메서드 구현
    public String renameFiles(MultipartFile multipartFile) throws IOException {
        // 업로드한 파일명
        String originalFilename = multipartFile.getOriginalFilename();

        // ============================= 파일명 지정 ===================================
        // 원본 파일의 확장자 분리
        int pos = originalFilename.lastIndexOf(".");
        String ext = originalFilename.substring(pos+1);
        // 서버에 저장할 중복되지 않을 파일명을 지정한다. (나노세컨즈 사용)
        String nanoSec = Integer.toString(LocalDateTime.now().getNano());
        // 나노세컨즈파일명.확장자로 파일명 완성
        String storeFilename = nanoSec + "." + ext;

        return storeFilename;
    }

    @ResponseBody
    @GetMapping("/getUserInfo")
    public UserVO getUserInfo(@Login User loginUser){
        UserVO uservo = new UserVO();
        uservo.setUserID(loginUser.getUserID());
        uservo.setUserName(loginUser.getUserName());
        uservo.setPw(loginUser.getPassword());
        uservo.setEmail(loginUser.getEmail());
        uservo.setUserImage(loginUser.getUserImage());
        uservo.setRole(loginUser.getRole());
        return uservo;
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
            userTeam.setExist(false);
            userTeam.setRealTime(0L);
            userTeam.setTotalTime(0L);
            userTeamService.save(userTeam);
            return true;
        }
        return false;
    }

    @ResponseBody
    @GetMapping("/findUser")
    public UserVO findUser(@Login User loginUser){
        User user = userService.find(loginUser.getUID());
        UserVO uservo = new UserVO();
        uservo.setUserName(user.getUserName());
        uservo.setPoint(user.getPoint());
        return uservo;
    }

    @ResponseBody
    @PostMapping("/getPgInfo")
    public List<String> getPgInfo(@Login User loginUser){

        List <String> data = new ArrayList<>();
        data.add(loginUser.getUserName());
        data.add(loginUser.getEmail());
        data.add(loginUser.getPhoneNumber());
        return data;


    }


}
