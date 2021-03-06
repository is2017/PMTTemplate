package daoimpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dao.UserProfileDao;
import db.DBConnector;
import models.UserProfile;

public class UserProfileDaoImpl implements UserProfileDao {

	// constructor
	public UserProfileDaoImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	/**
	 * method to verify if user already exists in the database username and email
	 * are unique fields in the profiles table, therefore, if username or email are
	 * already in that table, new user can't be inserted. Returns true if username
	 * or email already exist or if any database error occurs
	 */
	public boolean userExists(String username, String email) {
		boolean exists = true;
		int numberOfRows = 0;

		Connection conn = null;
		try {
			// try to create connection to the database
			conn = new DBConnector().getConnection();

			// use prepared statements to avoid SQL Injection
			String query = "SELECT COUNT(*) FROM profiles WHERE username=? OR email=?";

			PreparedStatement prepSt = null;
			ResultSet rs = null;
			try {
				prepSt = conn.prepareStatement(query);
				prepSt.setString(1, username);
				prepSt.setString(2, email);

				rs = prepSt.executeQuery();
				// iterate over results, retrieve by column index
				while (rs.next()) {
					numberOfRows = rs.getInt(1);
					// System.out.println("Number of users by that username or email equals " +
					// numberOfRows);
				}
				// if the query returned number of rows other than 0, user already exists
				if (numberOfRows > 0) {
					exists = true;
				} else {
					exists = false;
				}

			} catch (SQLException e) {
				System.out.println(e);
			} finally { // clean up
				if (rs != null) {
					rs.close();
				}
				if (prepSt != null) {
					prepSt.close();
				}
			}
		} catch (SQLException e) {
			System.out.println(e);
		} catch (Exception e) {
			e.printStackTrace();
		} finally { // clean up
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex) {
					System.out.println(ex);
				}
			}
		} // end clean up
			// System.out.println(exists);
		return exists;
	} // end userExists method

	@Override
	/**
	 * method to insert new user into database table Returns true if user was
	 * successfully inserted Returns false if not or if any database error occurs
	 */
	public boolean insertUser(UserProfile user) {
		boolean success = false;

		Connection conn = null;
		try {
			// try to create connection to the database
			conn = new DBConnector().getConnection();

			// use prepared statements to avoid SQL Injection
			String query = "INSERT INTO profiles (firstname, lastname, "
					+ "email, company, department, title, work_address, "
					+ "work_city, work_state, work_zip, phone, username, password) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

			PreparedStatement prepSt = null;

			try {
				prepSt = conn.prepareStatement(query);
				prepSt.setString(1, user.getFirstname());
				prepSt.setString(2, user.getLastname());
				prepSt.setString(3, user.getEmail());
				prepSt.setString(4, user.getCompany());
				prepSt.setString(5, user.getDepartment());
				prepSt.setString(6, user.getTitle());
				prepSt.setString(7, user.getWork_address());
				prepSt.setString(8, user.getWork_city());
				prepSt.setString(9, user.getWork_state());
				prepSt.setString(10, user.getWork_zip());
				prepSt.setString(11, user.getPhone());
				prepSt.setString(12, user.getUsername());
				prepSt.setString(13, user.getPassword());

				prepSt.executeUpdate();

			} catch (SQLException e) {
				System.out.println(e);
			} finally { // clean up

				if (prepSt != null) {
					prepSt.close();
				}
			}
		} catch (SQLException e) {
			System.out.println(e);
		} catch (Exception e) {
			e.printStackTrace();
		} finally { // clean up
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex) {
					System.out.println(ex);
				}
			}
		} // end clean up

		// verify if the new row was added to the table
		if (userExists(user.getUsername(), user.getEmail())) {
			success = true;
		}
		return success;
	} // end insertUser

	@Override
	/**
	 * method to search for users in database table by Last Name Returns Array List
	 * of UserProfile objects where each UserProfile object represents single user
	 * in database table
	 */
	public ArrayList<UserProfile> getSearchResultsByName(String lastName) {
		ArrayList<UserProfile> searchResults = new ArrayList<>();
		int id = 0;
		String lastnameResult;
		String firstname;
		String email;
		String department;
		Connection conn = null;
		try {
			// try to create connection to the database
			conn = new DBConnector().getConnection();

			// use prepared statements to avoid SQL Injection
			String query = "SELECT id, lastname, firstname, email, department FROM profiles WHERE lastname=?";

			PreparedStatement prepSt = null;
			ResultSet rs = null;
			try {
				prepSt = conn.prepareStatement(query);
				// bind parameter with SQL query
				prepSt.setString(1, lastName);

				rs = prepSt.executeQuery();
				// iterate over results, retrieve by column index
				while (rs.next()) {
					id = rs.getInt(1);
					lastnameResult = rs.getString(2);
					firstname = rs.getString(3);
					email = rs.getString(4);
					// this field can be null!
					department = rs.getString(5);
					if (department == null) {
						// replace null with empty string
						department = "";
					}
					// create new UserProfile object
					UserProfile resultUser = new UserProfile();
					// set appropriate fields to values from database
					resultUser.setId(id);
					resultUser.setLastname(lastnameResult);
					resultUser.setFirstname(firstname);
					resultUser.setEmail(email);
					resultUser.setDepartment(department);
					// add resultUser to the list of search results
					searchResults.add(resultUser);
				} // end while

			} catch (SQLException e) {
				System.out.println(e);
			} finally { // clean up
				if (rs != null) {
					rs.close();
				}
				if (prepSt != null) {
					prepSt.close();
				}
			}
		} catch (SQLException e) {
			System.out.println("SQL exception");
			System.out.println(e);
		} catch (Exception e) {
			System.out.println("Other exception");
			e.printStackTrace();
		} finally { // clean up
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex) {
					System.out.println("close conn exception");
					System.out.println(ex);
				}
			}
		}

//		 System.out.println("Results length: " + searchResults.size());
//		 // iterate over results
//		 for (UserProfile u : searchResults) {
//		 System.out.print(u.getId());
//		 System.out.print(u.getLastname());
//		 System.out.print(u.getFirstname());
//		 System.out.print(u.getEmail());
//		 System.out.println(u.getDepartment());
//		 }
		return searchResults;
	}

	@Override
	public ArrayList<UserProfile> getSearchResultsByDept(String department) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<UserProfile> listAllResults() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserProfile accessProfile(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String updateProfile(UserProfile user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteUser(int id) {
		// TODO Auto-generated method stub
		return false;
	}

}
