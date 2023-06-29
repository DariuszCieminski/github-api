package pl.dcieminski.githubapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("API_REQUEST_COUNT")
public class ApiRequestCount {

    @Id
    private String id;

    @Field("LOGIN")
    @Indexed(unique = true)
    private String login;

    @Field("REQUEST_COUNT")
    private Integer requestCount;

    public ApiRequestCount() {
    }

    public ApiRequestCount(String login, Integer requestCount) {
        this.login = login;
        this.requestCount = requestCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Integer getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Integer requestCount) {
        this.requestCount = requestCount;
    }
}