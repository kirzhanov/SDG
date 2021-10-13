package by.sdg.controller;

import by.sdg.exceptions.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("messages")
public class MessageController {

    private List<Map<String, String>> messages = fillMapByTestMessages();
    private int counter = messages.size() + 1;

    @GetMapping
    public List<Map<String, String>> getMessages() {
        return messages;
    }

    @GetMapping("{id}")
    public Map<String, String> getMessage(@PathVariable String id) {
        return findMessage(id);
    }

    @PostMapping
    public Map<String, String> addMessage(@RequestBody Map<String, String> newMessage) {
        newMessage.put("id", String.valueOf(counter++));
        messages.add(newMessage);
        return newMessage;
    }

    @PutMapping("{id}")
    public Map<String, String> updateMessage(@PathVariable String id, @RequestBody Map<String, String> message) {
        Map<String, String> messageToUpdate = findMessage(id);
        messageToUpdate.putAll(message);
        messageToUpdate.put("id", id);
        return messageToUpdate;
    }

    @DeleteMapping("{id}")
    public void deleteMessage(@PathVariable String id) {
        Map<String, String> messageToDelete = findMessage(id);
        messages.remove(messageToDelete);
    }

    private Map<String, String> findMessage(String id) {
        return messages.stream()
                .filter(msg -> msg.get("id").equals(id))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

    private ArrayList<Map<String, String>> fillMapByTestMessages() {
        return new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("id", "1");
                put("message_body", "First message");
            }});
            add(new HashMap<String, String>() {{
                put("id", "2");
                put("message_body", "Second message");
            }});
            add(new HashMap<String, String>() {{
                put("id", "3");
                put("message_body", "Third message");
            }});
        }};
    }
}
