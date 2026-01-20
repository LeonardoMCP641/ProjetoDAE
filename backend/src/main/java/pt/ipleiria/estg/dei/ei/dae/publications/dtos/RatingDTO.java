package pt.ipleiria.estg.dei.ei.dae.publications.dtos;

import java.io.Serializable;

public class RatingDTO implements Serializable {
    private int value;

    public RatingDTO() {}

    public RatingDTO(int value) {
        this.value = value;
    }

    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
}