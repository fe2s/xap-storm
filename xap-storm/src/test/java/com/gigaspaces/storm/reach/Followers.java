package com.gigaspaces.storm.reach;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

import java.util.List;

/**
 * @author Oleksiy_Dyagilev
 */
@SpaceClass
public class Followers {
    private String tweeter;
    private List<String> followers;

    public Followers() {
    }

    public Followers(String tweeter, List<String> followers) {
        this.tweeter = tweeter;
        this.followers = followers;
    }

    @SpaceId(autoGenerate = false)
    public String getTweeter() {
        return tweeter;
    }

    public void setTweeter(String tweeter) {
        this.tweeter = tweeter;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }
}
