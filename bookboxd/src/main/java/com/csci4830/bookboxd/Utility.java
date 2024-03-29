package com.csci4830.bookboxd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.LockTimeoutException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.QueryTimeoutException;
import javax.persistence.TransactionRequiredException;

import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import com.csci4830.datamodel.User;
import com.csci4830.datamodel.Books;
import com.csci4830.datamodel.Friends;
import com.csci4830.datamodel.List_Books;
import com.csci4830.datamodel.Lists;
import com.csci4830.datamodel.Reviews;

public class Utility {
	static SessionFactory sessionFactory = null;

	/**
	 * Returns the SessionFactory object for this Hibernate configuration.
	 * 
	 * @return The SessionFactory object.
	 */
	public static SessionFactory getSessionFactory() {
		if (sessionFactory != null) {
			return sessionFactory;
		}

		// New versions of Hibernate don't like the code given in class and is
		// considered deprecated.
		// Source for this code:
		// https://stackoverflow.com/questions/33005348/hibernate-5-org-hibernate-mappingexception-unknown-entity

		StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
		Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();

		return metadata.getSessionFactoryBuilder().build();
	}
	
	/**
	 * A generic method for running SQL queries that return lists.
	 * @param <T> The generic type
	 * @param sqlQuery The Hibernate compatible query you would like to use
	 * @param clazz The class you want your data casted to.
	 * @return A generic list with the objects requested inside.
	 */
	public static <T> List<T> getDataList(String sqlQuery, Class<T> clazz) {
		List<T> resultList = new ArrayList<T>();
		
		Session session = getSessionFactory().openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			List<?> objects = session.createQuery(sqlQuery).list();
			for (Iterator<?> iterator = objects.iterator(); iterator.hasNext();) {
				T object = clazz.cast(iterator.next());
				resultList.add(object);
			}
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		}
		catch (ClassCastException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();	
			session.getSessionFactory().close();
		}
				
