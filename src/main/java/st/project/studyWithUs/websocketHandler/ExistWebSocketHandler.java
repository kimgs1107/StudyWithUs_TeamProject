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
import st.project.studyWithUs.service.userService.UserService;
import st.project.studyWithUs.service.userTeamService.UserTeamService;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ExistWebSocketHandler extends TextWebSocketHandler {

    private final UserTeamService userTeamService;
    private final UserService userService;

//    List<WebSocketSession> sessions = new ArrayList<>(); //전체 유저 세션
    Map<Long, List<WebSocketSession>> teamSessionList = new HashMap<>(); //uID별로 세션 -> 나중에 tID별로 바꾸면 팀별로 세션 보낼 수 있을 거 같음
    Map<WebSocketSession, Long> teamSession = new HashMap<>();
    Map<WebSocketSession, Long> userSession = new HashMap<>();

    public void noticeExist(UserTeam userTeam) throws Exception{

        List<WebSocketSession> sess = teamSessionList.get(userTeam.getTeam().getTID());

        TextMessage message = null;
        String userImage = userTeam.getUser().getUserImage();
        if(userImage == null){
            userImage = "noImage";
        }
        if(userTeam.getExist()) {
            message = new TextMessage("ON "+userTeam.getUser().getUID()+" "+userImage+" "+userTeam.getUser().getUserName());
        }
        else{
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

        List<WebSocketSession> sess = teamSessionList.get(tID);

        TextMessage message = new TextMessage("leave "+uID);
        if(sess != null) {
            for (WebSocketSession s : sess) {
                try {
                    synchronized (s) {
                        s.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
        String[] ids = message.getPayload().split(" ");
        List<WebSocketSession> sessionList = teamSessionList.get(Long.parseLong(ids[1]));
        if(sessionList == null){
            sessionList = new ArrayList<>();
        }
        sessionList.add(session);
        teamSessionList.put(Long.parseLong(ids[1]), sessionList);
        teamSession.put(session, Long.parseLong(ids[1]));
        userSession.put(session, Long.parseLong(ids[0]));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
        Long tID = teamSession.get(session);
        Long uID = userSession.get(session);

        userTeamService.updateExistFalse(uID, tID);
        User user = userService.findByuID(uID);

        String userImage = user.getUserImage();
        if(userImage == null){
            userImage = "noImage";
        }
        TextMessage message = new TextMessage("OFF "+uID+" "+userImage+" "+user.getUserName());

        List<WebSocketSession> sessionList = teamSessionList.get(tID);
        for(WebSocketSession s : sessionList) {
            if(s.getId() != session.getId()) {
                try {
                    synchronized (s) {
                        s.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for(WebSocketSession s : sessionList){
            if(s.getId() == session.getId()){
                sessionList.remove(s);
                teamSessionList.put(tID, sessionList);
                break;
            }
        }
        teamSession.remove(session);
    }
}
