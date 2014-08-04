package com.gigaspaces.storm.googleanalytics.model.reports;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceRouting;
import org.openspaces.remoting.Routing;

import java.io.Serializable;

/**
 * @author Oleksiy_Dyagilev
 */
public class ActiveUsersReport implements Serializable {

    private Long activeUsersNumber;

    public ActiveUsersReport() {
    }

    public ActiveUsersReport(long activeUsersNumber) {
        this.activeUsersNumber = activeUsersNumber;
    }

    public Long getActiveUsersNumber() {
        return activeUsersNumber;
    }

    public void setActiveUsersNumber(Long activeUsersNumber) {
        this.activeUsersNumber = activeUsersNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActiveUsersReport that = (ActiveUsersReport) o;

        if (activeUsersNumber != null ? !activeUsersNumber.equals(that.activeUsersNumber) : that.activeUsersNumber != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return activeUsersNumber != null ? activeUsersNumber.hashCode() : 0;
    }
}
