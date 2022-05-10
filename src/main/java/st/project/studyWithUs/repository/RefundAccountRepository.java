package st.project.studyWithUs.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import st.project.studyWithUs.domain.RefundUserAccount;

import java.util.List;


@Repository
public interface RefundAccountRepository extends JpaRepository<RefundUserAccount, Long> {


    @Query("select rf from RefundUserAccount rf where rf.user.uID=:uID")
    List<RefundUserAccount> findAllByuID(@Param("uID") Long uID);

    RefundUserAccount findByrID (Long rID);

}
