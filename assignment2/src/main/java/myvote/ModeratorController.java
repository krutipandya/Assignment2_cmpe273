package myvote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.stereotype.Repository;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Repository
@EnableWebMvcSecurity
@RequestMapping(value = "/api/v1")
@RestController
public class ModeratorController extends WebSecurityConfigurerAdapter {

	@Autowired
	ModReposit moderatorRep;

	@Autowired
	PollReposit pollRep;

	Moderator mod = new Moderator();
	Polls poll = new Polls();
	Moderator m;
	Polls p;
	ArrayList<Moderator> modList = new ArrayList<Moderator>();

	ArrayList<String> pollList = new ArrayList<String>();
	
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	int[] tempresult = new int[2];
	int[] result = new int[2];
	int[] finalresult = new int[2];
	String[] choice = new String[2];

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().and().csrf().disable().authorizeRequests()
				.antMatchers(HttpMethod.POST, "/api/v1/moderators").permitAll()
				.antMatchers("/api/v1/polls/**").permitAll()
				.antMatchers("/api/v1/moderators/**").fullyAuthenticated()
				.anyRequest().hasRole("USER");
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.inMemoryAuthentication().withUser("foo").password("bar")
				.roles("USER");
	}

	@RequestMapping(value = "/moderators", method = RequestMethod.POST)
	public ResponseEntity<Moderator> createModerator(@Valid @RequestBody Moderator mod) {
		
		int mod_id = (int)Math.round(Math.random() * (999999 - 100000 + 1) + 100000);
		mod.setCreated_at(format.format(new Date()));
		mod.setId(mod_id);
		modList.add(mod);
		moderatorRep.save(mod);
		
		return new ResponseEntity<Moderator>(mod, HttpStatus.CREATED);

	}

	@RequestMapping(value = "/moderators/{id}", method = RequestMethod.GET)
	public ResponseEntity viewModerator(@PathVariable int id) {
		
		m = moderatorRep.findById(id);
		if (m == null) {
			return new ResponseEntity("Could not find moderator",HttpStatus.BAD_REQUEST);

		}
		return new ResponseEntity<Moderator>(m, HttpStatus.OK);

	}

	@RequestMapping(value = "/moderators/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Moderator> updateModerator(@Valid @RequestBody Moderator mod, @PathVariable int id) {

		String email = mod.getEmail();
		String password = mod.getPassword();
		m = moderatorRep.findById(id);
		m.setEmail(email);
		m.setPassword(password);
		moderatorRep.save(m);
		return new ResponseEntity(m, HttpStatus.OK);
	}

	@RequestMapping(value = "/moderators/{mod_id}/polls", method = RequestMethod.POST)
	public ResponseEntity createPoll(@Valid @RequestBody Polls poll, @PathVariable int mod_id) {
		
		String pollId= Integer.toString(new Random().nextInt(), 36).toUpperCase();
		poll.setId(pollId);
		pollRep.save(poll);
		m = moderatorRep.findById(mod_id);
		pollList = m.getPollslist();
		pollList.add(poll.getId());
		m.setPollslist(pollList);
		moderatorRep.save(m);
		
		Map<String, Object> pollMap = new LinkedHashMap<String, Object>();

		pollMap.put("id", poll.getId());
		pollMap.put("question", poll.getQuestion());
		pollMap.put("started_at", poll.getStarted_at());
		pollMap.put("expired_at", poll.getExpired_at());
		pollMap.put("choice", poll.getChoice());
		
		return new ResponseEntity(pollMap, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/polls/{poll_id}", method = RequestMethod.GET)
	public ResponseEntity viewPollsWithoughtResult(@PathVariable String poll_id) {

		Map<String, Object> pollMap = new LinkedHashMap<String, Object>();

		p = pollRep.findById(poll_id);
		if (p == null) {
			return new ResponseEntity("Could not find poll",HttpStatus.BAD_REQUEST);

		}
		pollMap.put("id", p.getId());
		pollMap.put("question", p.getQuestion());
		pollMap.put("started_at", p.getStarted_at());
		pollMap.put("expired_at", p.getExpired_at());
		pollMap.put("choice", p.getChoice());

		return new ResponseEntity(pollMap, HttpStatus.OK);
	}

	@RequestMapping(value = "/moderators/{mod_id}/polls/{poll_id}", method = RequestMethod.GET)
	public ResponseEntity viewPollWithResult(@PathVariable int mod_id,
			@PathVariable String poll_id) {

		m = moderatorRep.findById(mod_id);
		pollList = m.getPollslist();
		for (int i = 0; i < pollList.size(); i++) {
			if (pollList.get(i).equals(poll_id)) {
				p = pollRep.findById(pollList.get(i));
				return new ResponseEntity(p, HttpStatus.OK);
			}
		}

		return new ResponseEntity("Could not find poll", HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(value = "/moderators/{mod_id}/polls", method = RequestMethod.GET)
	public ResponseEntity listAllPolls(@PathVariable int mod_id) {
		ArrayList<Polls> stringlist1 = new ArrayList<Polls>();
		m = moderatorRep.findById(mod_id);
		pollList = m.getPollslist();

		for (int i = 0; i < pollList.size(); i++) {
			p = pollRep.findById(pollList.get(i));
			stringlist1.add(p);

		}
		return new ResponseEntity(stringlist1, HttpStatus.OK);
	}

	@RequestMapping(value = "/moderators/{mod_id}/polls/{poll_id}", method = RequestMethod.DELETE)
	public ResponseEntity deletePoll(@PathVariable int mod_id,
			@PathVariable String poll_id) {

		p = pollRep.findById(poll_id);
		pollRep.delete(p);

		m = moderatorRep.findById(mod_id);
		pollList = m.getPollslist();

		for (int i = 0; i < pollList.size(); i++) {
			if (pollList.get(i).equals(poll_id)) {
				pollList.remove(i);

				m.setPollslist(pollList);
				moderatorRep.save(m);
				return new ResponseEntity("Poll is successfully deleted",HttpStatus.NO_CONTENT);
			}

		}
		return new ResponseEntity("Could not delete poll",HttpStatus.OK);
	}

	@RequestMapping(value = "/polls/{poll_id}", method = RequestMethod.PUT)
	public ResponseEntity voteAPoll(@PathVariable String poll_id, @RequestParam(value = "choice") int choice_index) {
		p = pollRep.findById(poll_id);
		if (choice_index == 0) {
			tempresult = p.getResult();
			tempresult[choice_index] = tempresult[choice_index] + 1;
			p.setResult(tempresult);
			pollRep.save(p);
			return new ResponseEntity(p, HttpStatus.NO_CONTENT);

		} else if (choice_index == 1) {
			tempresult = p.getResult();
			tempresult[choice_index] = tempresult[choice_index] + 1;
			p.setResult(tempresult);
			pollRep.save(p);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity("Could not cast vote", HttpStatus.NO_CONTENT);

	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public ResponseEntity handleBadInput(MethodArgumentNotValidException e) {
		String errors = "";
		for (FieldError obj : e.getBindingResult().getFieldErrors()) {
			errors += obj.getDefaultMessage();
		}
		return new ResponseEntity(errors, HttpStatus.BAD_REQUEST);
	}
}
