package st.project.studyWithUs.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import st.project.studyWithUs.domain.Board;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface BoardRepository extends JpaRepository<Board, Long> {


    Board findBybID(Long bID);

    @Query("select b from Board b where b.title like concat('%', :title, '%') order by b.uploadTime DESC" )
    List<Board> findByTitleContaining(@Param("title") String title);

}


