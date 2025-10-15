package egovframework.openapi.post;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Setter
@Getter
public class TbDomRegiPrclRecevSendBody {
    private TbDomRegiPrclRecevSendItems items;
    private int numOfRows;
    private int pageNo;
    private int totalCount;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
