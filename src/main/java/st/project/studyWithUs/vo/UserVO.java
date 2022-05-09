package st.project.studyWithUs.vo;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class UserVO {

    private Long uuID;
    private String userID;
    private String pw;
    private String userName;
    private String email;
    private String role;
    private String userImage;
    private Long point;

}
