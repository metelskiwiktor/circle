package pl.wiktor.circle.domain.contestant.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Contestant {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator",
            parameters = {
                    @Parameter(
                            name = "uuid_gen_strategy_class",
                            value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
                    )
            }
    )
    private String id;
    private String nick;
    private LocalDateTime createdAt;
    private String reward;
    private boolean rewarded;

    public Contestant() {
    }

    public Contestant(String nick, LocalDateTime createdAt, String reward, boolean rewarded) {
        this.nick = nick;
        this.createdAt = createdAt;
        this.reward = reward;
        this.rewarded = rewarded;
    }

    public String getId() {
        return id;
    }

    public String getNick() {
        return nick;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getReward() {
        return reward;
    }

    public boolean isRewarded() {
        return rewarded;
    }

    public void setRewarded(boolean rewarded) {
        this.rewarded = rewarded;
    }
}
