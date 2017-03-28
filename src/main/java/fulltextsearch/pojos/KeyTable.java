package fulltextsearch.pojos;

import javax.persistence.Column;
import javax.persistence.Table;


@Table(name="SYS_009")
public class KeyTable {
	@Column(name="CKEYSTR", nullable=false)
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
