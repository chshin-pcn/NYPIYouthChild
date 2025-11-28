package kr.re.nypi.data.domain.openapi.common.service;

import kr.re.nypi.data.domain.openapi.common.dto.FilterOptionsDto;

import java.net.URI;

public interface SearchFilterService {

    FilterOptionsDto getProcessedFilterData(URI uri, String opnDataCd);

}
