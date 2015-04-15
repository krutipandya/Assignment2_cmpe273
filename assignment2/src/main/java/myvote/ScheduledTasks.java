package myvote;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kafka.producer.KeyedMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
	
	@Autowired
	ModReposit moderatorRep;
    
    @Autowired
	PollReposit pollRep;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Scheduled(fixedDelay = 300000)
    public void fetchExpiredPoll() {
    	//System.out.println(" inside fetch "+dateFormat.format(new Date()));
    	List<Moderator> moderators = new ArrayList<Moderator>();
    	moderators = moderatorRep.findAll();
    	if(moderators!=null && !moderators.isEmpty()){
    		for(Moderator moderator:moderators){
    			for(String pollId : moderator.getPollslist()){
    				try{
    				Polls polls = pollRep.findById(pollId);
    				Date start = format.parse(polls.expired_at);
    				Date end = format.parse(format.format(new Date()));
    	        	//System.out.println(" start "+ start +" end "+end);
    	        	Calendar cal1 = Calendar.getInstance();
    	        	Calendar cal2 = Calendar.getInstance();
    	        	cal1.setTime(start);
    	        	cal2.setTime(end);
    					if(cal2.after(cal1)){
    						System.out.println(" ---- "+ "poll is expired "+dateFormat.format(new Date()));
    						callProducer(polls.getResult(),moderator.getEmail());
    						
    					}
    				}catch(Exception e){
    					e.printStackTrace();
    				}	
    			}
    		}
    	}
    }
    private void callProducer(int[] results,String emailId) {
	    new SimpleProducer();
        String topic = "cmpe273-topic";
        String msg = emailId+":010107266:Poll Result"+Arrays.toString(results);
        System.out.println("message " +msg);
        KeyedMessage<Integer, String> data = new KeyedMessage<>(topic, msg);
        SimpleProducer.producer.send(data);
        SimpleProducer.producer.close();
} 
}

