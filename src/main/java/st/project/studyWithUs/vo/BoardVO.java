package st.project.studyWithUs.vo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BoardVO {

    private Long bbID;  // bID로 할 경우 데이터 안넘어옴. 게시글 번호 아이디
    private String content;
    private String title;
    private String uploadTime;
    private Long uID;
    private String name;
    private int idx;

}

