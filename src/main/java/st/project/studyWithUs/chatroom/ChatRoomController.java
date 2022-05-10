package st.project.studyWithUs.chatroom;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import st.project.studyWithUs.argumentresolver.Login;
import st.project.studyWithUs.chatroom.model.ChatRoom;
import st.project.studyWithUs.chatroom.repository.ChatRoomRepository;
import st.project.studyWithUs.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Controller
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomRepository repository;
    private final String listViewName;
    private final String detailViewName;
    private final AtomicInteger seq = new AtomicInteger(0);


    @GetMapping
    public String chatting() {
        return "redirect:/chat/rooms";
    }

    @Autowired
    public ChatRoomController(ChatRoomRepository repository, @Value("${viewname.chatroom.list}") String listViewName, @Value("${viewname.chatroom.detail}") String detailViewName) {
        this.repository = repository;
        this.listViewName = listViewName;
        this.detailViewName = detailViewName;
    }

  /*  @GetMapping("/rooms")
    public String rooms(Model model) {

        return listViewName;
    }
*/

    @ResponseBody
    @GetMapping("/rooms/{id}")
    public boolean room(@PathVariable String id, Model model) {

        return true;
    }


    @GetMapping("/room/enter/{id}")
    public String room2(@PathVariable String id, Model model) {
        log.info("id값 : {}", id);
        ChatRoom room = repository.getChatRoom(id);
        log.info("스터디방 id : {}, 스터디방 name : {}, 스터디방 session : {}", room.getId(), room.getName(), room.getSessions());

        model.addAttribute("room", room);
        model.addAttribute("member", "member" + seq.incrementAndGet());
        return detailViewName;
    }

    @GetMapping("/studyTitle")
    public String studyTitle(@RequestParam String tID){
        log.info("kajk {}", tID)    ;

        ChatRoom room = repository.getChatRoom(tID);
        return room.getName();

    }

    @ResponseBody
    @GetMapping("/findStudyUser")
    public String findStudyUser(@Login User user){
        return user.getUserName();
    }


    @ResponseBody
    @GetMapping("/findTitle")
    public String findStudyUser(@RequestParam String tID){
        ChatRoom room = repository.getChatRoom(tID);
        return room.getName();

    }
}
