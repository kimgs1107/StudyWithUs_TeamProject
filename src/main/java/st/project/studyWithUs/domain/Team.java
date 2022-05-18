package st.project.studyWithUs.domain;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tID;

    @NotNull
    private String teamName;

    @NotNull
    private Long depositPoint;

    private Long totalDepositPoint;

    @NotNull
    private Long targetTime;

    @NotNull
    private Integer headCount;

    @NotNull
    private Integer currentCount;

    @NotNull
    private String teamDesc;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private String teamImage;

    @NotNull
    private String scope;

    private String password;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<UserTeam> userTeams = new ArrayList<>();

}
