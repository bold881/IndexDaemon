package fulltextsearch.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import fulltextsearch.appdaemon.AppConfig;

public class TableContentDAOKeyTableImpl<E> implements TableContentDAO<E> {
	
	private final String algorithm = "AES";

	@SuppressWarnings("unchecked")
	public List<E> getTableContent() {
		List<E> lstKeys = null;
		
		Session session = AppConfig.getSessionFactory().openSession();
		Transaction tx = null;
		try {
		    tx = session.beginTransaction();
		    @SuppressWarnings("rawtypes")
			Query query = session.createQuery("FROM KeyTable K where K.algorithm = :algorithm");
		    query.setParameter(":algorithm", algorithm);
		    query.setMaxResults(AppConfig.getMaxResultCount());
		    
		    lstKeys = query.getResultList();
		    tx.commit();
		}
		catch (Exception e) {
		    if (tx!=null) tx.rollback();
		    e.printStackTrace();
		}
		finally {
		    session.close();
		}
		return lstKeys;
	}
}
