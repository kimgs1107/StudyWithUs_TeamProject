package st.project.studyWithUs.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import st.project.studyWithUs.domain.Team;

import java.util.List;


@Repository
public interface TeamRepository extends JpaRepository<Team, Long>{


    Team findBytID(Long tId);

    @Query ("select t from Team t, UserTeam ut where t.tID = ut.team.tID and ut.user.uID=:uId")
    List<Team> findMyTeam(@Param("uId") Long uId);

    @Query ("select t from Team t where t.currentCount <> t.headCount")
    List<Team> findTeams();

    Team findByTeamName(String teamName);



}
