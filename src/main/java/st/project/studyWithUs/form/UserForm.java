package st.project.studyWithUs.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class UserForm {

    @NotEmpty(message = "아이디는 필수 입니다")
    private String id;

    @NotEmpty(message = "비밀번호는 필수 입니다")
    private String passWord;

    @NotEmpty(message = "이름은 필수 입니다")
    private String name;

    @NotEmpty(message = "E-mail은 필수 입니다")
    private String email;

    @NotEmpty(message = "전화번호는 필수 입니다")
    private String phone;
}