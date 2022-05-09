package st.project.studyWithUs.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import st.project.studyWithUs.domain.Team;
import st.project.studyWithUs.domain.UserTeam;

import java.util.List;

@Repository
public interface UserTeamRepository extends JpaRepository<UserTeam, Long> {


    @Query("select t from Team t , UserTeam ut where ut.user.uID = :uID and ut.team.tID = t.tID")
    List<Team>  findUserTeam(@Param("uID") Long uID);

}
