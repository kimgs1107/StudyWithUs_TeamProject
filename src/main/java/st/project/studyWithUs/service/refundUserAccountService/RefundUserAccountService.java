package st.project.studyWithUs.service.refundUserAccountService;

import org.springframework.stereotype.Service;
import st.project.studyWithUs.domain.RefundUserAccount;

import java.util.List;

@Service
public interface RefundUserAccountService {


    List<RefundUserAccount> findRefundAccount();


    void changeFlag(Long rID);

    boolean deleteCompleteAccounts();
}
