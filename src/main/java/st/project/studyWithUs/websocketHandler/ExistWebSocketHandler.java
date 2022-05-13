package st.project.studyWithUs.websocketHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import st.project.studyWithUs.argumentresolver.Login;
import st.project.studyWithUs.domain.User;
import st.project.studyWithUs.domain.UserTeam;
import st.project.studyWithUs.service.studyingService.StudyingService;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ExistWebSocketHandler extends TextWebSocketHandler {

    private final StudyingService studyingService;

//    List<WebSocketSession> sessions = new ArrayList<>(); //전체 유저 세션
    Map<Long, List<WebSocketSession>> teamSessionList = new HashMap<>(); //uID별로 세션 -> 나중에 tID별로 바꾸면 팀별로 세션 보낼 수 있을 거 같음
    Map<WebSocketSession, Long> teamSession = new HashMap<>();
    Map<WebSocketSession, Long> userSession = new HashMap<>();

    public void noticeExist(UserTeam userTeam) throws Exception{

        System.out.println("noticeExist tID "+userTeam.getTeam().getTID());
        List<WebSocketSession> sess = teamSessionList.get(userTeam.getTeam().getTID());
        System.out.println(teamSessionList.get(userTeam.getTeam().getTID()).size());

        TextMessage message = null;
        String userImage = userTeam.getUser().getUserImage();
        if(userImage == null){
            userImage = "noImage";
        }
        if(userTeam.getExist()) {
            System.out.println("있음!"+userTeam.getExist());
            message = new TextMessage("ON "+userTeam.getUser().getUID()+" "+userImage+" "+userTeam.getUser().getUserName());
        }
        else{
            System.out.println("없음!"+userTeam.getExist());
            message = new TextMessage("OFF "+userTeam.getUser().getUID()+" "+userImage+" "+userTeam.getUser().getUserName());
        }
        for(WebSocketSession s : sess) {
            try{
                synchronized (s) {
                    s.sendMessage(message);
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void noticeLeave(Long uID, Long tID) throws Exception{
        System.out.println("noticeLeave tID "+tID);
        List<WebSocketSession> sess = teamSessionList.get(tID);
        System.out.println(teamSessionList.get(tID).size());

        TextMessage message = new TextMessage("leave "+uID);

        for(WebSocketSession s : sess) {
            try{
                synchronized (s) {
                    s.sendMessage(message);
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
        System.out.println("!!!!!!!!curTID : "+message);
        System.out.println("!!!!!session : "+session.getId());

        List<WebSocketSession> sessionList = teamSessionList.get(Long.parseLong(message.getPayload()));
        if(sessionList == null){
            sessionList = new ArrayList<>();
        }
        sessionList.add(session);
        teamSessionList.put(Long.parseLong(message.getPayload()), sessionList);
        teamSession.put(session, Long.parseLong(message.getPayload()));

        System.out.println("get : "+Long.parseLong(message.getPayload())+" "+teamSessionList.get(Long.parseLong(message.getPayload())).size());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
//        sessions.add(session);
        System.out.println("소켓 연결!!"+session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
        Long tID = teamSession.get(session);
//        Long uID = userSession.get(session);
//        studyingService.updateExist(tID, uID);

        List<WebSocketSession> list = teamSessionList.get(tID);
        for(WebSocketSession s : list){
            if(s.getId() == session.getId()){
                list.remove(s);
                teamSessionList.put(tID, list);
                break;
            }
        }
        teamSession.remove(session);

        System.out.println("session 끊어짐 "+session.getId());
        System.out.println(status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    }
}
