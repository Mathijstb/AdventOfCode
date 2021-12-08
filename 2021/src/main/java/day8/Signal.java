package day8;

import lombok.Value;

import java.util.List;

@Value
public class Signal {
    List<Segment> segments;
}
