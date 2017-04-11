package fulltextsearch.dao;

public class DBHelperInterItem extends DBHelper {
	public void resetInterIdentity() {
		String querySQL = "DBCC CHECKIDENT (fulltext_intertable, RESEED, 0)";
		doUpdate(querySQL);
		}
}
