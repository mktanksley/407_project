package cz.cvut.fel.karolan1.tidyup.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Flat.
 */
@Entity
@Table(name = "flat")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "flat")
public class Flat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "date_created")
    private ZonedDateTime dateCreated;

    @OneToOne
    @JoinColumn(unique = true)
    private User hasAdmin;

    @OneToMany(mappedBy = "memberOf")
    @JsonManagedReference
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<User> residents = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "flat_friends",
               joinColumns = @JoinColumn(name="flat_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="friend_id", referencedColumnName="ID"))
    private Set<Flat> friends = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(ZonedDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public User getHasAdmin() {
        return hasAdmin;
    }

    public void setHasAdmin(User user) {
        this.hasAdmin = user;
    }

    public Set<User> getResidents() {
        return residents;
    }

    public void setResidents(Set<User> users) {
        this.residents = users;
    }

    public Set<Flat> getFriends() {
        return friends;
    }

    public void setFriends(Set<Flat> flats) {
        this.friends = flats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Flat flat = (Flat) o;
        if(flat.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, flat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Flat{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", dateCreated='" + dateCreated + "'" +
            '}';
    }
}
