package st.project.studyWithUs.domain;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class UserTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long utID;

    @NotNull
    private Boolean exist;

    private Long realTime;

    private Long totalTime;

    @ManyToOne(fetch = FetchType.EAGER )
    @JoinColumn(name="uID")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER )
    @JoinColumn(name="tID")
    private Team team;

}
