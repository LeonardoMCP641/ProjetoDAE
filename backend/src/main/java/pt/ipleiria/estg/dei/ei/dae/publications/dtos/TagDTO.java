package pt.ipleiria.estg.dei.ei.dae.publications.dtos;

import pt.ipleiria.estg.dei.ei.dae.publications.entities.Tag;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TagDTO implements Serializable {

    private long id;
    private String name;
    private List<String> subscriberUsernames;

    private int usageCount;

    public TagDTO() {
        this.subscriberUsernames = new ArrayList<>();
    }

    public TagDTO(long id, String name, List<String> subscriberUsernames, int usageCount) {
        this.id = id;
        this.name = name;
        this.subscriberUsernames = subscriberUsernames;
        this.usageCount = usageCount;
    }

    public static TagDTO from(Tag tag) {
        List<String> names = new ArrayList<>();

        if (tag.getSubscribers() != null) {
            names = tag.getSubscribers().stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList());
        }

        return new TagDTO(
                tag.getId(),
                tag.getName(),
                names,
                names.size()
        );
    }

    public static List<TagDTO> from(List<Tag> tags) {
        return tags.stream().map(TagDTO::from).collect(Collectors.toList());
    }

    // Getters e Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getSubscriberUsernames() { return subscriberUsernames; }
    public void setSubscriberUsernames(List<String> subscriberUsernames) { this.subscriberUsernames = subscriberUsernames; }

    public int getUsageCount() { return usageCount; }
    public void setUsageCount(int usageCount) { this.usageCount = usageCount; }
}