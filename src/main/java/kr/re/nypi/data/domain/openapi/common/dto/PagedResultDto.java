package kr.re.nypi.data.domain.openapi.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PagedResultDto<T> {
    private final int totalCount;
    private final int numOfRows;
    private final int pageNo;
    private final List<T> items;
}
