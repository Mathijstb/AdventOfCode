package day15;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Risk {

    int enterLevel;

    List<Risk> shortestPath;

    int totalLevel;
}
