package nypi.openapi.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Objects;

@Component
public class UriBuilderUtil {

    @Value("${api.base.url}")
    private String baseUrl;

    public URI buildSearchUri(String opnDataCd) {
        String path = "/openapi/service/openapi/Search";
        return UriComponentsBuilder.fromUriString(baseUrl)
                .path(path)
                .queryParam("_type", "json")
                .queryParam("opnDataCd", opnDataCd)
                .build(true)
                .toUri();
    }

    public URI buildWholeSearchUri() {
        String path = "/openapi/service/openapi/WholSearchFilter";
        return UriComponentsBuilder.fromUriString(baseUrl)
                .path(path)
                .queryParam("_type", "json")
                .build(true)
                .toUri();
    }

    public URI buildAoePlcyRscrWholInfoUri(
            String ornuNm,
            String opnDataCd,
            String rspnsMnbdNm,
            String searchKeyword,
            String srvyYr01,
            String srvyYr02
    ) throws UnsupportedEncodingException {
        String path = "/openapi/service/openapi/AoePlcyRscrWholInfo";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl)
                .path(path)
                .queryParam("_type", "json");

        if (Objects.nonNull(searchKeyword)) {
            uriBuilder.queryParam("searchKeyword", URLEncoder.encode(searchKeyword, "UTF-8"));
        }
        addQueryParamIfPresent(uriBuilder, "opnDataCd", opnDataCd);
        addQueryParamIfPresent(uriBuilder, "ornuNm", ornuNm);
        addQueryParamIfPresent(uriBuilder, "srvyYr01", srvyYr01);
        addQueryParamIfPresent(uriBuilder, "srvyYr02", srvyYr02);
        addQueryParamIfPresent(uriBuilder, "rspnsMnbdNm", URLEncoder.encode(rspnsMnbdNm, "UTF-8"));
        return uriBuilder.build(true).toUri();
    }

    public URI buildKorKidAoePnlInfoUri(
            String srvyYr,
            String rspnsMnbdNm,
            String srvyQitemId,
            String svbnClsfCd01,
            String svbnClsfCd02,
            String svbnClsfCd03,
            String aiCrtYn
    ) throws UnsupportedEncodingException {
        String path = "/openapi/service/openapi/KorKidAoePnlInfo";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl)
                .path(path)
                .queryParam("_type", "json")
                .queryParam("srvyYr", srvyYr)
                .queryParam("rspnsMnbdNm", URLEncoder.encode(rspnsMnbdNm, "UTF-8"))
                .queryParam("srvyQitemId", srvyQitemId)
                .queryParam("aiCrtYn", aiCrtYn);

        addQueryParamIfPresent(uriBuilder, "svbnClsfCd01", svbnClsfCd01);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd02", svbnClsfCd02);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd03", svbnClsfCd03);

        return uriBuilder.build(true).toUri();
    }

    public URI buildMcltAoePnlInfoUri(
            String ornuNm,
            String srvyYr,
            String rspnsMnbdNm,
            String srvyQitemId,
            String svbnClsfCd01,
            String svbnClsfCd02,
            String svbnClsfCd03,
            String aiCrtYn
    ) throws UnsupportedEncodingException {
        String path = "/openapi/service/openapi/McltAoePnIinfo"; // TODO: 오아시스랑 같이 수정(대문자 i임...)
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl)
                .path(path)
                .queryParam("_type", "json")
                .queryParam("ornuNm", URLEncoder.encode(ornuNm, "UTF-8"))
                .queryParam("srvyYr", srvyYr)
                .queryParam("rspnsMnbdNm", URLEncoder.encode(rspnsMnbdNm, "UTF-8"))
                .queryParam("srvyQitemId", srvyQitemId)
                .queryParam("aiCrtYn", aiCrtYn);

        addQueryParamIfPresent(uriBuilder, "svbnClsfCd01", svbnClsfCd01);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd02", svbnClsfCd02);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd03", svbnClsfCd03);

        return uriBuilder.build(true).toUri();
    }

    public URI buildKorAoePnlInfoUri(
            String srvyYr,
            String rspnsMnbdNm,
            String srvyQitemId,
            String svbnClsfCd01,
            String svbnClsfCd02,
            String svbnClsfCd03,
            String aiCrtYn
    ) throws UnsupportedEncodingException {
        String path = "/openapi/service/openapi/KorAoePnlInfo";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl)
                .path(path)
                .queryParam("_type", "json")
                .queryParam("srvyYr", srvyYr)
                .queryParam("rspnsMnbdNm", URLEncoder.encode(rspnsMnbdNm, "UTF-8"))
                .queryParam("srvyQitemId", srvyQitemId)
                .queryParam("aiCrtYn", aiCrtYn);

        addQueryParamIfPresent(uriBuilder, "svbnClsfCd01", svbnClsfCd01);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd02", svbnClsfCd02);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd03", svbnClsfCd03);

        return uriBuilder.build(true).toUri();
    }

    public URI buildStdsItrpAoePnlInfoUri(
            String srvyYr,
            String srvyQitemId,
            String svbnClsfCd01,
            String svbnClsfCd02,
            String svbnClsfCd03,
            String aiCrtYn
    ) throws UnsupportedEncodingException {
        String path = "/openapi/service/openapi/StdsItrpAoePnlInfo";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl)
                .path(path)
                .queryParam("_type", "json")
                .queryParam("srvyYr", srvyYr)
                .queryParam("srvyQitemId", srvyQitemId)
                .queryParam("aiCrtYn", aiCrtYn);

        addQueryParamIfPresent(uriBuilder, "svbnClsfCd01", svbnClsfCd01);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd02", svbnClsfCd02);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd03", svbnClsfCd03);

        return uriBuilder.build(true).toUri();
    }

    public URI buildKidAoeHrthAccndInfoUri(
            String srvyYr,
            String rspnsMnbdNm,
            String srvyQitemId,
            String svbnClsfCd01,
            String svbnClsfCd02,
            String svbnClsfCd03,
            String aiCrtYn
    ) throws UnsupportedEncodingException {
        String path = "/openapi/service/openapi/KidAoeHrthAccndInfo";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl)
                .path(path)
                .queryParam("_type", "json")
                .queryParam("srvyYr", srvyYr)
                .queryParam("rspnsMnbdNm", URLEncoder.encode(rspnsMnbdNm, "UTF-8"))
                .queryParam("srvyQitemId", srvyQitemId)
                .queryParam("aiCrtYn", aiCrtYn);

        addQueryParamIfPresent(uriBuilder, "svbnClsfCd01", svbnClsfCd01);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd02", svbnClsfCd02);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd03", svbnClsfCd03);

        return uriBuilder.build(true).toUri();
    }

    public URI buildYtScitEcnmAccndInfoUri(
            String srvyYr,
            String srvyQitemId,
            String svbnClsfCd01,
            String svbnClsfCd02,
            String svbnClsfCd03,
            String aiCrtYn
    ) throws UnsupportedEncodingException {
        String path = "/openapi/service/openapi/YtScitEcnmAccndInfo";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl)
                .path(path)
                .queryParam("_type", "json")
                .queryParam("srvyYr", srvyYr)
                .queryParam("srvyQitemId", srvyQitemId)
                .queryParam("aiCrtYn", aiCrtYn);

        addQueryParamIfPresent(uriBuilder, "svbnClsfCd01", svbnClsfCd01);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd02", svbnClsfCd02);
        addQueryParamIfPresent(uriBuilder, "svbnClsfCd03", svbnClsfCd03);

        return uriBuilder.build(true).toUri();
    }

    private void addQueryParamIfPresent(UriComponentsBuilder builder, String key, String value) {
        if (Objects.nonNull(value) && !value.isEmpty()) {
            builder.queryParam(key, value);
        }
    }
}
