package st.project.studyWithUs.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import st.project.studyWithUs.domain.RefundUserAccount;


@Repository
public interface RefundAccountRepository extends JpaRepository<RefundUserAccount, Long> {



    RefundUserAccount findByrID (Long rID);

}
