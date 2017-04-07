package fulltextsearch.indexmanage;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.rest.RestStatus;

import fulltextsearch.appdaemon.AppConfig;
import fulltextsearch.data.MultiThreadData;
import fulltextsearch.pojos.InterItem;

public class IndexManageWorker implements Runnable {

	public void perIndexRun() {
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
	
	public void run() {
		// bulk index
		while(true) {
			
			BulkRequestBuilder bulkRequest = null;
			
			for(int i=0; i<AppConfig.getIndexbulksize(); i++) {
				InterItem interItem = MultiThreadData.dequeueItem();
				if(interItem == null) {
					break;
				}
				
				if(bulkRequest == null) {
					bulkRequest = AppConfig.getEsClient().prepareBulk();
				}
				
				String itemID = interItem.getIdPdm() + "#" + interItem.getVerPdm();
				
				if(interItem.getOp_type() == "OP_DELETE") {
					// delete document from ES by document ID
					bulkRequest.add(AppConfig.getEsClient().prepareDelete(
							AppConfig.getEsindexname(),
							interItem.getItemType().toLowerCase(),
							itemID));
				} else {
					bulkRequest.add(AppConfig.getEsClient().prepareIndex(
							AppConfig.getEsindexname(), 
							interItem.getItemType().toLowerCase(),
							itemID)
							.setSource(interItem2String(interItem)));
				}
			}
			
			if(bulkRequest!=null && bulkRequest.numberOfActions() > 0) {
				BulkResponse bulkResponse = bulkRequest.get();
				if(bulkResponse.hasFailures()) {
					BulkItemResponse[] bulkItemResponses = bulkResponse.getItems();
					for(BulkItemResponse bulkItemResponse: bulkItemResponses) {
						if(bulkItemResponse.isFailed()) {
							System.out.println(bulkItemResponse.getId() 
									+ bulkItemResponse.getOpType() + " Failed");
						}
					}
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
