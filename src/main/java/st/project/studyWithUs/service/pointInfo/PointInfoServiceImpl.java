package st.project.studyWithUs.service.pointInfo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import st.project.studyWithUs.domain.PointInfo;
import st.project.studyWithUs.domain.RefundUserAccount;
import st.project.studyWithUs.repository.PointInfoRepository;
import st.project.studyWithUs.repository.RefundAccountRepository;

import javax.transaction.Transactional;



@Service
@RequiredArgsConstructor
public class PointInfoServiceImpl implements PointInfoService{


    private final PointInfoRepository pointInfoRepository;
    private final RefundAccountRepository refundAccountRepository;

    //포인트 입금
    @Transactional
    public void deposit( Long point, Long pId) {
        PointInfo pointInfo = pointInfoRepository.findBypID(pId);
        Long currBalance = pointInfo.getBalance();
        pointInfo.setBalance(currBalance + point);
        pointInfoRepository.update(currBalance+point, pId);
    }

    //포인트 관리
    public PointInfo find(Long aId) {
        return pointInfoRepository.getById(aId);
    }



    //환급 요청 추가
    @Transactional
    public void addRefundUserAccount(RefundUserAccount refundUserAccount){
        refundAccountRepository.save(refundUserAccount);
    }

    //포인 환급 후 blance 감소
    @Transactional
    @Override
    public void changePoint(Long point) {
        PointInfo pointInfo = pointInfoRepository.findBypID(1L);
        pointInfo.setBalance(pointInfo.getBalance()- point);
        pointInfoRepository.save(pointInfo);
    }




    //테스트용
    @Transactional
    public void depositError(Long pId, Long point) {
        PointInfo pointInfo = pointInfoRepository.findById(pId).orElseThrow();
        Long currBalance = pointInfo.getBalance();
        System.out.println("증가 thread = " + Thread.currentThread().getName() + ", " + "currBalance = " + currBalance);
        pointInfo.setBalance(currBalance + point);
        System.out.println("증가 thread = " + Thread.currentThread().getName() + ", " + "currBalance = " + (currBalance + point));
        pointInfoRepository.save(pointInfo);

    }



    //테스트용 코드
    @Transactional
    public void depositTest(Long pId, Long point) {
        PointInfo pointInfo = pointInfoRepository.findBypID(pId);
        Long currBalance = pointInfo.getBalance();
        System.out.println("증가 thread = " + Thread.currentThread().getName() + ", " + "currBalance = " + currBalance);
        pointInfo.setBalance(currBalance + point);
        System.out.println("증가 thread = " + Thread.currentThread().getName() + ", " + "currBalance = " + (currBalance + point));
        pointInfoRepository.save(pointInfo);
    }

    //테스트용
    @Transactional
    public void withDrawTest(Long pId, Long point) {
        PointInfo pointInfo = pointInfoRepository.findBypID(pId);
        Long currBalance = pointInfo.getBalance();
        System.out.println("감소 thread = " + Thread.currentThread().getName() + ", " + "currBalance = " + currBalance);
        if (currBalance - point < 0) {
            throw new IllegalArgumentException("잔액이 부족합니다");
        }
        pointInfo.setBalance(currBalance - point);
        System.out.println("감소 thread = " + Thread.currentThread().getName() + ", " + "currBalance = " + (currBalance - point));
        pointInfoRepository.save(pointInfo);
    }



}
