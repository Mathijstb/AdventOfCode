package day8;

import lombok.Value;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

@Value
public class Display {

    List<Signal> signals;
    List<Digit> digits;

    public int getNumberOfDisplayValues(int displayValue) {
        switch (displayValue) {
            case 1: return (int) digits.stream().filter(d -> d.getLightedSegments().size() == 2).count();
            case 4: return (int) digits.stream().filter(d -> d.getLightedSegments().size() == 4).count();
            case 7: return (int) digits.stream().filter(d -> d.getLightedSegments().size() == 3).count();
            case 8: return (int) digits.stream().filter(d -> d.getLightedSegments().size() == 7).count();
            default: throw new NotImplementedException();
        }
    }
}
