package nypi.openapi.domain.common.service;

import nypi.openapi.domain.common.dto.PagedResultDto;

import java.net.URI;

public interface ApiService {

    <T> PagedResultDto<T> getSurveyResult(URI uri, Class<T> itemType);

}
