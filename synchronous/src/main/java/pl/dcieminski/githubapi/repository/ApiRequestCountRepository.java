package pl.dcieminski.githubapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.dcieminski.githubapi.model.ApiRequestCount;

@Repository
public interface ApiRequestCountRepository extends JpaRepository<ApiRequestCount, Long> {

    boolean existsByLogin(String login);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE ApiRequestCount SET requestCount = requestCount + 1 WHERE login = :login")
    void increaseRequestCountForLogin(@Param("login") String login);
}