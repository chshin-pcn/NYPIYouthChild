package kr.re.nypi.data.domain.openapi.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import kr.re.nypi.data.domain.openapi.common.dto.FilterOptionsDto;

import java.net.URI;

public interface SearchFilterService {

    FilterOptionsDto getProcessedFilterData(URI uri, String opnDataCd);

    JsonNode refreshFilterCache(URI uri, String opnDataCd);
}
