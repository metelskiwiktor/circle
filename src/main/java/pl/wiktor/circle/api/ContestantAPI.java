package pl.wiktor.circle.api;

import org.springframework.web.bind.annotation.*;
import pl.wiktor.circle.api.request.CreateContestantRequest;
import pl.wiktor.circle.api.response.ContestantView;
import pl.wiktor.circle.domain.contestant.ContestantService;

import java.util.List;

@RestController
@RequestMapping("/contestant")
public class ContestantAPI {
    private final ContestantService contestantService;

    public ContestantAPI(ContestantService contestantService) {
        this.contestantService = contestantService;
    }

    @GetMapping("/get-contestants")
    public List<ContestantView> getContestants(@RequestParam boolean rewarded) {
        return contestantService.getContestantsByRewarded(rewarded);
    }

    @PostMapping("/create")
    public void createContestants(@RequestBody CreateContestantRequest createContestantRequest) {
        contestantService.create(createContestantRequest.getNick(), createContestantRequest.getReward());
    }

    @GetMapping("/switch-rewarded")
    public ContestantView switchContestantRewarded(@RequestParam String contestantId) {
        return contestantService.switchRewarded(contestantId);
    }
}
