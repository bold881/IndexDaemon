package fulltextsearch.dao;

import java.util.List;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import fulltextsearch.appdaemon.AppConfig;
import fulltextsearch.pojos.InterItem;

public class InterItemDAOImpl implements InterItemDAO {

	@SuppressWarnings("unchecked")
	public List<InterItem> getLatestInterItem(Long lastIndex) {
		List<InterItem> interItems = null;
		
		Session session = AppConfig.getSessionFactory().openSession();
		Transaction tx = null;
		try {
		    tx = session.beginTransaction();
		    @SuppressWarnings("rawtypes")
			Query query = session.createQuery("FROM InterItem I where I.id > :id order by I.id");
		    query.setParameter("id", lastIndex);
		    query.setMaxResults(AppConfig.getMaxResultCount());
		    
		    interItems = query.getResultList();
		    tx.commit();
		}
		catch (Exception e) {
		    if (tx!=null) tx.rollback();
		    e.printStackTrace();
		}
		finally {
		    session.close();
		}
		return interItems;
	}

}
