package myvote;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ModReposit extends MongoRepository<Moderator, String> {

	public Moderator findById(int id);

}