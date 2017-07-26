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
     * @param id - the database id of the firm
     * @param name - the name the firm took place
     * @param address - the address the firm took place
     * @param account_name - the account number of the firm
     * @param ico - the ico of the firm
     * @param dic - the dic of the firm
     * @param ic_dph - the ic dph of the firm
     * @return - the new version of the firm
     */

    @Transactional
    public Firm saveFirm(String username, Long id, String name, String address, String account_num, String ico, String dic, String ic_dph) {

        assertNotBlank(username, "username cannot be blank");
        notNull(name, "name is mandatory");
        notNull(address, "address is mandatory");
        notNull(account_num, "account number is mandatory");
        notNull(ico, "ico is mandatory");
        notNull(dic, "dic is mandatory");
        notNull(ic_dph, "ic dph is mandatory");

        Firm firm = null;

        if (id != null) {
            firm = firmRepository.findFirmById(id);
            
            firm.setName(name);
            firm.setAddress(address);
            firm.setAccount_num(account_num);
            firm.setIco(ico);
            firm.setDic(dic);
            firm.setIc_dph(ic_dph);
        } else {
            User user = userRepository.findUserByUsername(username);

            if (user != null) {
                firm = firmRepository.save(new Firm(user, name, address, account_num, ico, dic, ic_dph));
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
                        firm.getName(),
                        firm.getAddress(),
                        firm.getAccount_num(),
                        firm.getIco(),
                        firm.getDic(),
                        firm.getIc_dph()))
                .collect(Collectors.toList());
    }
}
