import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Day22 {

    @Value
    private static class Stats {
        int hitPoints;
        int damage;
        int armor;
        int mana;
    }

    @Data
    @AllArgsConstructor
    private static class Player {
        String name;
        int hitPoints;
        int damage;
        int armor;
        int mana;
        Map<SpellType, Spell> spells = new HashMap<>();

        public Player(String name, int hitPoints, int damage, int armor, int mana) {
            this.name = name;
            this.hitPoints = hitPoints;
            this.damage = damage;
            this.armor = armor;
            this.mana = mana;
        }

        public void castSpell(Spell spell) {
            //Pay mana costs
            mana -= spell.cost;
            gameState.manaSpent += spell.cost;
            if(mana < 0) { throw new IllegalStateException("Can not afford spell"); }

            //Cast spell
            if (spell.castMessage != null) System.out.println(spell.castMessage);
            spell.applyImmediateEffect();

            //Check spell is inactive, otherwise add to pool
            if (spell.effects.size() > 0) {
                if (isSpellActive(spell.type)) {
                    throw new IllegalStateException("Spell type already active");
                }
                spells.put(spell.type, spell);
            }
        }

        public boolean isSpellActive(SpellType type) {
            return spells.containsKey(type);
        }

        public void changeArmor(int amount) {
            armor += amount;
        }

        public void changeMana(int amount) {
            mana += amount;
        }

        public void applyForce(Player sourcePlayer) {
            int trueDamage = Math.max(1, sourcePlayer.damage - armor);
            if (armor > 0) {
                System.out.printf("%s attacks for %s - %s = %s damage!%n", sourcePlayer.name, sourcePlayer.damage, armor, trueDamage);
            } else {
                System.out.printf("%s attacks for %s damage!%n", sourcePlayer.name, trueDamage);
            }
            hitPoints -= trueDamage;
        }

        public void applyMagic(int amount) {
            hitPoints -= amount;
        }

        public void heal(int amount) { hitPoints += amount; }

        public void printStats() {
            if (this == gameState.player) {
                System.out.printf("- Player has %s hit points, %s armor, %s mana%n", hitPoints, armor, mana);
            }
            else if (this == gameState.boss) {
                System.out.printf("- Boss has %s hit points%n", hitPoints);
            }
        }
    }

    @Data
    @AllArgsConstructor
    private static class GameState {
        Player player;
        Player boss;
        int manaSpent;
        int turns;
    }

    private enum SpellType {
        MAGIC_MISSILE,
        DRAIN,
        SHIELD,
        POISON,
        RECHARGE
    }

    @Value
    private static class Spell {
        SpellType type;
        int cost;
        Runnable immediateEffect;
        Stack<Runnable> effects;
        String castMessage;
        Consumer<Spell> effectMessage;
        String spellEndedMessage;

        public void applyImmediateEffect() {
            if (immediateEffect != null) immediateEffect.run();
        }

        public void applyNextEffect() {
            effects.pop().run();
            if (effectMessage != null) effectMessage.accept(this);
        }
    }

    @Data
    private static class Strategy {
        List<SpellType> originalSpellTypes;
        List<SpellType> spellTypes;

        public Strategy(List<SpellType> spellTypes) {
            this.spellTypes = spellTypes;
        }

        private SpellType getNextSpellType() {
            return spellTypes.size() > 0 ? spellTypes.remove(0) : SpellType.values()[new Random().nextInt(SpellType.values().length)];
        }

        public Optional<SpellType> getNextActiveSpellType() {
            SpellType spellType = getNextSpellType();
            int ordinal = spellType.ordinal();
            while(getSpell(spellType).cost > gameState.player.mana || gameState.player.isSpellActive(spellType)) {
                int newOrdinal = Math.floorMod(spellType.ordinal() + 1, SpellType.values().length);
                if (newOrdinal == ordinal) return Optional.empty();
                spellType = SpellType.values()[newOrdinal];
            }
            return Optional.of(spellType);
        }
    }

//    private static class RandomStrategy extends Strategy {
//        SpellType getNextSpellType() {
//            Random random = new Random();
//            return SpellType.values()[random.nextInt(SpellType.values().length)];
//        }
//    }
//
//    private static class StrategyExample1 extends Strategy {
//        List<SpellType> spellTypes = new ArrayList<>();
//
//        public StrategyExample1() {
//            spellTypes.add(SpellType.POISON);
//            spellTypes.add(SpellType.MAGIC_MISSILE);
//        }
//
//        SpellType getNextSpellType() {
//            return spellTypes.remove(0);
//        }
//    }
//
//    private static class StrategyExample2 extends Strategy {
//        List<SpellType> spellTypes = new ArrayList<>();
//
//        public StrategyExample2() {
//            spellTypes.add(SpellType.RECHARGE);
//            spellTypes.add(SpellType.SHIELD);
//            spellTypes.add(SpellType.DRAIN);
//            spellTypes.add(SpellType.POISON);
//            spellTypes.add(SpellType.MAGIC_MISSILE);
//        }
//
//        SpellType getNextSpellType() {
//            return spellTypes.remove(0);
//        }
//    }

    private static Stats bossStats;
    private static Stats playerStats;
    private static GameState gameState;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input22.csv");
        readInitialStats(lines);

        List<Strategy> strategies = determineStrategies();
        int minManaSpent = 9999999;
        Strategy minStrategy = null;
        int minTurns = 999999;
        for (Strategy strategy: strategies) {
            System.out.println("----------------------- New game -------------------");
            System.out.println();
            initializeGameState();
            Player winningPlayer = executeGame(strategy);
            printEndStats(winningPlayer);
            if (winningPlayer == gameState.player) {
                if (gameState.manaSpent < minManaSpent) {
                    minManaSpent = gameState.manaSpent;
                    minStrategy = strategy;
                    minTurns = gameState.turns;
                }
            }
        }
        System.out.println();
        System.out.println("Minimum mana cost found: " + minManaSpent);
        System.out.println("Strategy: " + minStrategy.originalSpellTypes);
        System.out.println("Turns: " + minTurns);
    }

    private static List<Strategy> determineStrategies() {
        // Minimum mana cost found: 1309
        // Strategy: [POISON, RECHARGE, SHIELD, POISON, RECHARGE, SHIELD, POISON, MAGIC_MISSILE, MAGIC_MISSILE]
        // Turns: 9
        List<SpellType> spellTypes = new ArrayList<>();
        spellTypes.add(SpellType.POISON);
        spellTypes.add(SpellType.RECHARGE);
        spellTypes.add(SpellType.SHIELD);
        spellTypes.add(SpellType.POISON);
        spellTypes.add(SpellType.RECHARGE);
        spellTypes.add(SpellType.SHIELD);
        spellTypes.add(SpellType.POISON);
        spellTypes.add(SpellType.MAGIC_MISSILE);
        spellTypes.add(SpellType.MAGIC_MISSILE);
        strategyList.add(new Strategy(spellTypes));

//        addSpellType(new Strategy(new ArrayList<>()));
        for (Strategy strategy: strategyList) {
            strategy.originalSpellTypes = new ArrayList<>(strategy.spellTypes);
        }
        return strategyList;
    }

    private static final int strategyLength = 9;
    private static List<Strategy> strategyList = new ArrayList<>();

    private static void addSpellType(Strategy strategy) {
        if (strategy.spellTypes.size() == strategyLength) {
            strategyList.add(strategy);
        }
        else {
            for (SpellType spellType: SpellType.values()) {
                List<SpellType> spellTypes = new ArrayList<>(strategy.spellTypes);
                Strategy newStrategy = new Strategy(spellTypes);
                newStrategy.spellTypes.add(spellType);
                addSpellType(newStrategy);
            }
        }
    }

    private static void printEndStats(Player winningPlayer) {
        System.out.println();
        System.out.printf("%s wins the game!%n", winningPlayer.name);
        gameState.player.printStats();
        gameState.boss.printStats();
    }

    private static void readInitialStats(List<String> lines) {
        bossStats = new Stats(Integer.parseInt(lines.get(0).split("Hit Points: ")[1]),
                              Integer.parseInt(lines.get(1).split("Damage: ")[1]), 0, 0);
        playerStats = new Stats(50, 0, 0, 500);
    }

    private static void initializeGameState() {
        gameState = new GameState(new Player("Player", playerStats.hitPoints, playerStats.damage, playerStats.armor, playerStats.mana),
                                  new Player("Boss", bossStats.hitPoints, bossStats.damage, bossStats.armor, bossStats.mana), 0, 1);
    }

    private static Player executeGame(Strategy strategy) {
        Player currentPlayer = gameState.player;
        while(true) {
            //Print stats
            System.out.printf("-- %s turn %s --%n", currentPlayer.name, gameState.turns);
            gameState.player.printStats();
            gameState.boss.printStats();

            //lose 1 hit point
            gameState.player.hitPoints -= 1;

            //Apply spell effects
            Optional<Player> winningPlayer = applySpellEffects();
            if (winningPlayer.isPresent()) return winningPlayer.get();

            //Current player does action
            winningPlayer = doAction(currentPlayer, strategy);
            if (winningPlayer.isPresent()) return winningPlayer.get();

            //Change current player
            System.out.println();
            if (currentPlayer == gameState.boss) gameState.turns += 1;
            currentPlayer = currentPlayer == gameState.player ? gameState.boss : gameState.player;
        }
    }

    private static Optional<Player> checkWinningPlayer() {
        if (gameState.player.hitPoints <= 0) return Optional.of(gameState.boss);
        if (gameState.boss.hitPoints <= 0) return Optional.of(gameState.player);
        return Optional.empty();
    }

    private static Optional<Player> applySpellEffects() {
        Map<SpellType, Spell> spells = gameState.player.spells;
        for (Spell spell : spells.values()) {
            spell.applyNextEffect();
            Optional<Player> winningPlayer = checkWinningPlayer();
            if (winningPlayer.isPresent()) return winningPlayer;
            if (spell.effects.size() == 0) {
                if (spell.spellEndedMessage != null) System.out.println(spell.spellEndedMessage);
            }
        }
        List<SpellType> spellsToRemove = spells.values().stream().filter(spell -> spell.effects.size() == 0).map(spell -> spell.type).collect(Collectors.toList());
        spellsToRemove.forEach(spells::remove);
        return Optional.empty();
    }

    private static Optional<Player> doAction(Player player, Strategy strategy) {
        if (player == gameState.player) {
            Optional<SpellType> optionalSpellType = strategy.getNextActiveSpellType();
            if (optionalSpellType.isEmpty()) {
                System.out.println("Out of mana");
                return Optional.of(gameState.boss);
            }
            Spell spell = getSpell(optionalSpellType.get());
            player.castSpell(spell);
        }
        else {
            gameState.player.applyForce(gameState.boss);
            return checkWinningPlayer();
        }
        return checkWinningPlayer();
    }

    private static Spell getSpell(SpellType type) {
        int cost;
        Runnable immediateEffect = null;
        Stack<Runnable> effects = new Stack<>();
        String castMessage = null;
        Consumer<Spell> effectMessage = null;
        String spellEndedMessage = null;
        switch (type) {
            case MAGIC_MISSILE: {
                cost = 53;
                immediateEffect = () -> gameState.boss.applyMagic(4);
                castMessage = "Player casts Magic Missile, dealing 4 damage.";
            } break;
            case DRAIN: {
                cost = 73;
                immediateEffect = () -> {
                    gameState.boss.applyMagic(2);
                    gameState.player.heal(2); };
                castMessage = "Player casts Drain, dealing 2 damage, and healing 2 hit points.";
            } break;
            case SHIELD: {
                cost = 113;
                immediateEffect = () -> gameState.player.changeArmor(7);
                effects.push(() -> gameState.player.changeArmor(-7));
                pushEffect(effects, () -> {}, 5);
                castMessage = "Player casts Shield, increasing armor by 7.";
                effectMessage = (spell) -> System.out.printf("Shield's timer is now %s.%n", spell.effects.size());
                spellEndedMessage = "Shield wears off, decreasing armor by 7.";
            } break;
            case POISON: {
                cost = 173;
                pushEffect(effects, () -> gameState.boss.applyMagic(3), 6);
                castMessage = "Player casts Poison.";
                effectMessage = (spell) -> System.out.printf("Poison deals 3 damage; it's timer is now %s.%n", spell.effects.size());
                spellEndedMessage = "Poison wears off";
            } break;
            case RECHARGE: {
                cost = 229;
                pushEffect(effects, () -> gameState.player.changeMana(101), 5);
                castMessage = "Player casts Recharge.";
                effectMessage = (spell) -> System.out.printf("Recharge provides 101 mana; its timer is now %s.%n", spell.effects.size());
                spellEndedMessage = "Recharge wears off.";
            } break;
            default: throw new IllegalStateException("Invalid type");
        }
        return new Spell(type, cost, immediateEffect, effects, castMessage, effectMessage, spellEndedMessage);
    }

    private static void pushEffect(Stack<Runnable> effects, Runnable runnable, int numberOfTimes) {
        for (int i = 0; i < numberOfTimes; i++) {
            effects.push(runnable);
        }
    }

}
