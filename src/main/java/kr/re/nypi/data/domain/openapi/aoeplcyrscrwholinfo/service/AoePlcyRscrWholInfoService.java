package kr.re.nypi.data.domain.openapi.aoeplcyrscrwholinfo.service;

import com.fasterxml.jackson.databind.JsonNode;
import kr.re.nypi.data.domain.openapi.aoeplcyrscrwholinfo.dto.FilterItemDto;

import java.net.URI;
import java.util.List;

public interface AoePlcyRscrWholInfoService {

    List<FilterItemDto> getFilterData(URI uri);

    JsonNode refreshFilterCache(URI uri);
}
