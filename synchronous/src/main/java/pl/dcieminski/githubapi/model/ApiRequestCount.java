package pl.dcieminski.githubapi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "API_REQUEST_COUNT")
public class ApiRequestCount {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "requestcount_seq")
    @SequenceGenerator(name = "requestcount_seq", allocationSize = 1)
    private Long id;

    @Column(name = "LOGIN", unique = true)
    private String login;

    @Column(name = "REQUEST_COUNT")
    private Integer requestCount;

    public ApiRequestCount() {
    }

    public ApiRequestCount(String login, Integer requestCount) {
        this.login = login;
        this.requestCount = requestCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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