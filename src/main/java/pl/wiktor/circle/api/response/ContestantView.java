package pl.wiktor.circle.api.response;

public class ContestantView {
    private final String id;
    private final String nick;
    private final String reward;
    private final boolean rewarded;

    public ContestantView(String id, String nick, String reward, boolean rewarded) {
        this.id = id;
        this.nick = nick;
        this.reward = reward;
        this.rewarded = rewarded;
    }

    public String getId() {
        return id;
    }

    public String getNick() {
        return nick;
    }

    public String getReward() {
        return reward;
    }

    public boolean isRewarded() {
        return rewarded;
    }
}
