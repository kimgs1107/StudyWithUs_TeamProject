package st.project.studyWithUs.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Setter
@Getter
public class AccountVO {

    private Long rrID;
    private String userName;
    private String bankName;
    private String account;
    private Long point;
    private boolean flag;
    private LocalDate requestDate;


}
