package st.project.studyWithUs.vo;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import st.project.studyWithUs.domain.User;

import javax.persistence.*;


@Setter
@Getter
public class AccountVO {

    private Long rrID;
    private String userName;
    private String bankName;
    private String account;
    private Long point;
    private boolean flag;


}
