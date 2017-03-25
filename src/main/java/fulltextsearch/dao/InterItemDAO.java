package fulltextsearch.dao;

import java.util.List;

import fulltextsearch.pojos.InterItem;

public interface InterItemDAO {
	
	public List<InterItem> getLatestInterItem(Long lastIndex);
	
}
