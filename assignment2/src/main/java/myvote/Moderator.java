package myvote;

import java.util.ArrayList;

import javax.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Moderator {
	@Id
	int id;

	String name;

	@NotNull(message = "Email cannot be null \n")
	String email;

	@NotNull(message = "Password cannot be null \n")
	String password;

	String created_at;

	@JsonIgnore
	ArrayList<String> pollslist = new ArrayList<String>();

	public ArrayList<String> getPollslist() {
		return pollslist;
	}

	public void setPollslist(ArrayList<String> pollslist) {
		this.pollslist = pollslist;
	}

	public Moderator() {
	}

	public String getCreated_at() {

		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;

	}

	public Moderator(String name, String email, String password) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public void setId(int mod_id) {
		this.id = mod_id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

}
