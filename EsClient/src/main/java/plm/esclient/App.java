package plm.esclient;

import java.util.List;

public class App 
{

		
    public static void main( String[] args )
    {
    	EsClient esClient = new EsClient();
    	List<SearchedRet> lstRets = esClient.doSearch("mat", "攀");
    	for(SearchedRet lstRet : lstRets) {
    		System.out.println(lstRet.getMatchedInfo());
    	}
    }
}
