package pl.wiktor.circle.domain.contestant;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wiktor.circle.domain.contestant.model.Contestant;

import java.util.List;

public interface ContestantRepository extends JpaRepository<Contestant, String> {
    List<Contestant> getByRewarded(boolean rewarded);
}
