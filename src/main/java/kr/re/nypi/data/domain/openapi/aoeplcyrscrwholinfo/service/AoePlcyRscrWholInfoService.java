package kr.re.nypi.data.domain.openapi.aoeplcyrscrwholinfo.service;

import kr.re.nypi.data.domain.openapi.aoeplcyrscrwholinfo.dto.FilterItemDto;

import java.net.URI;
import java.util.List;

public interface AoePlcyRscrWholInfoService {

    List<FilterItemDto> getFilterData(URI uri);

}
