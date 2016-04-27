package cz.cvut.fel.karolan1.tidyup.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Event.
 */
@Entity
@Table(name = "event")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "event")
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "date_till")
    private ZonedDateTime dateTill;

    @Column(name = "date_done")
    private ZonedDateTime dateDone;

    @ManyToOne
    private User doneBy;

    @ManyToOne
    private Chore is;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDateTill() {
        return dateTill;
    }

    public void setDateTill(ZonedDateTime dateTill) {
        this.dateTill = dateTill;
    }

    public ZonedDateTime getDateDone() {
        return dateDone;
    }

    public void setDateDone(ZonedDateTime dateDone) {
        this.dateDone = dateDone;
    }

    public User getDoneBy() {
        return doneBy;
    }

    public void setDoneBy(User user) {
        this.doneBy = user;
    }

    public Chore getIs() {
        return is;
    }

    public void setIs(Chore chore) {
        this.is = chore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event event = (Event) o;
        if(event.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Event{" +
            "id=" + id +
            ", dateTill='" + dateTill + "'" +
            ", dateDone='" + dateDone + "'" +
            '}';
    }
}
