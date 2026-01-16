package pt.ipleiria.estg.dei.ei.dae.publications.dtos;

import pt.ipleiria.estg.dei.ei.dae.publications.entities.Tag;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class TagDTO implements Serializable {

    private long id;
    private String name;

    public TagDTO() {
    }

    public TagDTO(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static TagDTO from(Tag tag) {
        return new TagDTO(
                tag.getId(),
                tag.getName()
        );
    }

    public static List<TagDTO> from(List<Tag> tags) {
        return tags.stream().map(TagDTO::from).collect(Collectors.toList());
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
