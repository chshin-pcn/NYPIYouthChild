package nypi.openapi.domain.aoeplcyrscrwholinfo.service;

import nypi.openapi.domain.aoeplcyrscrwholinfo.dto.FilterItemDto;

import java.net.URI;
import java.util.List;

public interface AoePlcyRscrWholInfoService {

    List<FilterItemDto> getFilterData(URI uri);

}
