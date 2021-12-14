package pl.wiktor.circle.domain.contestant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.wiktor.circle.api.response.ContestantView;
import pl.wiktor.circle.domain.contestant.model.Contestant;
import pl.wiktor.circle.domain.exception.ApiException;
import pl.wiktor.circle.domain.exception.ExceptionType;
import pl.wiktor.circle.infrastructure.lib.Assertion;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContestantService {
    private static final Logger logger = LoggerFactory.getLogger(ContestantService.class);
    private final ContestantRepository contestantRepository;
    private final DateTimeFormatter formatter;
    private final Clock clock;

    public ContestantService(ContestantRepository contestantRepository, DateTimeFormatter formatter, Clock clock) {
        this.contestantRepository = contestantRepository;
        this.formatter = formatter;
        this.clock = clock;
    }

    public void create(String nick, String reward) {
        logger.info("Adding new contestant with nick='{}' and reward='{}'", nick, reward);
        Validator.validateNick(nick);
        Validator.validateReward(reward);
        LocalDateTime createdAt = LocalDateTime.ofInstant(clock.instant(), ZoneId.of("Europe/Paris"));
        Contestant contestant = contestantRepository.save(new Contestant(nick, createdAt, reward, false));
        logger.info("Contestant created with id='{}', nick='{}', reward='{}', rewarded='{}', createdAt='{}'",
                contestant.getId(), contestant.getNick(), contestant.getReward(), contestant.isRewarded(),
                formatter.format(contestant.getCreatedAt()));
    }

    public ContestantView switchRewarded(String contestantId) {
        logger.info("Switching contestant reward status with id='{}' to opposite", contestantId);
        Contestant contestant = getContestantById(contestantId);
        contestant.setRewarded(!contestant.isRewarded());
        contestant = contestantRepository.save(contestant);
        logger.info("Switched contestant (id='{}' and nick='{}') rewarded status to '{}'",
                contestantId, contestant.getNick(), contestant.isRewarded());
        return contestantToView(contestant);
    }

    public List<ContestantView> getContestantsByRewarded(boolean rewarded) {
        logger.info("Searching contestants by rewarded status='{}'", rewarded);
        List<ContestantView> contestants = contestantRepository.getByRewarded(rewarded).stream()
                .sorted(Comparator.comparing(Contestant::getCreatedAt))
                .map(ContestantService::contestantToView)
                .collect(Collectors.toList());
        logger.info("Found {} contestants by rewarded status='{}'", contestants.size(), rewarded);
        return contestants;
    }

    private Contestant getContestantById(String contestantId) {
        Contestant contestant = contestantRepository.findById(contestantId)
                .orElseThrow(() -> {
                    logger.error("Provided contestant id='{}' not found", contestantId);
                    return new ApiException(ExceptionType.CONTESTANT_NOT_FOUND, contestantId);
                });
        logger.info("Contestant by provided id='{}' found as nick='{}'", contestantId, contestant.getNick());
        return contestant;
    }

    private static ContestantView contestantToView(Contestant contestant) {
        return new ContestantView(contestant.getId(), contestant.getNick(), contestant.getReward(),
                contestant.isRewarded());
    }

    private static class Validator {
        private static final int NICK_MINIMUM_LENGTH = 3;
        private static final int REWARD_MINIMUM_LENGTH = 3;

        private static void validateNick(String nick) {
            Assertion.notNull(nick, () -> new ApiException(ExceptionType.NICK_IS_NULL));
            Assertion.isBiggerEqualsThan(NICK_MINIMUM_LENGTH, nick,
                    () -> new ApiException(ExceptionType.NICK_IS_TOO_SHORT, nick));
        }

        private static void validateReward(String reward) {
            Assertion.notNull(reward, () -> new ApiException(ExceptionType.REWARD_IS_NULL));
            Assertion.isBiggerEqualsThan(REWARD_MINIMUM_LENGTH, reward,
                    () -> new ApiException(ExceptionType.REWARD_IS_TOO_SHORT, reward));
        }
    }
}
