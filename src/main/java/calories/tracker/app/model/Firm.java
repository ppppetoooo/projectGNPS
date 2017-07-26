package calories.tracker.app.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Time;
import java.util.Date;

/**
 *
 * The Firm JPA entity
 *
 */
@Entity
@Table(name = "FIRMS")
public class Firm extends AbstractEntity {

    @ManyToOne
    private User user;
    
    private String name;
    private String address;
    private String account_num;
    private String ico;
    private String dic;
    private String ic_dph;

    public Firm() {

    }

    public Firm(User user, String name, String address, String account_num, String ico, String dic, String ic_dph) {
        this.user = user;
        this.name = name;
        this.address = address;
        this.account_num = account_num;
        this.ico = ico;
        this.dic = dic;
        this.ic_dph = ic_dph;
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

	public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

