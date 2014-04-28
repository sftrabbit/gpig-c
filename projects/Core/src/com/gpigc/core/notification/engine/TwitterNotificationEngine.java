package com.gpigc.core.notification.engine;

import java.util.List;

import twitter4j.GeoLocation;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.event.DataEvent;
import com.gpigc.core.notification.NotificationEngine;

public class TwitterNotificationEngine extends NotificationEngine {

	
	private static final String CONSUMER_KEY = "6o7IH0CM9q2wOYrI94RA0W16E";
	private static final String CONSUMER_SECRET = 
			"pFyeoF5nYliR967eq1RdDdVnMrngGywTj92qyBuulIGBYpQgPh";
	private static final String ACCESS_TOKEN = 
	"2466857444-bCriobHyeBix9Bmln3kE2I1xrnqaKJ0S387w2R2";
	private static final String ACCESS_SECRET = 
			"PLjTn3y9vzxDgd90Pq2l5hdcrlsDFtq6eYoaG9EDSqyyr";
	
	
	public TwitterNotificationEngine(List<ClientSystem> registeredSystems) {
		super(registeredSystems);
	}

	@Override
	public void send(DataEvent event) {
		
        TwitterFactory twitterFactory = new TwitterFactory();
         Twitter twitter = twitterFactory.getInstance();
 
        twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
 
        //setup OAuth Access Token
        twitter.setOAuthAccessToken(new AccessToken(ACCESS_TOKEN, ACCESS_SECRET));
 
        //Instantiate and initialize a new twitter status update
        StatusUpdate statusUpdate = new StatusUpdate(
        		event.getData().get("Message")
             );
        if(event.getData().containsKey("Long")&& event.getData().containsKey("Lat")){
        	System.out.println("Setting Location");
        	statusUpdate.setLocation(
        			new GeoLocation(Double.parseDouble(event.getData().get("Lat")),
        					Double.parseDouble(event.getData().get("Long"))));
        }
        //tweet or update status
        try {
			Status status = twitter.updateStatus(statusUpdate);
		} catch (TwitterException e) {
			System.out.println("Could Not Update Status");
			e.printStackTrace();
		}
 
  	}

}