		return resultList;
	}
	
	/**
	 * A generic method for running SQL queries that return single objects.
	 * @param <T> The generic type
	 * @param sqlQuery The Hibernate compatible query you would like to run.
	 * @param clazz The class you want the data casted to.
	 * @return The object that is requested. If an error occurs, null is returned.
	 */
	public static <T> T getDataObject(String sqlQuery, Class<T> clazz) {
		T result = null;

		Session session = getSessionFactory().openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			result = clazz.cast(session.createQuery(sqlQuery).getSingleResult());
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} catch (NoResultException e) {
			if (tx != null)
				tx.rollback();
			System.err.printf("ERR: no result found for query: %s\n", sqlQuery);
			e.printStackTrace();
			//needs to throw the exception so that servlets can catch it and report the error to the view
			throw e;
		} catch (NonUniqueResultException e) {
			if (tx != null)
				tx.rollback();
			System.err.printf("ERR: more than one result found for query: %s\n", sqlQuery);
			e.printStackTrace();
		} catch (IllegalStateException e) {
			if (tx != null)
				tx.rollback();
			System.err.printf("ERR: more than one result found for query: %s\n", sqlQuery);
			e.printStackTrace();
		} catch (QueryTimeoutException e) {
			if (tx != null)
				tx.rollback();
			System.err.printf("ERR: the query timed out for query. Only the query was rolled back. query: %s\n", sqlQuery);
			e.printStackTrace();
		} catch (TransactionRequiredException e) {
			if (tx != null)
				tx.rollback();
			System.err.printf("ERR: This should never happen... query: %s\n", sqlQuery);
			e.printStackTrace();
		} catch (PessimisticLockException e) {
			if (tx != null)
				tx.rollback();
			System.err.printf("ERR: Pessimistic locking failed... transaction rolled back. query: %s\n", sqlQuery);
			e.printStackTrace();
		} catch (LockTimeoutException e) {
			if (tx != null)
				tx.rollback();
			System.err.printf("ERR: Pessimistic locking failed... query rolled back. query: %s\n", sqlQuery);
			e.printStackTrace();
		} catch (PersistenceException e) {
			if (tx != null)
				tx.rollback();
			System.err.printf("ERR: the query timed out for query. The transaction was rolled back. query: %s\n", sqlQuery);
			e.printStackTrace();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			System.err.printf("ERR: some other exception happened... see stack trace.");
			e.printStackTrace();
		}
		finally {
			session.close();
			session.getSessionFactory().close();
		}

		return result;
	}
	
	/**
	 * A generic method for deleting objects from the database.
	 * @param <T> Generic type
	 * @param object The object you would like deleted.
	 * @return The object that was deleted if successful, otherwise null.
	 */
	public static <T> T deleteDataObject(T object) {
		Session session = getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.delete(object);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
			return null;
		} finally {			
			session.close();
			session.getSessionFactory().close();
		}
			
		return object;
	}
	
	/**
	 * A generic method for saving objects from the database.
	 * @param <T> Generic type
	 * @param object The object you would like to be saved to the DB.
	 * @return The object that was deleted if successful, otherwise null.
	 */
	public static <T> T saveDataObject(T object) {
		Session session = getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.save(object);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
			return null;
		} finally {
			session.close();	
			session.getSessionFactory().close();
		}
				
		return object;
	}
	
	
	/**
	 * Returns a list of all users in the database.
	 * 
	 * @return A List containing all Users.
	 */
	public static List<User> getUsers() {
		return (List<User>) getDataList("FROM User", User.class);
	}
	
	
	/**
	 * Returns a User object of the user with the corresponding user_id. If an 
	 * error occurs while retrieving the object, null is returned.
	 * 
	 * @param user_id The ID of the user
	 * @return The User object that was found. null if there is an error.
	 */
	public static User getUserByUserID(Integer user_id) {
		return (User) getDataObject("FROM User WHERE user_id = " + user_id, User.class);
	}

	/**
	 * Returns a User object of the user with the corresponding username. If an 
	 * error occurs while retrieving the object, null is returned.
	 * 
	 * @param username The username of the User
	 * @return The User object that was found. null if there is an error.
	 */
	public static User getUserByUsername(String username) {
		return (User) getDataObject("FROM User WHERE username = '" + username + "'", User.class);
	}
	
	/**
	 * Returns a pending Friend Request if it exists.
	 * 
	 * @param user1 The first user ID of the User
	 * @param user2 The second user ID of the User
	 * @return The Friends object if a pending request was found, null if not found.
	 */
	public static Friends getPendingFriendRequest(int user1, int user2) {
		try {
			return (Friends) getDataObject("FROM Friends WHERE (user_id_1 = " + user1 +
					" AND user_id_2 = " + user2 + ") OR (user_id_1 = " + user2 +
					" AND user_id_2 = " + user1 + ") AND confirmed = 0", Friends.class);			
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Returns a list of a user's friends.
	 * 
	 * @param user_id The ID of the user
	 * @return A list of user IDs
	 */
	public static List<Friends> getFriendsByUserID(Integer user_id) {
		//ensure that this only get friends if they're confirmed (confirmed column = 1)
		return (List<Friends>) getDataList("FROM Friends WHERE confirmed = 1 AND (user_id_1 = " + user_id 
				+ " OR user_id_2 = " + user_id + ")", Friends.class);
	}

	/**
	 * Returns a list of all books from the database.
	 * 
	 * @return A List of all Books.
	 */
	public static List<Books> getBooks() {
		return (List<Books>) getDataList("FROM Books", Books.class);
	}

	/**
	 * Returns a Books object of the book with the corresponding id. If an 
	 * error occurs while retrieving the object, null is returned.
	 * 
	 * @param id The ID of the book
	 * @return The Books object that was found. null if there is an error.
	 */
	public static Books getBookByBookID(Integer id) {
		return (Books) getDataObject("FROM Books WHERE book_id = '" + id + "'", Books.class);
	}

	/**
	 * Returns a Books object of the book with the corresponding name. If an 
	 * error occurs while retrieving the object, null is returned.
	 * 
	 * @param name The exact name of the book
	 * @return The Books object that was found. null if there is an error.
	 */
	public static Books getBookByName(String name) {
		return (Books) getDataObject("FROM Books WHERE book_name = '" + name + "'", Books.class);
	}
		
	/**
	 * Returns a List of Books with the corresponding author.
	 * @param name The exact name of the author.
	 * @return The List of books that were found.
	 */
	public static List<Books> getBooksByAuthor(String name) {
		return (List<Books>) getDataList("FROM Books WHERE author = '" + name + "'", Books.class);
	}

	/**
	 * Returns a List of Books with the corresponding genre.
	 * @param name The exact name of the genre.
	 * @return The List of books that were found.
	 */
	public static List<Books> getBooksByGenre(String genre) {
		return (List<Books>) getDataList("FROM Books WHERE genre = '" + genre + "'", Books.class);
	}
		
	/**
	 * Returns a List of Books that match the title search query. Uses the LIKE
	 * SQL operator.
	 * @param search The search query for the book.
	 * @return The List of books that were found.
	 */
	public static List<Books> getBooksByNameSearch(String search) {
		return (List<Books>) getDataList("FROM Books WHERE book_name LIKE '%" + search + "%'", Books.class);
	}
	
	/**
	 * Returns a List of User that match the title search query. Uses the LIKE
	 * SQL operator.
	 * @param search The search query for the book.
	 * @return The List of books that were found.
	 */
	public static List<User> getUsersByNameSearch(String search) {
		return (List<User>) getDataList("FROM User WHERE username LIKE '%" + search + "%'", User.class);
	}
	
	/**
	 * Returns a List of Books that match the author search query. Uses the LIKE
	 * SQL operator.
	 * @param search The search query for the book.
	 * @return The List of books that were found.
	 */
	public static List<Books> getBooksByAuthorSearch(String search) {
		return (List<Books>) getDataList("FROM Books WHERE author LIKE '%" + search + "%'", Books.class);
	}
		
	/**
	 * Performs a search for books between the min and max ratings. Returns the
	 * results in a List.
	 * @param min The minimum rating inclusive
	 * @param max The maximum rating inclusive
	 * @return The results found.
	 */
	public static List<Books> getBooksByAverageRating(Integer min, Integer max) {
		return (List<Books>) getDataList("FROM Books WHERE average_rating BETWEEN " + min + " AND " + max, Books.class);
	}
		
	/**
	 * Retrieves a list of books referenced by a list ID.
	 * @param id The ID of the list.
	 * @return The results found.
	 */
	public static List<Books> getBooksByListID(Integer id) {
		List<List_Books> lb = getDataList("FROM List_Books WHERE list_id = " + id, List_Books.class);
		List<Books> books = new ArrayList<Books>();
		
		for (List_Books l : lb) {
			books.add((Books) getDataObject("FROM Books WHERE book_id = " + l.getBook_id(), Books.class));
		}
		
		return books;
	}
	
	/**
	 * Returns a list of all lists in the database.
	 * 
	 * @return A List containing all Lists.
	 */
	public static List<Lists> getLists() {
		return (List<Lists>) getDataList("FROM Lists", Lists.class);
	}
		
	/**
	 * Retrieves a list of lists referenced by a user ID.
	 * @param id The ID of the list.
	 * @return The results found.
	 */
	public static List<Lists> getListsByUserID(Integer id) {
		return (List<Lists>) getDataList("from Lists WHERE user_id = '" + id + "'", Lists.class);
	}
	
	/**
	 * Returns a Lists object of the list with the corresponding list_id. If an 
	 * error occurs while retrieving the object, null is returned.
	 * 
	 * @param list_id The ID of the list
	 * @return The Lists object that was found. null if there is an error
	 */
	public static Lists getListByID(Integer list_id) {
		return (Lists) getDataObject("FROM Lists WHERE list_id = '" + list_id + "'", Lists.class);
	}
	
	/**
	 * TODO: get all lists from a user that are marked as public
	 * @param user_id
	 * @return
	 */
	public static List<Lists> getPublicListsByUserID(Integer user_id) {
		return (List<Lists>) getDataList("FROM Lists WHERE privacy_setting = 0 AND user_id = " + user_id, Lists.class);
	}
	
	/**
	 * Returns a list of all lists in the database.
	 * 
	 * @return A List containing all Lists.
	 */
	public static List<Reviews> getReviews() {
		return (List<Reviews>) getDataList("FROM Reviews", Reviews.class);
	}
		
	/**
	 * Returns a Reviews object of the review with the corresponding review_id. If an 
	 * error occurs while retrieving the object, null is returned.
	 * 
	 * @param review_id The ID of the review
	 * @return The Reviews object that was found. null if there is an error
	 */
	public static Reviews getReviewByReviewID(Integer review_id) {
		return (Reviews) getDataObject("FROM Reviews WHERE review_id = '" + review_id + "'", Reviews.class);
	}
		
	/**
	 * Returns a List of Reviews with the corresponding user ID.
	 * @param user_id The user ID.
	 * @return The List of Reviews that were found.
	 */
	public static List<Reviews> getReviewsByUserID(Integer userID) {
		return (List<Reviews>) getDataList("FROM Reviews WHERE user_id = '" + userID + "'", Reviews.class);
	}
	
	/**
	 * Returns a List of Reviews with the corresponding book ID.
	 * @param book_id The book ID.
	 * @return The List of Reviews that were found.
	 */
	public static List<Reviews> getReviewsByBookID(Integer bookID) {
		return (List<Reviews>) getDataList("FROM Reviews WHERE book_id = '" + bookID + "'", Reviews.class);
	}
	
	/**
	 * Checks the login username and password in the database.
	 * 
	 * @param username The username to check
	 * @param password The password to check
	 * @return A User object if the username and password matches
	 */
	public static User checkLogin(String username, String password) {
		return (User) getDataObject("FROM User WHERE username = '" + username + "' AND password = '" + encryptSHA1(password) + "'", User.class);
	}
	
	/**
	 * Adds a new user to the system. This does not add the default lists for 
	 * the newly created user.
	 * @param username The username
	 * @param password The password
	 * @return The user object of the newly created User.
	 */
	public static User createUser(String username, String password) {
		Session session = getSessionFactory().openSession();
		Transaction tx = null;
		User output = null;
		try {
			tx = session.beginTransaction();
			output = new User(username, password);
			session.save(output);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
			throw e;
		} finally {			
			session.close();
			session.getSessionFactory().close();
		}
		
		if (output != null) {
			createDefaultUserLists(output);
		}
		
		
		return output;
	}
	
	/**
	 * Creates the default lists for a newly created user.
	 * @param user A newly created User object
	 * @return A List of the Lists that were created.
	 */
	public static List<Lists> createDefaultUserLists(User user) {
		Session session = getSessionFactory().openSession();
		Transaction tx = null;
		List<Lists> listsCreated = new ArrayList<Lists>();
		
		try {
			tx = session.beginTransaction();
			String[] listNames = {"Favorites","To Read","Finished","Reviewed"};
			for (String string : listNames) {
				Lists l = new Lists(user.getUser_id(), string, 0);
				session.save(l);
				listsCreated.add(l);
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {			
			session.close();
			session.getSessionFactory().close();
		}
		
		return listsCreated;
	}
	
	/**
	 * create a new custom list
	 * @param user_id The user creating the list
	 * @param name List name
	 * @param privacy_setting The user set privacy setting
	 * @return The new list
	 */
	public static Lists createCustomList(Integer user_id, String name, Integer privacy_setting) {
		Lists newList = null;
		Session session = getSessionFactory().openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			newList = new Lists(user_id, name, privacy_setting);
			session.save(newList);
			
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
			session.getSessionFactory().close();
		}
		
		return newList;
	}
	
	public static Lists deleteCustomList(Lists lists) {
		Lists result = null;
		Session session = getSessionFactory().openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			result = (Lists) session.get(Lists.class,  lists.getList_id());
			session.delete(result);
			
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
			session.getSessionFactory().close();
		}
		
		return result;
	}
	
	public static String encryptSHA1(String input)
    {
		return DigestUtils.sha1Hex(input);
    }


	public static Books createBook(String url, String name, String author, String genre, String description) {
	    Session session = getSessionFactory().openSession();
	    Transaction tx = null;
	    Books output = null;
	    try {
	        tx = session.beginTransaction();
	        output = new Books(url, name, author, genre, description);
	        session.save(output);
	        tx.commit();
	    } catch (HibernateException e) {
	        if (tx != null) {
	            tx.rollback();
	        }
	        e.printStackTrace();
	    } finally {
	    	session.close();
			session.getSessionFactory().close();
	    }
	    
	    return output;
	}

	public static Books deleteBook(Books book) {
	
	    Session session = getSessionFactory().openSession();
	    Transaction tx = null;
	    try {
	        tx = session.beginTransaction();
	        session.delete(book);
	        tx.commit();
	    } catch (HibernateException e) {
	        if (tx != null) {
	            tx.rollback();
	        }
	        e.printStackTrace();
	    } finally {
	    	session.close();
			session.getSessionFactory().close();
	    }

	    return book;
	}
	
	public static Double getAverageReview(Integer book_id) {
		Double avg = 0.0;
		Session session = getSessionFactory().openSession();
	    Transaction tx = null;
	    
	    try {
	        tx = session.beginTransaction();
	        avg = (Double) session.createQuery("select avg(rating) from Reviews where book_id = " + book_id + " group by book_id").getSingleResult();
	        tx.commit();
	    } catch (HibernateException e) {
	        if (tx != null) {
	            tx.rollback();
	        }
	        e.printStackTrace();
	    } finally {
	    	session.close();
			session.getSessionFactory().close();
	    }

	    return avg;
	}
}
