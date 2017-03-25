package fulltextsearch.pojos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="SYS_009")
public class KeyTable {
	
	@Column(name="CKEYSTR")
	private String key;
	
	@Column(name="ALGORITHM")
	private String algorithm;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
}
