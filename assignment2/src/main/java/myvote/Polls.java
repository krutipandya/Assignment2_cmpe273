package myvote;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Polls {
	@Id
	String id;

	String question, started_at, expired_at;
	String[] choice = new String[2];
	int[] result = new int[2];
	@JsonIgnore
	boolean check = false;

	public Polls() {
		super();
	}

	public Polls(String poll_id, String question, String started_at,
			String expired_at, String[] choice, boolean check) {
		super();
		this.id = poll_id;
		this.question = question;
		this.started_at = started_at;
		this.expired_at = expired_at;
		this.choice = choice;
		this.check=check;
	}

	public String getId() {
		return id;
	}

	public void setId(String poll_id) {
		this.id = poll_id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getStarted_at() {
		return started_at;
	}

	public void setStarted_at(String started_at) {
		this.started_at = started_at;
	}

	public String getExpired_at() {
		return expired_at;
	}

	public void setExpired_at(String expired_at) {
		this.expired_at = expired_at;
	}

	public String[] getChoice() {
		return choice;
	}

	public void setChoice(String[] choice) {
		this.choice = choice;
	}

	public int[] getResult() {
		return result;
	}

	public void setResult(int[] result) {
		this.result = result;
	}
	public void setCheck(boolean check)
	{
		this.check=check;
	}
	public boolean getCheck()
	{
		return this.check;
	}
}
