package com.gigaspaces.storm.reach;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

import java.util.List;

/**
 * @author Oleksiy_Dyagilev
 */
@SpaceClass
public class TweetedUrl {

    private String url;
    private List<String> tweeters;

    public TweetedUrl() {
    }

    public TweetedUrl(String url, List<String> tweeters) {
        this.url = url;
        this.tweeters = tweeters;
    }

    @SpaceId(autoGenerate = false)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getTweeters() {
        return tweeters;
    }

    public void setTweeters(List<String> tweeters) {
        this.tweeters = tweeters;
    }

    @Override
    public String toString() {
        return "TweetedUrl{" +
                "url='" + url + '\'' +
                ", tweeters=" + tweeters +
                '}';
    }
}
