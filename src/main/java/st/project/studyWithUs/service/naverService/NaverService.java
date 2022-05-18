package st.project.studyWithUs.service.naverService;

import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public interface NaverService {

    String getNaverToken(String code);
    HashMap<String, Object> getUserInfo (String access_Token);

    String getKey();
    String receiveImg(String key);

    String resCap(String key, String value);

}
