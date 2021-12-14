package pl.wiktor.circle.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.wiktor.circle.api.response.ContestantView;
import pl.wiktor.circle.domain.contestant.ContestantService;

import java.util.List;

@Controller
public class ContestantMvc {
    private final ContestantService contestantService;
    private final String serverPort;

    public ContestantMvc(ContestantService contestantService, @Value("${server.port}") String serverPort) {
        this.contestantService = contestantService;
        this.serverPort = serverPort;
    }

    @GetMapping("/console")
    public String index(Model model) {
        List<ContestantView> contestants = contestantService.getContestantsByRewarded(false);
        contestants.addAll(contestantService.getContestantsByRewarded(true));
        model.addAttribute("contestants", contestants);
        model.addAttribute("port", serverPort);
        return "console";
    }
}
