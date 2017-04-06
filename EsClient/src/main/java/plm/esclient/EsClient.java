package plm.esclient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.text.Text;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder.Field;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.transport.client.PreBuiltTransportClient;


public class EsClient {
	public EsClient() {};
	
	private static final String localHost = "0.0.0.0";
	
	private static final int esPort = 9300;
	
	private static TransportClient client = null;
	
	private static final String clusterName = "yonyouplm";
	
	private static final String defaultIndex = "plmfulltext";
	
	private static final String defaultQueryField = "_all";
	
	private static int scrollKeepAliveTime = 30000;
	
	private static int scrollSearchPageSize = 100;
	
	private static final String idPdm = "idpdm";
	
	private static final String verPdm = "verpdm";
	
	private static final String object = "object";
	
	private static final String objectInfo = "objectinfo";
	
	private static final String info = "info";
	
	
	@SuppressWarnings({ "resource", "unchecked" })
	private boolean initClient() {
		Settings settings = Settings.builder()
		        .put("cluster.name", clusterName).build();

			if(client == null) {
				try {
					client = new PreBuiltTransportClient(settings)
							.addTransportAddress(new InetSocketTransportAddress(
									InetAddress.getByName(localHost), esPort));
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
			
			return (client == null) ?false:true;
	}
			
	public List<SearchedRet> doSearch(String typeName, String keyWord) {
		
		if(!initClient()) {
			return null;
		}
		
		List<SearchedRet> retList = new ArrayList<SearchedRet>();
		
		SearchResponse scrollResp = null;
		
		if(typeName!=null && !typeName.isEmpty()) {
			// with type specified
			 scrollResp = client.prepareSearch(defaultIndex)	// Index Name
				.setTypes(typeName)		// Type Name
				.setScroll(new TimeValue(scrollKeepAliveTime))		// Scroll Keep Alive Time
				.setQuery(QueryBuilders.multiMatchQuery(keyWord, info, objectInfo))		//
				.highlighter(new HighlightBuilder()
						.field(new Field(info))
						.field(new Field(objectInfo)))
				.setFetchSource(null, new String[]{objectInfo, info})
				.setSize(scrollSearchPageSize)
				.get();
		} else {
			scrollResp = client.prepareSearch(defaultIndex)
					.setScroll(new TimeValue(scrollKeepAliveTime))
					.setQuery(QueryBuilders.matchQuery(defaultQueryField, keyWord))
					.highlighter(new HighlightBuilder()
							.field(new Field(info))
							.field(new Field(objectInfo)))
					.setFetchSource(null, new String[]{objectInfo, info})
					.setSize(scrollSearchPageSize)
					.get();
		}
		
		do {
			for(SearchHit hit : scrollResp.getHits().getHits()) {
				SearchedRet searchedRet = new SearchedRet();				
				// score
				searchedRet.setScore(hit.getScore());
				// item type
				searchedRet.setItemType(hit.getType());
				// query string 
				searchedRet.setQueryTerm(keyWord);
				
				Map<String, Object> mapSources = hit.getSource();
				// idPdm
				searchedRet.setIdPdm((String)mapSources.get(idPdm));
				// verPdm
				searchedRet.setVerPdm((String)mapSources.get(verPdm));
				// object
				searchedRet.setObject((String)mapSources.get(object));
				
				String hintHighlight = "";
				Map<String, HighlightField> mapHights = hit.getHighlightFields();
				for(Map.Entry<String, HighlightField> entry: mapHights.entrySet()) {
					Text[] fragments = entry.getValue().getFragments();
					for(Text frag : fragments) {
						hintHighlight += frag.toString();
					}
				}
				// matched info 
				searchedRet.setMatchedInfo(hintHighlight);
				
				
				retList.add(searchedRet);
			}
			scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
					.setScroll(new TimeValue(scrollKeepAliveTime))
					.execute()
					.actionGet();
			
		} while(scrollResp.getHits().getHits().length != 0);
		
		
		closeClient();
		
		return retList;
	}
	
	public void closeClient() {
		if(client != null) {
			client.close();
			client = null;
		}
	}
}
