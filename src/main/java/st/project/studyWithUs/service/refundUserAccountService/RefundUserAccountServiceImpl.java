package st.project.studyWithUs.service.refundUserAccountService;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import st.project.studyWithUs.domain.RefundUserAccount;
import st.project.studyWithUs.repository.RefundAccountRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class RefundUserAccountServiceImpl implements RefundUserAccountService {

    private final RefundAccountRepository refundAccountRepository;

    @Override
    public List<RefundUserAccount> findRefundAccount() {
        return refundAccountRepository.findAll();
    }


    public List<RefundUserAccount> findAllByuID(Long uID){ return refundAccountRepository.findAllByuID(uID);}


    @Transactional
    @Override
    public void changeFlag(Long rID) {
        RefundUserAccount rua = refundAccountRepository.findByrID(rID);
        rua.setFlag(true);
        refundAccountRepository.save(rua);
    }

    @Override
    @Transactional
    public boolean deleteCompleteAccounts() {
        List<RefundUserAccount> refundUserAccounts = refundAccountRepository.findAll();
        List<Long> deleteRefundAccounts = new ArrayList<>();
        for (RefundUserAccount r : refundUserAccounts) {
            if(r.isFlag()==true){
                deleteRefundAccounts.add(r.getRID());
            }
        }
        boolean check = false;
        for(Long Rid : deleteRefundAccounts){
            refundAccountRepository.deleteById(Rid);
            check = true;
        }
        if(check==false)return false;
        else return true;
    }
}
