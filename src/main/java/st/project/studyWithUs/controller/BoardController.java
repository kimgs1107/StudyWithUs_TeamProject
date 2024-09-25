package st.project.studyWithUs.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import st.project.studyWithUs.argumentresolver.Login;
import st.project.studyWithUs.domain.Board;
import st.project.studyWithUs.domain.Comment;
import st.project.studyWithUs.domain.User;
import st.project.studyWithUs.service.boardService.BoardService;
import st.project.studyWithUs.service.commentService.CommentService;
import st.project.studyWithUs.vo.BoardVO;
import st.project.studyWithUs.vo.CommentVO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardservice;
    private final CommentService commentService;


    @GetMapping(value = "/boardSearch") //게시물 목록 조회페이지
    public String search(Model model, @Login User loginUser, @PageableDefault(page = 0, size = 10, sort = "uploadTime", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam(required = false, defaultValue = "", name = "keyword") String keyword) {

        List<BoardVO> list = new ArrayList<>();

        Page<Board> all = boardservice.findByTitleContaining(keyword, pageable);
        model.addAttribute("boardList", all);

        int currentPage = all.getPageable().getPageNumber() + 1; // 현재 페이지 넘버 _ 인덱스는 1부터니까 +1
        int startPage = Math.max(currentPage - 4, 1);
        int endPage = Math.min(currentPage + 4, all.getTotalPages());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("end", all.getTotalPages());


        int size = all.getSize();
        int totalCount = (int) all.getTotalElements();
        int idx = Math.max((totalCount - (size * (currentPage - 1))), 1);

        for (Board li : all) {
            BoardVO vo = new BoardVO();
            vo.setContent(li.getContent());
            vo.setTitle(li.getTitle());
            vo.setName(li.getUser().getUserName());
            vo.setUploadTime(li.getUploadTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            vo.setBbID(li.getBID());
            vo.setIdx(idx--);
            vo.setUID(li.getUser().getUID());
            list.add(vo);
        }

        model.addAttribute("VOlist", list);

        return "/board";
    }

    @GetMapping("/write") //게시물 작성하는 페이지
    public String write() {
        return "writeContent";
    }


    @ResponseBody
    @PostMapping("/saveContent") //게시물 작성버튼 누르면
    public boolean saveContent(@RequestBody HashMap<String, String> data, @Login User loginUser) {

        Board board = new Board();
        board.setTitle(data.get("title"));
        board.setContent(data.get("content"));

        board.setUser(loginUser);
        board.setUploadTime(LocalDateTime.now());

        boardservice.saveContent(board);

        return true;
    }

    @ResponseBody
    @GetMapping("/contentsListInfo")
    public List<BoardVO> contentsListInfo() {
        return boardservice.contentsListInfo();
    }

    static BoardVO contentvo = new BoardVO();


    @PostMapping("/detailContent/{idx}") //게시물 상세보기
    public String detailContentPost(@PathVariable("idx") String idx, String bID) {
        contentvo.setBbID(Long.parseLong(bID));
        return "detailContent";
    }


    @GetMapping("/detailContent/{idx}") // 게시글 수정 후 redirect시 필요.
    public String detailContentGet(@PathVariable("idx") String idx) {
        return "detailContent";
    }

    @ResponseBody
    @GetMapping("/detailContent")
    public BoardVO detailContent2(@RequestParam String idx) {
        contentvo.setIdx(Integer.parseInt(idx));

        // 조회수 증가
        boardservice.increaseViewCount(contentvo.getBbID());

        BoardVO vo = boardservice.findBybID(contentvo.getBbID());
        vo.setIdx(Integer.parseInt(idx));
        return vo;
    }

    @ResponseBody
    @GetMapping("/deleteContent")
    public boolean deleteContent(@Login User loginUser) {

        if (loginUser != null) {
            if (boardservice.findBybID2(contentvo.getBbID()).getUser().getUID() == loginUser.getUID()) return true;

            else return false;
        }
        return false;
    }

    @ResponseBody
    @GetMapping("/deleteBoard")
    public boolean deleteBoard() {

        boardservice.delete(contentvo.getBbID());

        return true;
    }

    @ResponseBody
    @GetMapping("/updateContent") //게시글 수정 후 상세보기 페이지에 다시 뿌려주기
    public boolean updateContent(@RequestParam String ti, @RequestParam String co) {

        boardservice.update(contentvo.getBbID(), ti, co);

        return true;
    }



    @PostMapping("/checkUser") //로그인 안한 유저 댓글 작성 못하게
    @ResponseBody
    public int checkUser(@Login User loginUser) {
        if (loginUser != null) {
            return 1;
        } else {
            return 0;
        }
    }

    @ResponseBody
    @GetMapping("/findComment")
    public List<CommentVO> board() {

        return commentService.findComment(contentvo.getBbID());

    }


    @ResponseBody
    @GetMapping("/saveComment")
    public CommentVO saveComment(@Login User loginUser, @RequestParam String comment) {

        Comment comment1 = new Comment();
        comment1.setCommentContent(comment);
        comment1.setWriterUID(loginUser.getUID());
        comment1.setBoard(boardservice.findBybID2(contentvo.getBbID()));
        Long cmID = commentService.save(comment1);
        Comment resComment = commentService.findBycmID(cmID);

        CommentVO vo = new CommentVO();
        vo.setCmID(cmID);
        vo.setWriterName(loginUser.getUserName());
        vo.setCommentContent(comment);

        return vo;
    }


    @ResponseBody
    @GetMapping("/deleteCommentCheck")
    public boolean deleteCommentCheck(@RequestParam Long cmID, @Login User loginUser) {
        if (loginUser == null) {
            return false;
        }
        //댓글 주인이거나, 그 글의 글쓴이거나 둘 중 하나
        else if (loginUser.getUID() == commentService.findBycmID(cmID).getWriterUID() || boardservice.findBybID2(contentvo.getBbID()).getUser().getUID() == loginUser.getUID()) {
            return true;
        } else return false;
    }


    @ResponseBody
    @GetMapping("/deleteComment")
    public boolean deleteComment(@RequestParam Long cmID) {

        commentService.delete(cmID);

        return true;
    }

    @ResponseBody
    @GetMapping("/updateComment")
    public boolean updateComment(@RequestParam Long cmID) {
        return true;
    }

    @ResponseBody
    @GetMapping("/updateCommentCheck")
    public boolean updateCommentCheck(@RequestParam Long cmID, @Login User loginUser) {
        if (loginUser == null) {
            return false;
        } else {
            return commentService.updateCommentCheck(cmID, loginUser.getUID());
        }
    }


    @ResponseBody
    @GetMapping("/updateCommentSave")
    public boolean updateCommentSave(@RequestParam Long cmID, @RequestParam String commentContent) {

        commentService.updateCommentSave(cmID, commentContent);
        return true;
    }

}