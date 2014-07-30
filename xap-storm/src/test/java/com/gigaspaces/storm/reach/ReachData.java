package com.gigaspaces.storm.reach;

import org.openspaces.core.GigaSpace;

import java.util.Arrays;
import java.util.List;

/**
 * @author Oleksiy_Dyagilev
 */
public class ReachData {

    public static void writeToSpace(GigaSpace space) {
        List<TweetedUrl> tweetedUrls = Arrays.asList(
                new TweetedUrl("foo.com/blog/1", Arrays.asList("sally", "bob", "tim", "george", "nathan")),
                new TweetedUrl("engineering.twitter.com/blog/5", Arrays.asList("adam", "david", "sally", "nathan")),
                new TweetedUrl("tech.backtype.com/blog/123", Arrays.asList("tim", "mike", "john"))
        );
        space.writeMultiple(tweetedUrls.toArray());

        List<Followers> followersList = Arrays.asList(
                new Followers("sally", Arrays.asList("bob", "tim", "alice", "adam", "jim", "chris", "jai")),
                new Followers("bob", Arrays.asList("sally", "nathan", "jim", "mary", "david", "vivian")),
                new Followers("tim", Arrays.asList("alex")),
                new Followers("nathan", Arrays.asList("sally", "bob", "adam", "harry", "chris", "vivian", "emily", "jordan")),
                new Followers("adam", Arrays.asList("david", "carissa")),
                new Followers("mike", Arrays.asList("john", "bob")),
                new Followers("john", Arrays.asList("alice", "nathan", "jim", "mike", "bob"))
        );
        space.writeMultiple(followersList.toArray());
    }
}
