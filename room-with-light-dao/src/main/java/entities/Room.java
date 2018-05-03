package entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Room {
    private Long id;
//    private String name = "";
    private String name;
    private Boolean lightState;

    public Room(String name, Boolean lightState) {
        this.name = name;
        this.lightState = lightState;
    }
}
