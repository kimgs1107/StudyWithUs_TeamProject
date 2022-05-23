package st.project.studyWithUs.service.userService;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import st.project.studyWithUs.domain.User;
import st.project.studyWithUs.repository.UserRepository;
import st.project.studyWithUs.vo.UserVO;
import org.springframework.mail.javamail.JavaMailSender;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableAsync
public class UserServiceImpl implements UserService {

    private final JavaMailSender javaMailSender;

    private final UserRepository userRepository;
    @Override
    public User find(Long uid) {
        return userRepository.findByuID(uid);
    }

    @Override
    public void updatePoint(Long point, Long uId) {
        Long p = find(uId).getPoint();
        p+=point;
        userRepository.updatePoint(p, uId);
    }

    @Override
    public boolean checkPoint(Long point, Long uId) {
        if(point <= find(uId).getPoint()){
            updatePoint(-point, uId);
            return true;
        }
        else return false;
    }

    @Override
    public User login(String id, String pw) {
       return userRepository.loginCheck(id, pw);
    }

    @Override
    public User getNameEmail(String nickname, String email) {
        return userRepository.kakaoLoginCheck(nickname, email);

    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteUser(Long uID) {
        userRepository.deleteById(uID);
    }


    //user ID로 회원 찾기
    @Override
    public List<UserVO> searchUserInfo(String userID) {
        List<User> u = userRepository.searchUserInfo(userID);
        List<UserVO> user = new ArrayList<>();
        UserVO uvo = new UserVO();
        uvo.setUuID(u.get(0).getUID());
        uvo.setUserID(u.get(0).getUserID());
        uvo.setUserName(u.get(0).getUserName());
        uvo.setPw(u.get(0).getPassword());
        uvo.setPoint(u.get(0).getPoint());
        user.add(uvo);
        return user;
    }

    @Override
    public void saveEditUser(Long uid, String name, String id, String pw, String email) {
        User user = userRepository.findByuID(uid);
        user.setUserID(id);
        user.setPassword(pw);
        user.setUserName(name);
        user.setEmail(email);
        userRepository.save(user);
    }

    @Override
    public User findByuID(Long uid) { return userRepository.findByuID(uid); }

    @Override
    public void save(User user){
        userRepository.save(user);
    }

    @Override
    public List<User> findByUserID(String userID){ return userRepository.searchUserInfo(userID); }

    @Override
    public List<User> findByUserEmail(String email){
        return userRepository.findByUserEmail(email);
    }

    @Override
    public User findByNameAndEmail(String name,String email){ return userRepository.findByNameAndEmail(name,email); }

    @Override
    public String getTempPW(){
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        String str = "";

        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }

    @Override
    @Async
    public void mailToPW(String name,String email, String tempPW){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);                              //수신자
        message.setFrom("StudyWithUs@noreply.com");        //발신자.
        message.setSubject("[Study With Us] "+name+"님 임시비밀번호 발급 메일입니다.");       //메일 제목
        String sendMsg = name+"님의 임시 비밀번호 생성 안내를 위해 발송된 메일입니다.\n 임시 비밀번호는 "+tempPW + "입니다.";
        message.setText(sendMsg);                   //메일 내용
        javaMailSender.send(message);
    }

    @Transactional
    @Override
    public void addPoint(Long uid, Long addUserPoint) {
        User u = userRepository.findByuID(uid);
        u.setPoint(u.getPoint() + addUserPoint);
        userRepository.save(u);
    }
}
