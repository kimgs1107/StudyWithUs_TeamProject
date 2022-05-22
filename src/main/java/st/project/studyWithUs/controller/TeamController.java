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
import st.project.studyWithUs.repository.ChatRoomRepository;
import st.project.studyWithUs.domain.Team;
import st.project.studyWithUs.domain.User;
import st.project.studyWithUs.domain.UserTeam;
import st.project.studyWithUs.form.TeamForm;
import st.project.studyWithUs.service.teamService.TeamService;
import st.project.studyWithUs.service.userTeamService.UserTeamService;
import st.project.studyWithUs.vo.TeamVO;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final UserTeamService userTeamService;
    private final ChatRoomRepository chatRoomRepository;

    @ResponseBody
    @GetMapping("/findAllTeams")
    public List<TeamVO> teams (@RequestParam int check, @RequestParam String teamName, @Login User loginUser){
        // 검색해서 나오는 스터디 리스트
        if(check==1) {
            return teamService.searchTeam(teamName, loginUser);
        }
        // 비로그인/로그인에서 볼 수 있는 모든 스터디 리스트
        else {
            return teamService.findAllTeams(loginUser);
        }
    }

    @GetMapping("/createStudy")
    public String createStudy(){
        return "createStudy";
    }

    @PostMapping("/upload")
    public String saveFile(@RequestParam("tName") String teamName, @RequestParam MultipartFile file, Model model
    ) throws IOException {

        if(!file.isEmpty()){
            // ========================== 파일 업로드 ========================== //
            // 업로드된 파일의 파일명을 변경 ( 중복 파일명이 될 수 있으므로, 중복되지 않을 문자로 변경해준다. )
            String fileName = renameFiles(file);
            // 실제 업로드 될 파일의 경로를 지정해준다.
            String fullPath = new File("").getAbsolutePath()+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"static"+File.separator+"teamImage"+File.separator+ fileName;
            // 해당 경로에 파일을 업로드 한다.
            file.transferTo(new File(fullPath));

            Team team = teamService.findByTeamName(teamName);
            // DB에 올릴 경로를 짧게 잡아준다. ( static/example.jpg )
            fullPath = File.separator + "teamImage" + File.separator + fileName;

            // 팀 테이블에 이미지 경로 저장
            team.setTeamImage(fullPath);
            // 팀 테이블 업데이트
            teamService.saveTeam(team);
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

    // 기능 _ 스터디명 중복 여부
    @ResponseBody
    @PostMapping(value = "/validateStudy")
    public int validateTeam(@RequestParam("sendData") String teamName){
        if(teamName==""){
            return -1;
        }else {
            Team team = teamService.findByTeamName(teamName);
            if (team == null) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    @ResponseBody
    @PostMapping(value = "/createStudy")
    public void  CreateTeamForm(TeamForm studyForm, @Login User loginUser) {

        // 전달받은 데이터를 Team 테이블에 저장
        Team team = new Team();
        team.setTeamName(studyForm.getTeamName());
        team.setTeamDesc(studyForm.getTeamDesc());
        team.setStartDate(LocalDate.parse(studyForm.getStartDate()));
        team.setEndDate(LocalDate.parse(studyForm.getEndDate()));
        team.setHeadCount(studyForm.getHeadCount());
        team.setCurrentCount(1);
        team.setDepositPoint((long)studyForm.getDepositPoint());
        team.setTargetTime((long)studyForm.getTargetTime()*60); // 분으로 저장
        team.setScope(studyForm.getScope());
        if(studyForm.getScope().equals("private")){
            team.setPassword(studyForm.getPassword());
        }


        teamService.saveTeam(team);

        UserTeam userTeam = new UserTeam();
        userTeam.setTeam(team);
        userTeam.setUser(loginUser);
        userTeam.setExist(false);
        userTeam.setTotalTime(0l); // 초 _ 스터디 끝날때까지 저장될 값
        userTeam.setRealTime(0l); // 초 _ 매일 자정 리셋
        userTeamService.save(userTeam);

        chatRoomRepository.add(team.getTID(), team.getTeamName());
    }

    @ResponseBody
    @PostMapping("/checkTeamPW")
    public boolean checkTeamPassword(@RequestParam("tId") String tID, @RequestParam("password") String password) {
        return teamService.checkTeamPassword(Long.parseLong(tID), password);
    }
}
