package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.exceptions.QuestionNotFoundException;
import com.example.demo.model.leetCodeApiService.ApiResponse;
import com.example.demo.model.leetCodeApiService.Question;

@Service
public class LeetCodeApiService {

    @Autowired
    private RestTemplate restTemplate;
    
    public Question getQuestion(String questionName) {
        String url = "https://leetcode.com/graphql/";
    
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("referer", String.format("https://leetcode.com/"));

        String query = generateQuery(questionName);
        HttpEntity<String> requestEntity = new HttpEntity<>(query, headers);

        ResponseEntity<ApiResponse> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ApiResponse.class);
        ApiResponse apiResponse = response.getBody();

        if (apiResponse.getData() == null || apiResponse.getData().getQuestion() == null) {
            throw new QuestionNotFoundException("Question field is null");
        }

        return apiResponse.getData().getQuestion();
    }

    private String generateQuery(String questionName) {
        String query = """
                {"query" : "query selectProblem($titleSlug: String!) {
                            question(titleSlug: $titleSlug) {
                                questionId
                                title
                                titleSlug
                                difficulty
                                content
                                topicTags {
                                    name
                                    slug
                                }
                                similarQuestions
                            }
                        }",
                "variables":{"titleSlug":"%s"}}
                """;
        query = query.replaceAll("\n", " ");
        query = query.replaceAll("\\s+", " ");
        return String.format(query, questionName);
    }
}
