package cz.cvut.fel.karolan1.tidyup.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Chore.
 */
@Entity
@Table(name = "chore")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "chore")
public class Chore implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "date")
    private ZonedDateTime date;

    @ManyToOne
    private TypeOfChore isOfType;

    @ManyToOne
    private User isDoneBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public TypeOfChore getIsOfType() {
        return isOfType;
    }

    public void setIsOfType(TypeOfChore typeOfChore) {
        this.isOfType = typeOfChore;
    }

    public User getIsDoneBy() {
        return isDoneBy;
    }

    public void setIsDoneBy(User user) {
        this.isDoneBy = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Chore chore = (Chore) o;
        if(chore.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, chore.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Chore{" +
            "id=" + id +
            ", date='" + date + "'" +
            '}';
    }
}
