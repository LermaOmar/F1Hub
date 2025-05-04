package ptzt.f1Hub.instraestructure.dto.out.shared;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public final class PageOutDto<T> {

    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private long totalPages;
    private List<T> content;

    public PageOutDto(Page<T> page) {
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.content = page.getContent();
    }
}