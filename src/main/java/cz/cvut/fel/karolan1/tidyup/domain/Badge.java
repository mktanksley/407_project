package cz.cvut.fel.karolan1.tidyup.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Badge.
 */
@Entity
@Table(name = "badge")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "badge")
public class Badge implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "earned_at")
    private ZonedDateTime earnedAt;

    @ManyToOne
    private TypeOfBadge isOfType;

    @ManyToOne
    private User ownedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(ZonedDateTime earnedAt) {
        this.earnedAt = earnedAt;
    }

    public TypeOfBadge getIsOfType() {
        return isOfType;
    }

    public void setIsOfType(TypeOfBadge typeOfBadge) {
        this.isOfType = typeOfBadge;
    }

    public User getOwnedBy() {
        return ownedBy;
    }

    public void setOwnedBy(User user) {
        this.ownedBy = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Badge badge = (Badge) o;
        if(badge.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, badge.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Badge{" +
            "id=" + id +
            ", earnedAt='" + earnedAt + "'" +
            '}';
    }
}
