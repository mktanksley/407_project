package cz.cvut.fel.karolan1.tidyup.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A ChoreEvent.
 */
@Entity
@Table(name = "chore_event")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "choreevent")
public class ChoreEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "date_to")
    private ZonedDateTime dateTo;

    @Column(name = "date_done")
    private ZonedDateTime dateDone;

    @ManyToOne
    private ChoreType isType;

    @ManyToOne
    private User doneBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDateTo() {
        return dateTo;
    }

    public void setDateTo(ZonedDateTime dateTo) {
        this.dateTo = dateTo;
    }

    public ZonedDateTime getDateDone() {
        return dateDone;
    }

    public void setDateDone(ZonedDateTime dateDone) {
        this.dateDone = dateDone;
    }

    public ChoreType getIsType() {
        return isType;
    }

    public void setIsType(ChoreType choreType) {
        this.isType = choreType;
    }

    public User getDoneBy() {
        return doneBy;
    }

    public void setDoneBy(User user) {
        this.doneBy = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChoreEvent choreEvent = (ChoreEvent) o;
        if(choreEvent.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, choreEvent.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ChoreEvent{" +
            "id=" + id +
            ", dateTo='" + dateTo + "'" +
            ", dateDone='" + dateDone + "'" +
            '}';
    }
}
