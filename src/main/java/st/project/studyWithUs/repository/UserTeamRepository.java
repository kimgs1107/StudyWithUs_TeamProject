package st.project.studyWithUs.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import st.project.studyWithUs.domain.Team;
import st.project.studyWithUs.domain.UserTeam;

import java.util.List;

@Repository
public interface UserTeamRepository extends JpaRepository<UserTeam, Long> {

    @Transactional
    @Modifying
    @Query("update UserTeam ut set ut.realTime=0")
    void resetRealTime();

    @Query("select t from Team t , UserTeam ut where ut.user.uID = :uID and ut.team.tID = t.tID")
    List<Team>  findUserTeam(@Param("uID") Long uID);

    @Query("select ut from UserTeam ut where ut.user.uID = :uID and ut.team.tID = :tID")
    UserTeam findByuIDandtID(@Param("uID") Long uID, @Param("tID") Long tID);

}
