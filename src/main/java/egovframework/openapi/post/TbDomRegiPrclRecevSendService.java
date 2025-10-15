package egovframework.openapi.post;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class TbDomRegiPrclRecevSendService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TbDomRegiPrclRecevSendService.class);

    @Resource
    private HttpUtil httpUtil;

    public void getSearchData(Map<String, Object> resultMap,
                              String rcptPstofcSeCd, String delivPstofcSeCd, String rcptYmdStt,
                              String rcptYmdEnd, int pageNo, int numOfRows) throws Exception {

        String type = "json";

        StringBuilder errorHandler = new StringBuilder();

        TbDomRegiPrclRecevSendBody response = searchData(type, rcptPstofcSeCd,
                delivPstofcSeCd, rcptYmdStt, rcptYmdEnd, pageNo, numOfRows, errorHandler);

        if (response == null) {
            LOGGER.info("No response received from searchData");
            resultMap.put("result", "fail");
            resultMap.put("response", new TbDomRegiPrclRecevSendBody());
        } else if (response.getItems() == null
                || response.getItems().getItem() == null
                || response.getItems().getItem().isEmpty()) {
            LOGGER.info("No data found for the given parameters.");
            resultMap.put("result", "noData");
            resultMap.put("response", response);
        } else {
            LOGGER.info("Data found and successfully parsed.");
            resultMap.put("response", response);
            resultMap.put("result", "success");
        }

    }

    public TbDomRegiPrclRecevSendBody searchData(String type, String rcptPstofcSeCd,
                                                 String delivPstofcSeCd, String rcptYmdStt, String rcptYmdEnd,
                                                 int pageNo, int numOfRows, StringBuilder errorHandler) throws Exception {

        TbDomRegiPrclRecevSendBody response = new TbDomRegiPrclRecevSendBody();

        try {
            String key = "domRegiPrclRecevSend";
            String queryParams = "_type=" + type
                    + "&numOfRows=" + numOfRows
                    + "&pageNo=" + pageNo
                    + "&rcptPstofcSeCd=" + URLEncoder.encode(rcptPstofcSeCd, "UTF-8")
                    + "&delivPstofcSeCd=" + URLEncoder.encode(delivPstofcSeCd, "UTF-8")
                    + "&rcptYmdStt=" + URLEncoder.encode(rcptYmdStt, "UTF-8")
                    + "&rcptYmdEnd=" + URLEncoder.encode(rcptYmdEnd, "UTF-8");

            LOGGER.info("Sending HTTP request with key: {}", key);

            String jsonData = httpUtil.sendHttpRequestByKey(key, queryParams);

            if (jsonData == null || jsonData.isEmpty()) {
                return null;
            }

            LOGGER.debug("Received response data: {}", jsonData);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode bodyNode = rootNode.path("response").path("body");
            JsonNode itemsNode = bodyNode.path("items");
            JsonNode itemNode = itemsNode.path("item");
            JsonNode numOfRowsNode = bodyNode.path("numOfRows");
            JsonNode pageNoNode = bodyNode.path("pageNo");
            JsonNode totalCountNode = bodyNode.path("totalCount");

            response.setTotalCount(totalCountNode.asInt());
            response.setNumOfRows(numOfRowsNode.asInt());
            response.setPageNo(pageNoNode.asInt());

            if (itemNode.isArray()) {
                List<TbDomRegiPrclRecevSendItem> itemList = new ArrayList<TbDomRegiPrclRecevSendItem>();
                for (JsonNode node : itemNode) {
                    TbDomRegiPrclRecevSendItem item = objectMapper.treeToValue(node, TbDomRegiPrclRecevSendItem.class);
                    itemList.add(item);
                }
                TbDomRegiPrclRecevSendItems items = new TbDomRegiPrclRecevSendItems();
                items.setItem(itemList);
                response.setItems(items);
            } else if (itemNode.isObject()) {
                TbDomRegiPrclRecevSendItem item = objectMapper.treeToValue(itemNode, TbDomRegiPrclRecevSendItem.class);

                List<TbDomRegiPrclRecevSendItem> itemList = new ArrayList<TbDomRegiPrclRecevSendItem>();
                itemList.add(item);

                TbDomRegiPrclRecevSendItems items = new TbDomRegiPrclRecevSendItems();
                items.setItem(itemList);
                response.setItems(items);
            }

            LOGGER.info("Parsed response: {}", response);

        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Error while encoding URL parameters for the request: {}", e.getMessage(), e);
            errorHandler.append("10");
        } catch (MalformedURLException e) {
            LOGGER.error("Malformed URL encountered: {}", e.getMessage(), e);
            errorHandler.append("10");
        } catch (IOException e) {
            LOGGER.error("I/O error during the HTTP request: {}", e.getMessage(), e);
            errorHandler.append("10");
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid argument provided: {}", e.getMessage(), e);
            errorHandler.append("10");
        }

        return response;
    }

}
