package st.project.studyWithUs.service.userService;
import org.springframework.stereotype.Service;
import st.project.studyWithUs.domain.User;
import st.project.studyWithUs.vo.UserVO;

import java.util.List;


@Service
public interface UserService {

    User findByNameAndEmail(String name,String email);

    void save(User user);

    List<User> findByUserEmail(String email);

    List<User> findByUserID(String userID);

    User find(Long uid); //유저 찾기

    void updatePoint(Long point, Long uId); //포인트 업데이트

    boolean checkPoint(Long point, Long uId); // 포인트 삭감 여부

    User login(String id, String pw);

    User getNameEmail(String nickname, String email);

    List<User> findAll();

    void deleteUser(Long uID);

    List<UserVO> searchUserInfo(String userID);

    void saveEditUser(Long uid, String photo, String name, String id, String pw, String email);

    User findByuID(Long uid);

    String getTempPW();

    void mailToPW(String name, String email, String tempPW);

}
