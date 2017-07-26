package calories.tracker.app.dto;

import java.util.List;

/**
 *
 * JSON serializable DTO containing data concerning a firm search request.
 *
 */
public class FirmsDTO {
    private long currentPage;
    private long totalPages;
    List<FirmDTO> firms;

    public FirmsDTO(long currentPage, long totalPages, List<FirmDTO> firms) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.firms = firms;
    }

    public long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<FirmDTO> getFirms() {
        return firms;
    }

    public void setFirms(List<FirmDTO> firms) {
        this.firms = firms;
    }
}
