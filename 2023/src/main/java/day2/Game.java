package day2;

import lombok.Value;

import java.util.List;

public record Game(int id, List<Sample> samples) {}
