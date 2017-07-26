package calories.tracker.app.services;

import calories.tracker.app.dao.FirmRepository;
import calories.tracker.app.dao.UserRepository;
import calories.tracker.app.dto.FirmDTO;
import calories.tracker.app.model.Firm;
import calories.tracker.app.model.SearchResult;
import calories.tracker.app.model.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static calories.tracker.app.services.ValidationUtils.assertNotBlank;
import static org.springframework.util.Assert.notNull;

/**
 *
 * Business service for Firm-related operations.
 *
 */
@Service
public class FirmService {

    private static final Logger LOGGER = Logger.getLogger(FirmService.class);

    @Autowired
    FirmRepository firmRepository;

    @Autowired
    UserRepository userRepository;

    /**
     *
     * searches firms by date/time
     *
     * @param username - the currently logged in user
     * @param fromDate - search from this date, including
     * @param toDate - search until this date, including
     * @param fromTime - search from this time, including
     * @param toTime - search to this time, including
     * @param pageNumber - the page number (each page has 10 entries)
     * @return - the found results
     */
    @Transactional(readOnly = true)
    public SearchResult<Firm> findFirms(String username, Date fromDate, Date toDate, Time fromTime, Time toTime, int pageNumber) {

        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("Both the from and to date are needed.");
        }

        if (fromDate.after(toDate)) {
            throw new IllegalArgumentException("From date cannot be after to date.");
        }

        if (fromDate.equals(toDate) && fromTime != null && toTime != null && fromTime.after(toTime)) {
            throw new IllegalArgumentException("On searches on the same day, from time cannot be after to time.");
        }

        Long resultsCount = firmRepository.countFirmsByDateTime(username, fromDate, toDate, fromTime, toTime);

        List<Firm> firms = firmRepository.findFirmsByDateTime(username, fromDate, toDate, fromTime, toTime, pageNumber);

        return new SearchResult<>(resultsCount, firms);
    }

    /**
     *
     * deletes a list of firms, given their Ids
     *
     * @param deletedFirmIds - the list of firms to delete
     */
    @Transactional
    public void deleteFirms(List<Long> deletedFirmIds) {
        notNull(deletedFirmIds, "deletedFirmsId is mandatory");
        deletedFirmIds.stream().forEach((deletedFirmId) -> firmRepository.delete(deletedFirmId));
    }

    /**
     *
     * saves a firm (new or not) into the database.
     *
     * @param username - - the currently logged in user
     * @param id - the database ud of the firm
     * @param date - the date the firm took place
     * @param time - the time the firm took place
     * @param description - the description of the firm
     * @param calories - the calories of the firm
     * @return - the new version of the firm
     */

    @Transactional
    public Firm saveFirm(String username, Long id, Date date, Time time, String description, Long calories) {

        assertNotBlank(username, "username cannot be blank");
        notNull(date, "date is mandatory");
        notNull(time, "time is mandatory");
        notNull(description, "description is mandatory");
        notNull(calories, "calories is mandatory");

        Firm firm = null;

        if (id != null) {
            firm = firmRepository.findFirmById(id);

            firm.setDate(date);
            firm.setTime(time);
            firm.setDescription(description);
            firm.setCalories(calories);
        } else {
            User user = userRepository.findUserByUsername(username);

            if (user != null) {
                firm = firmRepository.save(new Firm(user, date, time, description, calories));
                LOGGER.warn("A firm was attempted to be saved for a non-existing user: " + username);
            }
        }

        return firm;
    }

    /**
     *
     * saves a list of firms (new or not) into the database
     *
     * @param username - the currently logged in user
     * @param firms - the list of firms to be saved
     * @return - the new versions of the saved firms
     */
    @Transactional
    public List<Firm> saveFirms(String username, List<FirmDTO> firms) {
        return firms.stream()
                .map((firm) -> saveFirm(
                        username,
                        firm.getId(),
                        firm.getDate(),
                        firm.getTime(),
                        firm.getDescription(),
                        firm.getCalories()))
                .collect(Collectors.toList());
    }
}
