package st.project.studyWithUs.vo;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TeamVO {

    private Long tID;
    private Long ttID;
    private String teamName;
    private Long depositPoint;
    private Long totalDepositPoint;
    private Long targetTime;
    private Integer headCount;
    private Integer currentCount;
    private String teamDesc;
    private LocalDate startDate;
    private LocalDate endDate;
    private String teamImage;

}
