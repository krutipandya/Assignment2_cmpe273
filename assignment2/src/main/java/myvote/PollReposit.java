package myvote;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PollReposit extends MongoRepository<Polls, String> {
	public Polls findById(String id);

}
