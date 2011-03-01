package fr.digitbooks.guestbook;

import java.util.Date;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import com.google.appengine.api.users.User;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Greeting {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private String content;
    
    @Persistent
    private float rate;

    @Persistent
    private Date date;

    public Greeting(float rate, String content, Date date) {
        this.rate = rate;
        this.content = content;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public float getRate() {
        return rate;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
