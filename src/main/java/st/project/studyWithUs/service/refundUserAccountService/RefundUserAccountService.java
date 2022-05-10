package st.project.studyWithUs.service.refundUserAccountService;

import org.springframework.stereotype.Service;
import st.project.studyWithUs.domain.RefundUserAccount;

import java.util.List;

@Service
public interface RefundUserAccountService {


    List<RefundUserAccount> findRefundAccount();

    List<RefundUserAccount> findAllByuID(Long uID);

    void changeFlag(Long rID);

    boolean deleteCompleteAccounts();
}
