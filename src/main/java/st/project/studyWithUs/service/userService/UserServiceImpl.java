package st.project.studyWithUs.service.userService;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import st.project.studyWithUs.domain.User;
import st.project.studyWithUs.repository.UserRepository;
import st.project.studyWithUs.vo.UserVO;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

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
}
