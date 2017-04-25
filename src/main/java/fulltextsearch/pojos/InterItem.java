package fulltextsearch.pojos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="fulltext_intertable")
public class InterItem {
	
	@Id @GeneratedValue
	@Column(name="id")
	private Long id;
	
	@Column(name="idpdm")
	private String idPdm;
	
	@Column(name="verpdm")
	private String verPdm;
	
	@Column(name="itemtype")
	private String itemType;
	
	@Column(name="info")
	private String info;
	
	@Column(name="object")
	private String object;
	
	@Column(name="op_type")
	private String op_type;
	
	@Column(name="docformat")
	private String docformat;
	
	@Transient
	private String objectInfo;
	
	public String getDocformat() {
		return docformat;
	}

	public void setDocformat(String docformat) {
		this.docformat = docformat;
	}

	public String getObjectInfo() {
		return objectInfo;
	}
	
	public void setObjectInfo(String objectInfo) {
		this.objectInfo += objectInfo;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIdPdm() {
		return idPdm;
	}

	public void setIdPdm(String idPdm) {
		this.idPdm = idPdm;
	}

	public String getVerPdm() {
		return verPdm;
	}

	public void setVerPdm(String verPdm) {
		this.verPdm = verPdm;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getOp_type() {
		return op_type;
	}

	public void setOp_type(String op_type) {
		this.op_type = op_type;
	}
}
