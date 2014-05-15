package com.gpigc.core.notification.engine;

import java.util.List;

import twitter4j.GeoLocation;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.Parameter;
import com.gpigc.core.event.DataEvent;
import com.gpigc.core.notification.NotificationEngine;
import com.gpigc.core.view.StandardMessageGenerator;

public class TwitterNotificationEngine extends NotificationEngine {

	private static final String CONSUMER_KEY = "6o7IH0CM9q2wOYrI94RA0W16E";
	private static final String CONSUMER_SECRET = "pFyeoF5nYliR967eq1RdDdVnMrngGywTj92qyBuulIGBYpQgPh";
	private static final String ACCESS_TOKEN = "2466857444-bCriobHyeBix9Bmln3kE2I1xrnqaKJ0S387w2R2";
	private static final String ACCESS_SECRET = "PLjTn3y9vzxDgd90Pq2l5hdcrlsDFtq6eYoaG9EDSqyyr";

	public TwitterNotificationEngine(List<ClientSystem> registeredSystems,
			final int COOL_DOWN_SECS) {
		super(registeredSystems, COOL_DOWN_SECS);
	}

	@Override
	public boolean send(DataEvent event) {
		if (!getRecentlySent()) {
			TwitterFactory twitterFactory = new TwitterFactory();
			Twitter twitter = twitterFactory.getInstance();

			twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);

			// setup OAuth Access Token
			twitter.setOAuthAccessToken(new AccessToken(ACCESS_TOKEN,
					ACCESS_SECRET));

			// Instantiate and initialize a new twitter status update
			StatusUpdate statusUpdate = new StatusUpdate(event.getData().get(
					Parameter.MESSAGE));
			if (event.getData().containsKey(Parameter.LONG)
					&& event.getData().containsKey(Parameter.LAT)) {
				statusUpdate
						.setLocation(new GeoLocation(Double.parseDouble(event
								.getData().get(Parameter.LAT)), Double
								.parseDouble(event.getData()
										.get(Parameter.LONG))));
			}
			try {
				twitter.updateStatus(statusUpdate);
				setRecentlySent();
				StandardMessageGenerator.notificationGenerated(name, event
						.getSystem().getID());
				return true;
			} catch (TwitterException e) {
				StandardMessageGenerator.couldNotUpdateStatus();
				e.printStackTrace();
			}
		}
		return false;
	}

}
