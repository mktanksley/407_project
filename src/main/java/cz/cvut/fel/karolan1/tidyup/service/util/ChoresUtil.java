package cz.cvut.fel.karolan1.tidyup.service.util;

import cz.cvut.fel.karolan1.tidyup.domain.ChoreEvent;
import cz.cvut.fel.karolan1.tidyup.repository.ChoreEventRepository;
import cz.cvut.fel.karolan1.tidyup.repository.search.ChoreEventSearchRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;

/**
 * Class with Chore utils.
 */
@Component
@EnableScheduling
public class ChoresUtil {
    @Inject
    private ChoreEventRepository choreEventRepository;

    @Inject
    private ChoreEventSearchRepository choreEventSearchRepository;

    /**
     * Finds all choreEvents that are repeatable and creates new ones according to their interval.
     * Executes every day at 1:00 am.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void createRepeatedEvents() {
        // get all repeatable chores with datetill today
        ZonedDateTime today = ZonedDateTime.of(LocalDate.now(), LocalTime.MIN, ZoneId.of(TimeZone.getDefault().getID()));
        List<ChoreEvent> chores = choreEventRepository.findRepeatableToCopy(today, today.plusDays(1));
        for (ChoreEvent chore : chores) {
            // for each create new with same fields, but dateTo += interval
            ChoreEvent prolonged = chore;
            prolonged.setId(null);
            prolonged.setDateTo(prolonged.getDateTo().plusDays(prolonged.getIsType().getInterval()));
            prolonged.setDateDone(null);

            ChoreEvent result = choreEventRepository.save(prolonged);
            choreEventSearchRepository.save(result);
        }
    }
}
