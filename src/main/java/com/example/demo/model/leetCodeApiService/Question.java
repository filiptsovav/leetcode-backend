package com.example.demo.model.leetCodeApiService;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Question {
    private String questionId;
    private String title;
    private String titleSlug;
    private String difficulty;
    private String content;
    private List<TopicTag> topicTags;
    private String similarQuestions;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleSlug() {
        return titleSlug;
    }

    public void setTitleSlug(String titleSlug) {
        this.titleSlug = titleSlug;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getContent() {
        if (content == null) {
            return "<strong>Available with premium subscription only</strong>";
        }
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<TopicTag> getTopicTags() {
        return topicTags;
    }

    public void setTopicTags(List<TopicTag> topicTags) {
        this.topicTags = topicTags;
    }

    public String getSimilarQuestions(String similarQuestions) {
        return similarQuestions;
    }

    public void setSimilarQuestions(String similarQuestions) {
        this.similarQuestions = similarQuestions;
    }

    public List<String> getSimilarQuestionSlugs() {
        List<String> slugs = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            List<SimilarQuestion> similarQuestionsList = objectMapper.readValue(this.similarQuestions,
                    new TypeReference<List<SimilarQuestion>>() {
                    });
            for (SimilarQuestion sq : similarQuestionsList) {
                slugs.add(sq.getTitleSlug());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return slugs;
    }
}
