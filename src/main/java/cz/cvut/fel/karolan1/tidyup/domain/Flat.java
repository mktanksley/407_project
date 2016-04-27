package cz.cvut.fel.karolan1.tidyup.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

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
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "date_created")
    private ZonedDateTime dateCreated;

    @OneToOne
    @JoinColumn(unique = true)
    private User hasAdmin;

    @OneToMany(mappedBy = "isResidentOf")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<User> hasResidents = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "flat_friend_of",
               joinColumns = @JoinColumn(name="flats_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="friend_ofs_id", referencedColumnName="ID"))
    private Set<Flat> friendOfs = new HashSet<>();

    @ManyToMany(mappedBy = "friendOfs")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Flat> friendWiths = new HashSet<>();

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

    public Set<User> getHasResidents() {
        return hasResidents;
    }

    public void setHasResidents(Set<User> users) {
        this.hasResidents = users;
    }

    public Set<Flat> getFriendOfs() {
        return friendOfs;
    }

    public void setFriendOfs(Set<Flat> flats) {
        this.friendOfs = flats;
    }

    public Set<Flat> getFriendWiths() {
        return friendWiths;
    }

    public void setFriendWiths(Set<Flat> flats) {
        this.friendWiths = flats;
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
