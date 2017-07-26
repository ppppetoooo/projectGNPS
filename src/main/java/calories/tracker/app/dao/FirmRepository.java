package calories.tracker.app.dao;

import calories.tracker.app.model.Firm;
import calories.tracker.app.model.User;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * Repository class for the Firm entity
 *
 */
@Repository
public class FirmRepository {

    private static final Logger LOGGER = Logger.getLogger(FirmRepository.class);

    @PersistenceContext
    EntityManager em;

    /**
     *
     * counts the matching firms, given the bellow criteria
     *
     * @param username - the currently logged in username
     * @param fromDate - search from this date, including
     * @param toDate - search until this date, including
     * @param fromTime - search from this time, including
     * @param toTime - search to this time, including
     * @return -  a list of matching firms, or an empty collection if no match found
     */
    public Long countFirmsByDateTime(String username, Date fromDate, Date toDate, Time fromTime, Time toTime) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // query for counting the total results
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Firm> countRoot = cq.from(Firm.class);
        cq.select((cb.count(countRoot)));
        cq.where(getCommonWhereCondition(cb, username, countRoot, fromDate, toDate, fromTime, toTime));
        Long resultsCount = em.createQuery(cq).getSingleResult();

        LOGGER.info("Found " + resultsCount + " results.");

        return resultsCount;
    }

    /**
     *
     * finds a list of firms, given the bellow criteria
     *
     * @param username - the currently logged in username
     * @param fromDate - search from this date, including
     * @param toDate - search until this date, including
     * @param fromTime - search from this time, including
     * @param toTime - search to this time, including
     * @return -  a list of matching firms, or an empty collection if no match found
     */
    public List<Firm> findFirmsByDateTime(String username, Date fromDate, Date toDate,
                                          Time fromTime, Time toTime, int pageNumber) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // the actual search query that returns one page of results
        CriteriaQuery<Firm> searchQuery = cb.createQuery(Firm.class);
        Root<Firm> searchRoot = searchQuery.from(Firm.class);
        searchQuery.select(searchRoot);
        searchQuery.where(getCommonWhereCondition(cb, username, searchRoot, fromDate, toDate, fromTime, toTime));

        List<Order> orderList = new ArrayList();
        orderList.add(cb.desc(searchRoot.get("date")));
        orderList.add(cb.asc(searchRoot.get("time")));
        searchQuery.orderBy(orderList);

        TypedQuery<Firm> filterQuery = em.createQuery(searchQuery)
                .setFirstResult((pageNumber - 1) * 10)
                .setMaxResults(10);

        return filterQuery.getResultList();
    }

    /**
     * Delete a firm, given its identifier
     *
     * @param deletedFirmId - the id of the firm to be deleted
     */
    public void delete(Long deletedFirmId) {
    	Firm delete = em.find(Firm.class, deletedFirmId);
        em.remove(delete);
    }

    /**
     *
     * finds a firm given its id
     *
     */
    public Firm findFirmById(Long id) {
        return em.find(Firm.class, id);
    }

    /**
     *
     * save changes made to a firm, or create the firm if its a new firm.
     *
     */
    public Firm save(Firm firm) {
        return em.merge(firm);
    }


    private Predicate[] getCommonWhereCondition(CriteriaBuilder cb, String username, Root<Firm> searchRoot, Date fromDate, Date toDate,
                                                Time fromTime, Time toTime) {

        List<Predicate> predicates = new ArrayList<>();
        Join<Firm, User> user = searchRoot.join("user");

        predicates.add(cb.equal(user.<String>get("username"), username));
        predicates.add(cb.greaterThanOrEqualTo(searchRoot.<Date>get("date"), fromDate));

        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(searchRoot.<Date>get("date"), toDate));
        }

        if (fromTime != null) {
            predicates.add(cb.greaterThanOrEqualTo(searchRoot.<Date>get("time"), fromTime));
        }

        if (toTime != null) {
            predicates.add(cb.lessThanOrEqualTo(searchRoot.<Date>get("time"), toTime));
        }

        return predicates.toArray(new Predicate[]{});
    }

}
