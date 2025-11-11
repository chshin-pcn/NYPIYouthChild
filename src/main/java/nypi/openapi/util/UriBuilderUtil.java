package nypi.openapi.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Objects;

@Slf4j
@Component
public class UriBuilderUtil {

    @Value("${api.base.url}")
    private String baseUrl;

    private String encode(String value) {
        if (value == null) {
            return null;
        }
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("URI 인코딩 실패: {}", e.getMessage());
            throw new RuntimeException("URI 인코딩 실패", e);
        }
    }

    private UriComponentsBuilder createBaseUriBuilder(String path) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .path(path)
                .queryParam("_type", "json");
    }

    private void addQueryParamIfPresent(UriComponentsBuilder builder, String key, String value) {
        if (Objects.nonNull(value) && !value.isEmpty()) {
            builder.queryParam(key, value);
        }
    }

    public URI buildSearchUri(String opnDataCd) {
        return createBaseUriBuilder("/openapi/service/openapi/Search")
                .queryParam("opnDataCd", opnDataCd)
                .build(true)
                .toUri();
    }

    public URI buildWholeSearchUri() {
        return createBaseUriBuilder("/openapi/service/openapi/WholSearchFilter")
                .build(true)
                .toUri();
    }

    public URI buildAoePlcyRscrWholInfoUri(
            String pageNo, String numOfRows, String ornuNm, String opnDataCd,
            String rspnsMnbdNm, String searchKeyword, String srvyYr01, String srvyYr02) {

        UriComponentsBuilder uriBuilder = createBaseUriBuilder("/openapi/service/openapi/AoePlcyRscrWholInfo")
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows);

        addQueryParamIfPresent(uriBuilder, "searchKeyword", encode(searchKeyword));
        addQueryParamIfPresent(uriBuilder, "opnDataCd", opnDataCd);
        addQueryParamIfPresent(uriBuilder, "ornuNm", encode(ornuNm));
        addQueryParamIfPresent(uriBuilder, "srvyYr01", srvyYr01);
        addQueryParamIfPresent(uriBuilder, "srvyYr02", srvyYr02);
        addQueryParamIfPresent(uriBuilder, "rspnsMnbdNm", encode(rspnsMnbdNm));

        return uriBuilder.build(true).toUri();
    }

    public URI buildKorKidAoePnlInfoUri(
            String pageNo, String numOfRows, String srvyYr, String rspnsMnbdNm,
            String srvyQitemId, String svbnClsfCd01, String svbnClsfCd02, String svbnClsfCd03, String aiCrtYn) {

        UriComponentsBuilder uriBuilder = createBaseUriBuilder("/openapi/service/openapi/KorKidAoePnlInfo")
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("srvyYr", srvyYr)
                .queryParam("rspnsMnbdNm", encode(rspnsMnbdNm))
                .queryParam("srvyQitemId", srvyQitemId)
                .queryParam("aiCrtYn", aiCrtYn);

        addQueryParamIfPresent(uriBuilder, "svbnClsfCd01", svbnClsfCd01);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd02", svbnClsfCd02);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd03", svbnClsfCd03);

        return uriBuilder.build(true).toUri();
    }

    public URI buildMcltAoePnlInfoUri(
            String pageNo, String numOfRows, String ornuNm, String srvyYr, String rspnsMnbdNm,
            String srvyQitemId, String svbnClsfCd01, String svbnClsfCd02, String svbnClsfCd03, String aiCrtYn) {

        UriComponentsBuilder uriBuilder = createBaseUriBuilder("/openapi/service/openapi/McltAoePnlInfo")
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("ornuNm", encode(ornuNm))
                .queryParam("srvyYr", srvyYr)
                .queryParam("rspnsMnbdNm", encode(rspnsMnbdNm))
                .queryParam("srvyQitemId", srvyQitemId)
                .queryParam("aiCrtYn", aiCrtYn);

        addQueryParamIfPresent(uriBuilder, "svbnClsfCd01", svbnClsfCd01);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd02", svbnClsfCd02);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd03", svbnClsfCd03);

        return uriBuilder.build(true).toUri();
    }

    public URI buildKorAoePnlInfoUri(
            String pageNo, String numOfRows, String srvyYr, String rspnsMnbdNm,
            String srvyQitemId, String svbnClsfCd01, String svbnClsfCd02, String svbnClsfCd03, String aiCrtYn) {

        UriComponentsBuilder uriBuilder = createBaseUriBuilder("/openapi/service/openapi/KorAoePnlInfo")
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("srvyYr", srvyYr)
                .queryParam("rspnsMnbdNm", encode(rspnsMnbdNm))
                .queryParam("srvyQitemId", srvyQitemId)
                .queryParam("aiCrtYn", aiCrtYn);

        addQueryParamIfPresent(uriBuilder, "svbnClsfCd01", svbnClsfCd01);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd02", svbnClsfCd02);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd03", svbnClsfCd03);

        return uriBuilder.build(true).toUri();
    }

    public URI buildStdsItrpAoePnlInfoUri(
            String pageNo, String numOfRows, String srvyYr, String srvyQitemId,
            String svbnClsfCd01, String svbnClsfCd02, String svbnClsfCd03, String aiCrtYn) {

        UriComponentsBuilder uriBuilder = createBaseUriBuilder("/openapi/service/openapi/StdsItrpAoePnlInfo")
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("srvyYr", srvyYr)
                .queryParam("srvyQitemId", srvyQitemId)
                .queryParam("aiCrtYn", aiCrtYn);

        addQueryParamIfPresent(uriBuilder, "svbnClsfCd01", svbnClsfCd01);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd02", svbnClsfCd02);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd03", svbnClsfCd03);

        return uriBuilder.build(true).toUri();
    }

    public URI buildKidAoeHrthAccndInfoUri(
            String pageNo, String numOfRows, String srvyYr, String rspnsMnbdNm,
            String srvyQitemId, String svbnClsfCd01, String svbnClsfCd02, String svbnClsfCd03, String aiCrtYn) {

        UriComponentsBuilder uriBuilder = createBaseUriBuilder("/openapi/service/openapi/KidAoeHrthAccndInfo")
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("srvyYr", srvyYr)
                .queryParam("rspnsMnbdNm", encode(rspnsMnbdNm))
                .queryParam("srvyQitemId", srvyQitemId)
                .queryParam("aiCrtYn", aiCrtYn);

        addQueryParamIfPresent(uriBuilder, "svbnClsfCd01", svbnClsfCd01);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd02", svbnClsfCd02);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd03", svbnClsfCd03);

        return uriBuilder.build(true).toUri();
    }

    public URI buildYtScitEcnmAccndInfoUri(
            String pageNo, String numOfRows, String srvyYr, String srvyQitemId,
            String svbnClsfCd01, String svbnClsfCd02, String svbnClsfCd03, String aiCrtYn) {

        UriComponentsBuilder uriBuilder = createBaseUriBuilder("/openapi/service/openapi/YtScitEcnmAccndInfo")
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("srvyYr", srvyYr)
                .queryParam("srvyQitemId", srvyQitemId)
                .queryParam("aiCrtYn", aiCrtYn);

        addQueryParamIfPresent(uriBuilder, "svbnClsfCd01", svbnClsfCd01);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd02", svbnClsfCd02);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd03", svbnClsfCd03);

        return uriBuilder.build(true).toUri();
    }
}
