package com.gigaspaces.storm.googleanalytics.model.reports;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceRouting;
import org.openspaces.remoting.Routing;

import java.io.Serializable;

/**
 * @author Oleksiy_Dyagilev
 */
@SpaceClass
public class ActiveUsersReport implements Serializable {

    // singleton object in the space
    private Long id = 1L;
    private Long activeUsersNumber;

    public ActiveUsersReport() {
    }

    public ActiveUsersReport(long activeUsersNumber) {
        this.activeUsersNumber = activeUsersNumber;
    }

    @SpaceId
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActiveUsersNumber() {
        return activeUsersNumber;
    }

    public void setActiveUsersNumber(Long activeUsersNumber) {
        this.activeUsersNumber = activeUsersNumber;
    }
}
