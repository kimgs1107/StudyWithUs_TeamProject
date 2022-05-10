package st.project.studyWithUs.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import st.project.studyWithUs.domain.User;
import st.project.studyWithUs.vo.UserVO;

import javax.transaction.Transactional;
import java.util.List;


@Transactional
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query ("select u from User u where u.userID = :id and u.password = :pw")
    User loginCheck(@Param("id") String id, @Param("pw") String pw);

    @Query ("select u from User u where u.userID like :nickName and u.email like :email")
    User kakaoLoginCheck(@Param("nickName") String nickname, @Param("email") String email);

    User findByuID(Long uId);

    @Modifying
    @Query ("update User u set u.point = :p where u.uID = :uid")
    void updatePoint(@Param("p") Long point, @Param("uid") Long uId);

    @Query ("select u from User u where u.userID = :userID")
    List<User> searchUserInfo(@Param("userID") String userID);

    @Query("select u from User u where u.email = :email")
    List<User> findByUserEmail(@Param("email") String email);

    @Query ("select u from User u where u.userName=:name and u.email=:email")
    User findByNameAndEmail(@Param("name") String name, @Param("email") String email);
}
