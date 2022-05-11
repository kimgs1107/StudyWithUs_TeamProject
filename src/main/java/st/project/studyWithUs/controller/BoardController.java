package st.project.studyWithUs.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import st.project.studyWithUs.argumentresolver.Login;
import st.project.studyWithUs.domain.Board;
import st.project.studyWithUs.domain.User;
import st.project.studyWithUs.service.boardService.BoardService;
import st.project.studyWithUs.vo.BoardVO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardservice;

    @GetMapping("/board") //게시물 목록 조회
    public String board() {
        return "board";
    }


    @GetMapping("/write") //게시물 작성하는 페이지
    public String write() {
        return "writeContent";
    }


    @ResponseBody
    @PostMapping("/saveContent") //게시물 작성버튼 누르면
    public boolean saveContent(@RequestBody HashMap<String, String> data, @Login User loginUser) {


        System.out.println(data.get("title"));
        System.out.println(data.get("content"));

        Board board = new Board();
        board.setTitle(data.get("title"));//""안에 들어가는게 key값
        board.setContent(data.get("content"));



        board.setUser(loginUser);
        board.setUploadTime(LocalDate.now());

        boardservice.saveContent(board);

        return true;
    }

    @ResponseBody
    @GetMapping("/contentsListInfo") //게시물 목록 조회
    public List<BoardVO> contentsListInfo() {
        return boardservice.contentsListInfo();
    }


    @GetMapping("/detailContent/{bID}") //게시물 상세보기
    public String detailContent(@PathVariable("bID") Long bID) {

        return "detailContent";
    }

    @ResponseBody
    @GetMapping("/detailContent")
    public BoardVO detailContent2(@RequestParam Long bID) {

        return boardservice.findBybID(bID);
    }

    @ResponseBody
    @GetMapping("/deleteContent")
    public boolean deleteContent(@RequestParam Long bID, @Login User loginUser) {

        if (loginUser != null) {
            if (boardservice.findBybID2(bID).getUser().getUID() == loginUser.getUID()) return true;

            else return false;
        }
        return false;
    }

    @ResponseBody
    @GetMapping("/deleteBoard")
    public boolean deleteBoard(@RequestParam Long bID) {

        boardservice.delete(bID);
        return true;
    }

    @ResponseBody
    @GetMapping("/updateContent") //게시글 수정 후 상세보기 페이지에 다시 뿌려주기
    public boolean updateContent(@RequestParam Long bID,@RequestParam String ti, @RequestParam String co) {

        log.info("ti co {}, {}", ti, co);

        boardservice.update(bID, ti, co);

        return true;
    }



    @PostMapping(value = "/boardSearch") //게시물 목록 조회페이지
    @ResponseBody
    public List<BoardVO> search(@Login User loginUser, @RequestParam("keyword") String keyword) {

        System.out.println("키워드" + keyword);

        List<BoardVO> list = new ArrayList<>();

        List<Board> all = boardservice.findByTitleContaining(keyword);

        long idx = all.size();

        for (Board li : all) {
            BoardVO vo = new BoardVO();
            vo.setContent(li.getContent());
            vo.setTitle(li.getTitle());
            vo.setName(li.getUser().getUserName());
            vo.setUploadTime(li.getUploadTime());
            vo.setBbID(li.getBID());
            vo.setIdx(idx--);
            vo.setUID(li.getUser().getUID());
            list.add(vo);
        }

        return list;
    }


    @PostMapping("/checkUser") //로그인 안한 유저 댓글 작성 못하게
    @ResponseBody
    public int checkUser(@Login User loginUser){
        if(loginUser != null){
            return 1;
        } else {
            return 0;
        }
    }

}

//    int currentPage=all.getPageable().getPageNumber()+1; // 현재 페이지 넘버 _ 인덱스는 1부터니까 +1
//    int startPage=Math.max(currentPage-4,1);
//    int endPage=Math.min(currentPage+4,all.getTotalPages());
//        model.addAttribute("currentPage",currentPage);
//                model.addAttribute("startPage",startPage);
//                model.addAttribute("endPage",endPage);
//                model.addAttribute("end",all.getTotalPages());
