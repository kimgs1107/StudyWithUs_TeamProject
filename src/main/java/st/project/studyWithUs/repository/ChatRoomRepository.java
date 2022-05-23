package st.project.studyWithUs.repository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;
import st.project.studyWithUs.model.ChatRoom;

import java.util.Collection;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {

    private final Map<String, ChatRoom> chatRoomMap;

    private final Collection<ChatRoom> chatRooms;

    public void add(Long tID, String name){

        log.info("hih {} {}", tID, name);
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(Long.toString(tID));
        chatRoom.setName(name);
        chatRoomMap.put(Long.toString(tID), chatRoom);
        log.info("{}, {} {}", chatRoom.getName(), chatRoom.getId(), chatRoom.getSessions());

        chatRooms.add(chatRoom);

    }


    public ChatRoom getChatRoom(String id) {
        return chatRoomMap.get(id);
    }

    public void remove(WebSocketSession session) {
        this.chatRooms.parallelStream().forEach(chatRoom -> chatRoom.remove(session));
    }
}
