package com.example.demo.model;

import com.example.demo.model.leetCodeApiService.Question;
import com.example.demo.model.leetCodeApiService.TopicTag;
import com.example.demo.service.LeetCodeApiService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppUserTest {

    private AppUser user;

    @Mock
    private LeetCodeApiService apiService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = new AppUser("test", "pass");
    }

    private Question q(String difficulty, List<TopicTag> tags, List<String> similar) {
        Question q = new Question();
        q.setDifficulty(difficulty);
        q.setTopicTags(tags);
        q.setSimilarQuestions("[{\"titleSlug\":\"" + similar.get(0) + "\"}]");
        return q;
    }

    @Test
    void getSolvedProblemsByDifficulty_ReturnsCorrectStats() {
        LocalDateTime now = LocalDateTime.now();
        user.addRecord(new TaskRecord("t1", now, Duration.ofMinutes(20), 1));
        user.addRecord(new TaskRecord("t2", now, Duration.ofMinutes(20), 1));

        Question q1 = q("Easy", List.of(), List.of("aaa"));
        Question q2 = q("Hard", List.of(), List.of("bbb"));

        when(apiService.getQuestion("t1")).thenReturn(q1);
        when(apiService.getQuestion("t2")).thenReturn(q2);

        List<Double> result =
                user.getSolvedProblemsByDifficulty(apiService, Duration.ofDays(7));

        assertEquals(2, result.size());
        assertTrue(result.contains(1.0));
    }

    @Test
    void getSolvedProblemsByTopic_CountsCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        user.addRecord(new TaskRecord("t1", now, Duration.ofMinutes(10), 1));

        TopicTag tag = new TopicTag();
        tag.setName("dp");

        Question q1 = q("Easy", List.of(tag), List.of("zzz"));
        when(apiService.getQuestion("t1")).thenReturn(q1);

        Map<String, Long> map =
                user.getSolvedProblemsByTopic(apiService, Duration.ofDays(7));

        assertEquals(1, map.get("dp"));
    }

    @Test
    void getSolvedProblemsByDayOfWeek_Works() {
        LocalDateTime date = LocalDateTime.of(2024, 1, 1, 10, 0);
        user.addRecord(new TaskRecord("t1", date, Duration.ofMinutes(10), 1));

        Map<?, Long> map = user.getSolvedProblemsByDayOfWeek();

        assertEquals(1, map.get(DayOfWeek.MONDAY));
    }

    // @Test
    // void getRecommendedTasks_AddsSimilarQuestions() {
    //     user.addRecord(new TaskRecord("t1", LocalDateTime.now(), Duration.ofMinutes(10), 3));

    //     Question q1 = q("Easy", List.of(), List.of("sim1"));

    //     Question similar = q("Medium", List.of(), List.of("sim2"));

    //     when(apiService.getQuestion("t1")).thenReturn(q1);
    //     when(apiService.getQuestion("sim1")).thenReturn(similar);

    //     List<Question> list = user.getRecommendedTasks(apiService);

    //     assertEquals(2, list.size());
    // }

    @Test
    void getAverageTimeToSolve_ReturnsCorrectAverages() {
        LocalDateTime now = LocalDateTime.now();

        user.addRecord(new TaskRecord("t1", now, Duration.ofMinutes(10), 1));
        user.addRecord(new TaskRecord("t2", now, Duration.ofMinutes(20), 1));

        Question q1 = q("Easy", List.of(), List.of("x"));
        Question q2 = q("Medium", List.of(), List.of("y"));

        when(apiService.getQuestion("t1")).thenReturn(q1);
        when(apiService.getQuestion("t2")).thenReturn(q2);

        List<Double> res = user.getAverageTimeToSolve(apiService, Duration.ofDays(7));

        assertEquals(3, res.size());
        assertEquals(10.0, res.get(0)); // easy avg
        assertEquals(20.0, res.get(1)); // medium avg
    }

    @Test
    void getFirstAttemptStats_WorksCorrectly() {
        LocalDateTime now = LocalDateTime.now();

        user.addRecord(new TaskRecord("t1", now, Duration.ofMinutes(10), 1));
        user.addRecord(new TaskRecord("t2", now, Duration.ofMinutes(20), 3));

        List<Long> result =
                user.getFirstAttemptStats(apiService, Duration.ofDays(7));

        assertEquals(List.of(1L, 1L), result);
    }
    @ParameterizedTest
    @CsvSource({
            "Easy,1",
            "Medium,2",
            "Hard,3"
    })
    void testSolvedProblemsByDifficulty_Parametrized(String difficulty, int count) {
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < count; i++) {
            user.addRecord(new TaskRecord("task" + i, now, Duration.ofMinutes(5), 1));
        }

            for (int i = 0; i < count; i++) {
            Question q = new Question();
            q.setDifficulty(difficulty);
            q.setTopicTags(List.of());
            q.setSimilarQuestions("[{\"titleSlug\":\"x\"}]");

            when(apiService.getQuestion("task" + i)).thenReturn(q);
        }

        List<Double> result =
                user.getSolvedProblemsByDifficulty(apiService, Duration.ofDays(7));

        assertEquals(1, result.size(), "Только одна группа сложности");
        assertEquals(count, result.get(0));
    }

    @ParameterizedTest
@CsvSource({
        "Easy,10,10.0",
        "Medium,30,30.0",
        "Hard,50,50.0"
})
void testAverageTimeToSolve_Parametrized(String difficulty, long minutes, double expectedAvg) {
    LocalDateTime now = LocalDateTime.now();

    // 2 решения одинаковой сложности
    user.addRecord(new TaskRecord("task1", now, Duration.ofMinutes(minutes), 1));
    user.addRecord(new TaskRecord("task2", now, Duration.ofMinutes(minutes), 1));

    Question q = new Question();
    q.setDifficulty(difficulty);
    q.setTopicTags(List.of());
    q.setSimilarQuestions("[{\"titleSlug\":\"x\"}]");

    when(apiService.getQuestion("task1")).thenReturn(q);
    when(apiService.getQuestion("task2")).thenReturn(q);

    List<Double> averages =
            user.getAverageTimeToSolve(apiService, Duration.ofDays(7));

    int index = switch (difficulty) {
        case "Easy" -> 0;
        case "Medium" -> 1;
        case "Hard" -> 2;
        default -> throw new IllegalStateException("Unexpected difficulty: " + difficulty);
    };

    assertEquals(expectedAvg, averages.get(index));
    }

}
