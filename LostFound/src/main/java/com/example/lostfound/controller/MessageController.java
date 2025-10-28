package com.example.lostfound.controller;

import com.example.lostfound.model.Item;
import com.example.lostfound.model.Message;
import com.example.lostfound.repository.ItemRepository;
import com.example.lostfound.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ItemRepository itemRepository;

    @PostMapping
    public ResponseEntity<?> postMessage(@RequestParam Long itemId,
                                         @RequestParam String fromName,
                                         @RequestParam String fromContact,
                                         @RequestParam String content) {
        Optional<Item> opt = itemRepository.findById(itemId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Item not found"));
        }
        Message m = new Message();
        m.setItem(opt.get());
        m.setFromName(fromName);
        m.setFromContact(fromContact);
        m.setContent(content);
        messageRepository.save(m);
        return ResponseEntity.ok(m);
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<?> messagesForItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(messageRepository.findByItemId(itemId));
    }
}
