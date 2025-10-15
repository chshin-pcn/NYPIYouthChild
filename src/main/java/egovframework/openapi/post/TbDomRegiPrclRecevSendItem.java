package egovframework.openapi.post;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbDomRegiPrclRecevSendItem {
    private String addrseAddr;
    private String delivPstofcSeCd;
    private String delivYmd;
    private String domPopdSeNm;
    private String mngSn;
    private String popdCn;
    private String popdVolm;
    private String popdWght;
    private String pstofcRcptNo;
    private String rcptHcnt;
    private String rcptPstofcSeCd;
    private String rcptYmd;
    private String regDt;
    private String sndrAddr;
    private String tmtlSpdSeNm;
    private String sndrZip;
    private String addrseZip;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}