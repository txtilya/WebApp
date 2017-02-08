package model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Conference {
    private final int id;
    private final String name;

}
