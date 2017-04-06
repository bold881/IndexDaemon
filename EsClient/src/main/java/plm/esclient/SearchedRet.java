package plm.esclient;

public class SearchedRet {
	
	private float score; // 搜索结果评分
	
	private String idPdm; // 在PDM中的ID
	
	private String verPdm; // 在PDM中的版本，若版本存在的时候
	
	private String object;	// 对象关联文档（可能存在多个文档文档编码）
	
	private String itemType; // 对象类型（文档、物料、设计BOM、项目、变更申请）

	private String matchedInfo;	// 匹配的文本内容
	
	private String queryTerm;	// 搜索的关键字
	
	public SearchedRet() {};
	
	public SearchedRet(Float score, String idPdm, String verPdm, String object, String itemType, String matchedInfo,
			String queryTerm) {
		super();
		this.score = score;
		this.idPdm = idPdm;
		this.verPdm = verPdm;
		this.object = object;
		this.itemType = itemType;
		this.matchedInfo = matchedInfo;
		this.queryTerm = queryTerm;
	}

	public double getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
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

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getMatchedInfo() {
		return matchedInfo;
	}

	public void setMatchedInfo(String matchedInfo) {
		this.matchedInfo = matchedInfo;
	}

	public String getQueryTerm() {
		return queryTerm;
	}
	
	public void setQueryTerm(String queryTerm) {
		this.queryTerm = queryTerm;
	}
}
