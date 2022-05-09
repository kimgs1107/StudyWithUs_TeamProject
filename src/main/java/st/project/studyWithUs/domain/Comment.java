package st.project.studyWithUs.domain;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cmID;

    @NotNull
    private Long writerUID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cID")
    private Contents contents;
}

