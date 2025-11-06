package nypi.openapi.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class UriBuilderUtil {

    @Value("${api.base.url}")
    private String baseUrl;

    public URI buildSearchUri(String opnDataCd) {
        String searchPath = "/openapi/service/openapi/Search";
        return UriComponentsBuilder.fromUriString(baseUrl)
                .path(searchPath)
                .queryParam("_type", "json")
                .queryParam("opnDataCd", opnDataCd)
                .build()
                .toUri();
    }

    public URI buildWholeSearchUri() {
        String wholeSearchPath = "/openapi/service/openapi/WholSearchFilter";
        return UriComponentsBuilder.fromUriString(baseUrl)
                .path(wholeSearchPath)
                .queryParam("_type", "json")
                .build()
                .toUri();
    }
}
