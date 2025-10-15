package egovframework.openapi.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller(value = "/api")
public class ApiController {

    @Autowired
    private TbDomRegiPrclRecevSendService tbDomRegiPrclRecevSendService;

    // 국내 창구소포 물류이동
    @RequestMapping(value = "getTbDomRegiPrclRecevSendList", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getTbDomRegiPrclRecevSendList(@RequestParam int pageNo, @RequestParam int numOfRows,
                                                             @RequestParam String rcptPstofcSeCd, @RequestParam String delivPstofcSeCd, @RequestParam String rcptYmdStt,
                                                             @RequestParam String rcptYmdEnd) throws Exception {

        Map<String, Object> resultMap = new HashMap<String, Object>();

        tbDomRegiPrclRecevSendService.getSearchData(resultMap, rcptPstofcSeCd, delivPstofcSeCd, rcptYmdStt, rcptYmdEnd,
                pageNo, numOfRows);

        return resultMap;
    }
}
