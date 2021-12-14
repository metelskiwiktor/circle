package pl.wiktor.circle.api.request;

public class CreateContestantRequest {
    private final String nick;
    private final String reward;

    public CreateContestantRequest(String nick, String reward) {
        this.nick = nick;
        this.reward = reward;
    }

    public String getNick() {
        return nick;
    }

    public String getReward() {
        return reward;
    }
}
