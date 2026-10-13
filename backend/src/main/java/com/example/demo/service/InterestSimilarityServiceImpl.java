package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Service
public class InterestSimilarityServiceImpl implements InterestSimilarityService{
    @Override
    public Set<String> parseHobbies(String hobbiesString) {
        if (hobbiesString == null || hobbiesString.trim().isEmpty()) {
            return Collections.emptySet();
        }
        return Arrays.stream(hobbiesString.split(","))
                .map(String::trim)
                .filter(hobby -> !hobby.isEmpty())
                .collect(Collectors.toSet());
    }

    @Override
    public double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        if (set1 == null || set2 == null || set1.isEmpty() || set2.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }
}
