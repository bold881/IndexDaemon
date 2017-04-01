package fulltextsearch.indexmanage;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.rest.RestStatus;

import fulltextsearch.appdaemon.AppConfig;
import fulltextsearch.data.MultiThreadData;
import fulltextsearch.pojos.InterItem;

public class IndexManageWorker implements Runnable {

	public void run() {
		while(true) {
			InterItem interItem = MultiThreadData.dequeueItem();
			
			if(interItem!=null) {
				String itemID = interItem.getIdPdm() + "#" + interItem.getVerPdm();
				IndexResponse response = AppConfig.getEsClient().prepareIndex(
						AppConfig.getEsindexname(), 
						interItem.getItemType().toLowerCase(),
						itemID)
						.setSource(interItem2String(interItem))
						.get();
				RestStatus status = response.status();
				
				if(status.getStatus() == 200 
						||status.getStatus() == 201
						||status.getStatus() == 202) {
					System.out.println("Index created: " + response.getId());
				} else {
					System.out.println("Item " + itemID + ": index failed of status: " + status.getStatus());
				}
			}
			
			try {
				Thread.sleep(AppConfig.getWorkerminisleepduration());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Map<String, Object> interItem2String(InterItem interItem) {
		if(interItem == null) {
			return null;
		}
		
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("idpdm", interItem.getIdPdm());
		json.put("verpdm", interItem.getVerPdm());
		json.put("info", interItem.getInfo());
		json.put("object", interItem.getObject());
		json.put("objectinfo", interItem.getObjectInfo());
		return json;
	}

}
