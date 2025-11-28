package kr.re.nypi.data.domain.openapi.common.service;

import kr.re.nypi.data.domain.openapi.common.dto.PagedResultDto;

import java.net.URI;

public interface ApiService {

    <T> PagedResultDto<T> getSurveyResult(URI uri, Class<T> itemType);

}
