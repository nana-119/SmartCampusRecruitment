package com.example.demo.service;

import java.util.Set;

public interface InterestSimilarityService {
    Set<String> parseHobbies(String hobbiesString);
    double calculateJaccardSimilarity(Set<String> set1, Set<String> set2);
}
