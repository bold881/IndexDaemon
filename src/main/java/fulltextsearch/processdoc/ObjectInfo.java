package fulltextsearch.processdoc;

import java.util.ArrayList;
import java.util.List;

import fulltextsearch.pojos.ObjectItem;

public class ObjectInfo {
	
	private static String ECRTAGPRE = "[ECR*]";
	
	private static String ECRTAGSUF = "[ECR#]";
	

	public static List<ObjectItem> getObjects(String info) {
		List<ObjectItem> lstObjects = new ArrayList<ObjectItem>();
		
    	int pos = info.indexOf(ECRTAGPRE);
    	while(pos!=-1)
    	{
    		lstObjects.add(getObjectInfo((String) info.subSequence(0, pos)));
    		info = info.substring(pos+ECRTAGPRE.length());
    		pos = info.indexOf(ECRTAGPRE);
    	}
    	if(!info.isEmpty()) {
    		lstObjects.add(getObjectInfo(info));
    	}
    	
    	return lstObjects;
	}
	
	private static ObjectItem getObjectInfo(String objectInfo) {
		ObjectItem object = new ObjectItem();
		int pos = objectInfo.indexOf(ECRTAGSUF);
		
		if(pos!=-1) {
			object.setId((String) objectInfo.subSequence(0, pos));
			objectInfo = objectInfo.substring(pos+ECRTAGSUF.length());
			pos = objectInfo.indexOf(ECRTAGSUF);
			if(pos!=-1) {
				object.setVer((String) objectInfo.subSequence(0, pos));
				object.setFormat(objectInfo.substring(pos+ECRTAGSUF.length()));
			}
		}
		
		return object;
	}
}
