package fulltextsearch.data;

import java.util.LinkedList;
import java.util.Queue;

import fulltextsearch.pojos.InterItem;

public class MultiThreadData {
	
	// item without doc info
	private static Queue<InterItem> itemRawQueue = null;
	
	// item with doc info 
	private static Queue<InterItem> itemQueue = null;

	public static Queue<InterItem> getItemQueue() {
		if(itemQueue == null) {
			itemQueue = new LinkedList<InterItem>();
		}
		return itemQueue;
	}

	public static Queue<InterItem> getRawItemQueue() {
		if(itemRawQueue == null) {
			itemRawQueue = new LinkedList<InterItem>();
		}
		return itemRawQueue;
	}

	public static synchronized void addRawItem(InterItem interItem) {
		if(itemRawQueue == null) {
			itemRawQueue = new LinkedList<InterItem>();
		}
		
		if(interItem == null) {
			return;
		}
		
		itemRawQueue.add(interItem);
	}
	
	
	public static synchronized InterItem dequeueRawItem() {
		if(itemRawQueue == null) {
			return null;
		}
		
		return itemRawQueue.poll();
	}
	
	public static synchronized void addItem(InterItem interItem) {
		if(itemQueue == null) {
			itemQueue = new LinkedList<InterItem>();
		}
		
		if(interItem == null) {
			return;
		}
		
		itemQueue.add(interItem);
	}
	
	
	public static synchronized InterItem dequeueItem() {
		if(itemQueue == null) {
			return null;
		}
		
		return itemQueue.poll();
	}
	
}
