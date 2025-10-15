package egovframework.openapi.post;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;


@Setter
@Getter
public class TbDomRegiPrclRecevSendItems {

    private List<TbDomRegiPrclRecevSendItem> item;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
