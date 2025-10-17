package egovframework.openapi.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;


@Component
public class HttpUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);
    private final Map<String, URL> urlMap = new HashMap<String, URL>();

    @Value("${baseUrl}")
    private String baseUrl;

    public static String sendHttpRequest(String targetURL) throws Exception {
        String jsonData = "";
        try {
            URL url = new URL(targetURL);
            if (url.getProtocol().equals("https")) {
                LOGGER.info("Sending HTTPS request to {}", targetURL);
                jsonData = HttpUtil.getRequest(targetURL, true);
            } else if (url.getProtocol().equals("http")) {
                LOGGER.info("Sending HTTP request to {}", targetURL);
                jsonData = HttpUtil.getRequest(targetURL, false);
            }
        } catch (MalformedURLException e) {
            LOGGER.error("Malformed URL encountered: {}", targetURL, e);
        } catch (IOException e) {
            LOGGER.error("IOException during HTTP request to {}: {}", targetURL, e.getMessage(), e);
        }
        return jsonData;
    }

    public static String getRequest(String targetUrl, boolean isHttps)
            throws Exception {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();

        try {
            // HTTPS 요청이면 SSL 인증서 검증 우회
            URL url = new URL(targetUrl);
            if (isHttps) {
                LOGGER.info("Ignoring SSL certificate verification for {}", targetUrl);
                ignoreSsl();
                connection = (HttpsURLConnection) url.openConnection();
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(55000); // 연결 타임아웃 55초
            connection.setReadTimeout(50000); // 읽기 타임아웃 50초

            int responseCode = connection.getResponseCode();
            LOGGER.debug("HTTP response code: {}", responseCode);
            reader = new BufferedReader(new InputStreamReader(
                    responseCode != HttpURLConnection.HTTP_OK ? connection
                            .getErrorStream() : connection.getInputStream(), StandardCharsets.UTF_8));

            String inputLine;
            inputLine = reader.readLine();
            while (inputLine != null) {
                response.append(inputLine);
                inputLine = reader.readLine();
            }
        } catch (IOException e) {
            LOGGER.error("IOException during HTTP request to {}: {}", targetUrl, e.getMessage(), e);
            throw new IOException("Error during HTTP request: " + e.getMessage(), e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                LOGGER.warn("Error closing reader: {}", e.getMessage());
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        LOGGER.debug("Response data: {}", response);

        return response.toString();
    }

    public static void ignoreSsl() throws Exception {
        HostnameVerifier hv = new HostnameVerifier() {

            @Override
            public boolean verify(String urlHostName, SSLSession session) {
                // TODO Auto-generated method stub
                return true;
            }
        };
        trustAllHttpsCertificates();
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

    private static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    @PostConstruct
    public void init() {
        try {
            urlMap.put("domRegiPrclRecevSend", new URL(this.baseUrl + "/domRegiPrclRecevSend"));
            urlMap.put("poInfo", new URL(this.baseUrl + "/poInfo"));
            urlMap.put("poPrclSend", new URL(this.baseUrl + "/poPrclSend"));
            urlMap.put("poShopOrd", new URL(this.baseUrl + "/poShopOrd"));
            urlMap.put("shopOrdGoods", new URL(this.baseUrl + "/shopOrdGoods"));
        } catch (MalformedURLException e) {
            LOGGER.error("Error initializing URLs in HttpUtil: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Invalid base URL format", e);
        }
    }

    public String sendHttpRequestByKey(String key, String queryParams) throws Exception {
        try {
            URL baseUrl = urlMap.get(key);
            if (baseUrl == null) {
                throw new IllegalArgumentException("Invalid key: " + key);
            }

            String targetURL = baseUrl + "?" + queryParams;
            LOGGER.info("Requesting [{}] -> {}", key, targetURL);

            return sendHttpRequest(targetURL);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid argument provided: {}", e.getMessage(), e);
            return "";
        }
    }

    public static class miTM implements TrustManager, X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType)
                throws CertificateException {
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType)
                throws CertificateException {
        }
    }
}
