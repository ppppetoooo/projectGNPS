package calories.tracker.app.dto;

import calories.tracker.app.dto.serialization.CustomTimeDeserializer;
import calories.tracker.app.dto.serialization.CustomTimeSerializer;
import calories.tracker.app.model.Firm;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * JSON serializable DTO containing Firm data
 *
 */
public class FirmDTO {

    private Long id;

    private String name;
    private String address;
    private String account_num;
    private String ico;
    private String dic;
    private String ic_dph;

    public FirmDTO() {
    }

    public FirmDTO(Long id, String name, String address, String account_num, String ico, String dic, String ic_dph) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.account_num = account_num;
        this.ico = ico;
        this.dic = dic;
        this.ic_dph = ic_dph;
    }

    public static FirmDTO mapFromFirmEntity(Firm firm) {
        return new FirmDTO(firm.getId(), firm.getName(), firm.getAddress(),
                firm.getAccount_num(), firm.getIco(),firm.getDic(),firm.getIc_dph());
    }

    public static List<FirmDTO> mapFromFirmsEntities(List<Firm> firms) {
        return firms.stream().map((firm) -> mapFromFirmEntity(firm)).collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAccount_num() {
		return account_num;
	}

	public void setAccount_num(String account_num) {
		this.account_num = account_num;
	}

	public String getIco() {
		return ico;
	}

	public void setIco(String ico) {
		this.ico = ico;
	}

	public String getDic() {
		return dic;
	}

	public void setDic(String dic) {
		this.dic = dic;
	}

	public String getIc_dph() {
		return ic_dph;
	}

	public void setIc_dph(String ic_dph) {
		this.ic_dph = ic_dph;
	}

    
}
