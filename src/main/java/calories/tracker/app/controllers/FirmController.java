package calories.tracker.app.controllers;

import calories.tracker.app.dto.FirmDTO;
import calories.tracker.app.dto.FirmsDTO;
import calories.tracker.app.dto.FirmDTO;
import calories.tracker.app.dto.FirmsDTO;
import calories.tracker.app.model.Firm;
import calories.tracker.app.model.SearchResult;
import calories.tracker.app.services.FirmService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 *  REST service for firms - allows to update, create and search for firms for the currently logged in user.
 *
 */
@Controller
@RequestMapping("firm")

public class FirmController {
	Logger LOGGER = Logger.getLogger(FirmController.class);

    private static final long DAY_IN_MS = 1000 * 60 * 60 * 24;


    @Autowired
    private FirmService firmService;

    /**
     * search Firms for the current user by date and time ranges.
     *
     *
     * @param principal  - the current logged in user
     * @param fromDate - search from this date, including
     * @param toDate - search until this date, including
     * @param fromTime - search from this time, including
     * @param toTime - search to this time, including
     * @param pageNumber - the page number (each page has 10 entries)
     * @return - @see FirmsDTO with the current page, total pages and the list of firms
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public FirmsDTO searchFirmsByDate(
            Principal principal,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") Date fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") Date toDate,
            @RequestParam(value = "fromTime", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") Date fromTime,
            @RequestParam(value = "toTime", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") Date toTime,
            @RequestParam(value = "pageNumber") Integer pageNumber) {

        if (fromDate == null && toDate == null) {
            fromDate = new Date(System.currentTimeMillis() - (3 * DAY_IN_MS));
            toDate = new Date();
        }

        SearchResult<Firm> result = firmService.findFirms(
                principal.getName(),
                fromDate,
                toDate,
                fromTime != null ? new Time(fromTime.getTime()) : null,
                toTime != null ? new Time(toTime.getTime()) : null,
                pageNumber);

        Long resultsCount = result.getResultsCount();
        Long totalPages = resultsCount / 10;

        if (resultsCount % 10 > 0) {
            totalPages++;
        }

        return new FirmsDTO(pageNumber, totalPages, FirmDTO.mapFromFirmsEntities(result.getResult()));
    }

    /**
     *
     * saves a list of firms - they be either new or existing
     *
     * @param principal - the current logged in user
     * @param firms - the list of firms to save
     * @return - an updated version of the saved firms
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.POST)
    public List<FirmDTO> saveFirms(Principal principal, @RequestBody List<FirmDTO> firms) {

        List<Firm> savedFirms = firmService.saveFirms(principal.getName(), firms);

        return savedFirms.stream()
                .map(FirmDTO::mapFromFirmEntity)
                .collect(Collectors.toList());
    }

    /**
     *
     * deletes a list of firms
     *
     * @param deletedFirmIds - the ids of the firms to be deleted
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteFirms(@RequestBody List<Long> deletedFirmIds) {
        firmService.deleteFirms(deletedFirmIds);
    }

    /**
     *
     * error handler for backend errors - a 400 status code will be sent back, and the body
     * of the message contains the exception text.
     *
     * @param exc - the exception caught
     */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> errorHandler(Exception exc) {
        LOGGER.error(exc.getMessage(), exc);
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
