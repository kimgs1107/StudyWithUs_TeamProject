package st.project.studyWithUs.service.pointInfoService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import st.project.studyWithUs.domain.PointInfo;
import st.project.studyWithUs.repository.PointInfoRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
public class PointInfoServiceTest {


    @Autowired
    PointInfoService pointInfoService;
    @Autowired
    PointInfoRepository pointInfoRepository;

    private static final ExecutorService service =  Executors.newFixedThreadPool(100);

    //동시성 테스트 ( 입금만 )
    //도메인에서 version 컬럼 없애주고, 리파지토리에서 @Lock 없애면 동시성 이슈 생김
    @Test
    public void checkDeposit() throws InterruptedException {
        PointInfo pointInfo  = new PointInfo(100L);

        pointInfo = pointInfoRepository.save(pointInfo);
        Long aId = pointInfo.getPID();
        CountDownLatch latch = new CountDownLatch(100);
        for (int i=0; i < 100; i++) {
            service.execute(() -> {
                pointInfoService.depositTest(aId, 1L);
                latch.countDown();
            });
        }
        latch.await();
        PointInfo pointInfo1 = pointInfoRepository.findById(aId).orElseThrow();
        assertThat(pointInfo1.getBalance()).isEqualTo(200L);
    }



    @Test  //동시성 테스트 ( 입금, 환불 적용)
    public void checkDepositWithdraw() throws InterruptedException {
        PointInfo administrator = new PointInfo(100L);
        administrator = pointInfoRepository.save(administrator);
        Long aId = administrator.getPID();
        CountDownLatch latch = new CountDownLatch(100);
        for (int i=0; i < 100; i++) {
            service.execute(() -> {
                pointInfoService.depositTest(aId, 1L);
                pointInfoService.withDrawTest(aId, 1L);
                latch.countDown();
            });
        }
        latch.await();
        PointInfo administrator1 = pointInfoRepository.findById(aId).orElseThrow();
        assertThat(administrator1.getBalance()).isEqualTo(100L);
    }

}