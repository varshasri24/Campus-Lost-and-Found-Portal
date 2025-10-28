package com.example.lostfound.controller;

import com.example.lostfound.model.Item;
import com.example.lostfound.repository.ItemRepository;
import com.example.lostfound.service.ImageService;
import com.example.lostfound.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private MatchingService matchingService;

    @PostMapping
    public ResponseEntity<?> createItem(
            @RequestParam String type,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String posterName,
            @RequestParam(required = false) String posterContact,
            @RequestParam(required = false) MultipartFile image
    ) {
        try {
            Item item = new Item();
            item.setType(type);
            item.setTitle(title);
            item.setDescription(description);
            item.setLocation(location);
            item.setPosterName(posterName);
            item.setPosterContact(posterContact);

            // Save image if present
            if (image != null && !image.isEmpty()) {
                String relPath = imageService.saveImage(image); // returns "uploads/filename"
                item.setImagePath(relPath);
                // For aHash we need file system absolute path -> create from relative
                String absolutePath = Paths.get(relPath).toFile().getAbsolutePath();
                // But the saveImage saved it in configured uploadDir so we can compute path differently:
                // Simpler: get local file path:
                String filePath = new java.io.File(relPath).getAbsolutePath();
                String hash = imageService.averageHash(filePath);
                item.setImageHash(hash);
            }
            Item saved = itemRepository.save(item);

            // Find opposite type items for potential matches
            String oppositeType = type.equalsIgnoreCase("LOST") ? "FOUND" : "LOST";
            List<Item> candidates = itemRepository.findAll().stream()
                    .filter(it -> it.getType().equalsIgnoreCase(oppositeType))
                    .filter(it -> !Objects.equals(it.getId(), saved.getId()))
                    .collect(Collectors.toList());

            List<Map<String,Object>> matches = new ArrayList<>();
            for (Item c : candidates) {
                int imageDist = Integer.MAX_VALUE;
                if (saved.getImageHash() != null && c.getImageHash() != null) {
                    imageDist = imageService.hammingDistanceHex(saved.getImageHash(), c.getImageHash());
                } else imageDist = 999;
                double score = matchingService.combinedScore(saved, c, imageDist);
                if (score >= 0.35) {
                    Map<String,Object> m = new HashMap<>();
                    m.put("item", c);
                    m.put("score", score);
                    m.put("imageDistance", imageDist);
                    matches.add(m);
                }
            }
            matches.sort((a,b) -> Double.compare((Double)b.get("score"), (Double)a.get("score")));
            Map<String,Object> resp = new HashMap<>();
            resp.put("created", saved);
            resp.put("matches", matches);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public List<Item> listAll() { return itemRepository.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItem(@PathVariable Long id) {
        Optional<Item> opt = itemRepository.findById(id);
        if (opt.isPresent()) {
            return ResponseEntity.ok(opt.get());
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Not found"));
        }
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam String q) {
        String lower = q.toLowerCase();
        return itemRepository.findAll().stream()
                .filter(i -> (i.getTitle()!=null && i.getTitle().toLowerCase().contains(lower))
                          || (i.getDescription()!=null && i.getDescription().toLowerCase().contains(lower))
                          || (i.getLocation()!=null && i.getLocation().toLowerCase().contains(lower)))
                .collect(Collectors.toList());
    }
 // Example endpoint modification
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id, @RequestParam String username) {
        Optional<Item> itemOpt = itemRepository.findById(id);
        if (itemOpt.isEmpty()) return ResponseEntity.notFound().build();

        Item item = itemOpt.get();

        // Only admin or owner can delete
        if (!item.getPosterName().equals(username) && !username.equals("admin")) {
            return ResponseEntity.status(403).body(Map.of("message", "Access denied"));
        }

        itemRepository.delete(item);
        return ResponseEntity.ok(Map.of("message", "Item deleted"));
    }

}
