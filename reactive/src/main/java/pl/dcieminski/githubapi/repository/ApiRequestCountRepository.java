package pl.dcieminski.githubapi.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;
import pl.dcieminski.githubapi.model.ApiRequestCount;
import reactor.core.publisher.Mono;

@Repository
public interface ApiRequestCountRepository extends ReactiveMongoRepository<ApiRequestCount, String> {

    Mono<Boolean> existsByLogin(String login);

    @Query("{login: ?0}")
    @Update("{$inc: {requestCount: 1}}")
    Mono<Void> increaseRequestCountForLogin(String login);
}