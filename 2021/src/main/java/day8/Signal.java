package day8;

import lombok.Value;

import java.util.Set;

@Value
public class Signal {
    Set<Segment> segments;
}
