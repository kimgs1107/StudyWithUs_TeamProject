package st.project.studyWithUs.vo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CommentVO {

    private Long cmID;
    private String writerName;
    private String commentContent;
    private Long bID;
}
