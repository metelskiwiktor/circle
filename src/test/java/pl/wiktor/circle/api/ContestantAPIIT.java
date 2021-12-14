package pl.wiktor.circle.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.wiktor.circle.api.request.CreateContestantRequest;
import pl.wiktor.circle.api.response.ContestantView;
import pl.wiktor.circle.domain.contestant.ContestantRepository;
import pl.wiktor.circle.domain.contestant.model.Contestant;

import javax.servlet.ServletContext;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ContestantAPIIT {
    private static final String CREATE_CONTESTANT_ENDPOINT = "/contestant/create";
    private static final String SWITCH_CONTESTANT_REWARDED_ENDPOINT = "/contestant/switch-rewarded";
    private static final String GET_CONTESTANTS_BY_REWARDED_ENDPOINT = "/contestant/get-contestants";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ContestantRepository contestantRepository;

    @BeforeEach
    void setUp() {
        contestantRepository.deleteAll();
    }

    @Test
    void contestantControllerBean_shouldBeProvided() {
        //given
        char[] controllerSimpleName = ContestantAPI.class.getSimpleName().toCharArray();
        controllerSimpleName[0] = Character.toLowerCase(controllerSimpleName[0]);
        // when
        ServletContext servletContext = webApplicationContext.getServletContext();
        //then
        assertNotNull(servletContext);
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(webApplicationContext.getBean(new String(controllerSimpleName)));
    }

    @Test
    void createContestantRequest_shouldCreateContestant() throws Exception {
        //given
        String nick = "nick";
        String reward = "reward";
        int expectedContestantsSize = 1;
        //when
        int requestHttpResponse = createContestantRequest(nick, reward).getResponse().getStatus();
        //then
        List<Contestant> contestants = contestantRepository.findAll();
        assertEquals(expectedContestantsSize, contestants.size());
        validateCreatedContestant(nick, reward, contestants.get(0));
        assertEquals(HttpStatus.OK.value(), requestHttpResponse);
    }

    @ParameterizedTest
    @CsvSource({"valid-nick,", ",valid-reward", "in,valid-reward", "valid-nick,ir", ","})
    void createContestantRequest_withInvalidData_shouldRejectRequest(String nick, String reward) throws Exception {
        //given //when
        int requestHttpResponse = createContestantRequest(nick, reward).getResponse().getStatus();
        //then
        assertEquals(HttpStatus.BAD_REQUEST.value(), requestHttpResponse);
        assertEquals(0, contestantRepository.findAll().size());
    }

    @ParameterizedTest
    @CsvSource({"true,1", "false,2", "true,3", "false,4", "true,5"})
    void switchContestantRewardedRequest_shouldSwitchRewardedToOpposite(boolean expectedRewarded, int switches)
            throws Exception {
        //given
        Contestant contestant = createDefaultContestant();
        int expectedContestantsSize = 1;
        String requestResponse = "";
        //when
        for (int i = 0; i < switches; i++) {
            requestResponse = switchContestantRewarded(contestant.getId()).getResponse().getContentAsString();
        }
        //then
        List<Contestant> contestants = contestantRepository.findAll();
        assertEquals(expectedContestantsSize, contestants.size());
        assertTrue(requestResponse.contains("rewarded\":" + expectedRewarded));
        assertEquals(expectedRewarded, contestants.get(0).isRewarded());
    }

    @ParameterizedTest
    @CsvSource({"true,3", "false,2"})
    void getContestantsByRewarded_shouldFilterContestantsAndSortByCreateDate(boolean rewarded, int expectedContestants)
            throws Exception {
        //given
        createContestantAndSetRewarded("nick5", "reward5", false);
        createContestantAndSetRewarded("nick1", "reward1", true);
        createContestantAndSetRewarded("nick2", "reward2", false);
        createContestantAndSetRewarded("nick4", "reward4", true);
        createContestantAndSetRewarded("nick3", "reward3", true);
        Map<Boolean, List<String>> contestantsOrder = new HashMap<>();
        contestantsOrder.put(true, Arrays.asList("nick1", "nick4", "nick3"));
        contestantsOrder.put(false, Arrays.asList("nick5", "nick2"));
        //when
        String getContestantsResponse = getContestantsByRewarded(rewarded).getResponse().getContentAsString();
        //then
        List<ContestantView> contestantsByRewarded = objectMapper
                .readValue(getContestantsResponse, new TypeReference<>() {
                });
        assertEquals(expectedContestants, contestantsByRewarded.size());
        boolean orderByRewarded = contestantsByRewarded.stream()
                .map(ContestantView::getNick)
                .collect(Collectors.toList())
                .equals(contestantsOrder.get(rewarded));
        assertTrue(orderByRewarded);
    }

    private void validateCreatedContestant(String nick, String reward, Contestant validateObj) {
        assertEquals(nick, validateObj.getNick());
        assertEquals(reward, validateObj.getReward());
        assertFalse(validateObj.isRewarded());
        assertNotNull(validateObj.getId());
        assertTrue(validateObj.getId().length() > 0);
    }

    private Contestant createDefaultContestant() throws Exception {
        return createContestantAndSetRewarded("nick", "reward", false);
    }

    private Contestant createContestantAndSetRewarded(String nick, String reward, boolean rewarded) throws Exception {
        createContestantRequest(nick, reward);
        Contestant contestant = contestantRepository.findAll().stream()
                .filter(c -> c.getNick().equals(nick) && c.getReward().equals(reward))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Test user not found"));
        if (rewarded) {
            switchContestantRewarded(contestant.getId());
        }
        return contestant;
    }

    private MvcResult createContestantRequest(String nick, String reward) throws Exception {
        String requestBody = objectMapper.writeValueAsString(new CreateContestantRequest(nick, reward));
        return mockMvc.perform
                (MockMvcRequestBuilders
                        .post(CREATE_CONTESTANT_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andReturn();
    }

    private MvcResult switchContestantRewarded(String contestantId) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders
                        .get(SWITCH_CONTESTANT_REWARDED_ENDPOINT)
                        .param("contestantId", contestantId))
                .andReturn();
    }

    private MvcResult getContestantsByRewarded(boolean rewarded) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders
                        .get(GET_CONTESTANTS_BY_REWARDED_ENDPOINT)
                        .param("rewarded", Boolean.toString(rewarded)))
                .andReturn();
    }
}
