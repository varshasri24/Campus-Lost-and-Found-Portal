package com.example.lostfound.service;

import com.example.lostfound.model.Item;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    public double textJaccard(String a, String b) {
        if (a == null || b == null) return 0.0;
        Set<String> sa = tokenize(a);
        Set<String> sb = tokenize(b);
        if (sa.isEmpty() || sb.isEmpty()) return 0.0;
        Set<String> inter = new HashSet<>(sa);
        inter.retainAll(sb);
        Set<String> union = new HashSet<>(sa);
        union.addAll(sb);
        return (double) inter.size() / union.size();
    }

    private Set<String> tokenize(String s){
        return Arrays.stream(s.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .split("\\s+"))
                .filter(t -> t.length() > 0)
                .collect(Collectors.toSet());
    }

    // combined score: text weight 0.6, image weight 0.4 (imageDist 0->1)
    public double combinedScore(Item posted, Item candidate, int imageDistance) {
        double textSim = textJaccard(posted.getTitle() + " " + posted.getDescription(),
                                     candidate.getTitle() + " " + candidate.getDescription());
        double imageSim = 0.0;
        if (posted.getImageHash() != null && candidate.getImageHash() != null) {
            imageSim = Math.max(0.0, 1.0 - (double)imageDistance / 64.0);
        }
        return textSim * 0.6 + imageSim * 0.4;
    }
}
