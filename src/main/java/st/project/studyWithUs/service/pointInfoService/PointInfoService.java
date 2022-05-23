package st.project.studyWithUs.service.pointInfoService;

import st.project.studyWithUs.domain.PointInfo;
import st.project.studyWithUs.domain.RefundUserAccount;

public interface PointInfoService {

    void deposit( Long point, Long pId);
    PointInfo find(Long aId);
    void addRefundUserAccount(RefundUserAccount refundUserAccount);
    void depositTest(Long pId, Long point);
    void withDrawTest(Long pId, Long point);

    void changePoint(Long point);

    void addPoint(Long point);
}
