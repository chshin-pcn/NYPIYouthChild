package nypi.openapi.domain.common.service;

import nypi.openapi.domain.common.dto.FilterOptionsDto;

import java.net.URI;

public interface SearchFilterService {

    FilterOptionsDto getProcessedFilterData(URI uri, String opnDataCd);

}
