package day12;

import lombok.Value;
import networks.Network;

@Value
public class Cave implements Network.Node {

    String name;

    boolean small;

}
