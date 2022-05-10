package st.project.studyWithUs.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class BoardVO {

    private Long bbID;  // bID로 할 경우 데이터 안넘어옴.
    private String content;
    private String title;
    private LocalDate uploadTime;
    private Long uID;
    private String name;

}

