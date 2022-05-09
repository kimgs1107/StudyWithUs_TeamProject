package st.project.studyWithUs.domain;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class RefundUserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rID;

    @NotNull
    private String userName;

    @NotNull
    private String account;

    @NotNull
    private String bankName;

    @NotNull
    private Long point;

    @NotNull
    private boolean flag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="uID")
    private User user;




}
