package st.project.studyWithUs.websocketHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import st.project.studyWithUs.domain.UserTeam;
import st.project.studyWithUs.service.studyingService.StudyingService;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ExistWebSocketHandler extends TextWebSocketHandler {

    private final StudyingService studyingService;

    List<WebSocketSession> sessions = new ArrayList<>(); //전체 유저 세션
    Map<Long, WebSocketSession> userSessionsMap = new HashMap<>(); //uID별로 세션 -> 나중에 tID별로 바꾸면 팀별로 세션 보낼 수 있을 거 같음

    public void noticeExist(UserTeam userTeam) throws Exception{
//        System.out.println("왜 안돼냐ㅜㅜㅜ"+userSessionsMap.size());
//        Set<Long> key = userSessionsMap.keySet();
//        for(Long uID : key){
//            System.out.println("In handler "+uID);
//            WebSocketSession sess = userSessionsMap.get(key);
//            System.out.println(userSessionsMap.get(key));

//            if(!userTeam.getExist()) {
//                TextMessage message = new TextMessage(userTeam.getUser().getUID() + "");
//                sess.sendMessage(message);
//            }
//        }

//        전체 유저에게 보내는 방법
        for(WebSocketSession sess : sessions){
            TextMessage message = null;
            if(!userTeam.getExist()) {
                message = new TextMessage(userTeam.getUser().getUID() + " off");
            }
            else{
                message = new TextMessage(userTeam.getUser().getUID() + " on");
            }
            sess.sendMessage(message);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
        System.out.println("!!!!!!!!curUID : "+message); //나중엔 tID도 받아야 함
        System.out.println("!!!!!session : "+session);
        userSessionsMap.put(Long.parseLong(message.getPayload()), session);
        System.out.println("get : "+Long.parseLong(message.getPayload())+" "+userSessionsMap.get(Long.parseLong(message.getPayload())));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
        sessions.add(session);
        System.out.println("소켓 연결!!");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    }
}
