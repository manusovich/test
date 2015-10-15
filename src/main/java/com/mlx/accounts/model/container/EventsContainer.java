package com.mlx.accounts.model.container;

import com.mlx.accounts.model.Event;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Set;

/**
 * Bean for Events objects
 * <p>
 * 9/8/14.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EventsContainer implements Serializable {
    private Long total;
    private Set<Event> result;

    public EventsContainer() {
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Set<Event> getResult() {
        return result;
    }

    public void setResult(Set<Event> result) {
        this.result = result;
    }
}
