package database.handlers;

public class User 
{
	int id;
	String username;
	String password;

	// Empty constructor
	public User(){

	}
	// constructor
	public User(int id, String username, String password){
		this.id = id;
		this.username = username;
		this.password = password;
	}

	// constructor
	public User(String username, String password){
		this.username = username;
		this.password = password;
	}
	// getting ID
	public int getID(){
		return this.id;
	}

	// setting id
	public void setID(int id){
		this.id = id;
	}

	// getting name
	public String getUserName(){
		return this.username;
	}

	// setting name
	public void setUserName(String username){
		this.username = username;
	}

	// getting phone number
	public String getPassword(){
		return this.password;
	}

	// setting phone number
	public void setPassword(String password){
		this.password = password;
	}

}
