import lombok.Value;

import java.util.*;
import java.util.stream.Collectors;

public class Day21 {

    @Value
    private static class Ingredient {
        String name;
    }

    @Value
    private static class Allergen {
        String name;
    }

    @Value
    private static class Food {
        Set<Ingredient> ingredients;
        Set<Allergen> allergens;
    }

    private static List<Food> allFoods = new ArrayList<>();
    private static final Set<Ingredient> allIngredients = new HashSet<>();
    private static final Set<Allergen> allAllergens = new HashSet<>();

    public static void execute() {
        allFoods = getFoods(FileReader.getFileReader().readFile("input21.csv"));
        Map<Allergen, Ingredient> allergenIngredientMap = getAllergenIngredientMap();
        Set<Ingredient> ingredientsWithoutAllergen = findIngredientsWithoutAllergens(allergenIngredientMap);
        countNumberOfTimesIngredientsWithoutAllergenAppear(ingredientsWithoutAllergen);
        sortAllergenIngredientMap(allergenIngredientMap);
    }

    private static List<Food> getFoods(List<String> lines) {
        return lines.stream().map(line -> {
            String[] containsSplit = line.split(" \\(contains ");
            Set<Ingredient> ingredients = Arrays.stream(containsSplit[0].split(" ")).map(Ingredient::new).collect(Collectors.toSet());
            Set<Allergen> allergens = Arrays.stream(containsSplit[1].substring(0, containsSplit[1].length() - 1).split(", ")).map(Allergen::new).collect(Collectors.toSet());
            allIngredients.addAll(ingredients);
            allAllergens.addAll(allergens);
            return new Food(ingredients, allergens);
        }).collect(Collectors.toList());
    }

    private static void sortAllergenIngredientMap(Map<Allergen, Ingredient> allergenIngredientMap) {
        List<Allergen> allergens = new ArrayList<>(allergenIngredientMap.keySet());
        allergens.sort(Comparator.comparing(Allergen::getName));
        StringBuilder result = new StringBuilder();
        for (Allergen allergen : allergens) {
            if (!(result.toString().equals(""))) result.append(",");
            result.append(allergenIngredientMap.get(allergen).name);
        }
        System.out.println(result);
    }

    private static void countNumberOfTimesIngredientsWithoutAllergenAppear(Set<Ingredient> ingredientsWithoutAllergen) {
        int numberOfTimes = 0;
        for (Ingredient ingredient: ingredientsWithoutAllergen) {
            numberOfTimes += allFoods.stream().filter(food -> food.ingredients.contains(ingredient)).count();
        }
        System.out.println("number of times food without allergen appear: " + numberOfTimes);
    }

    private static Set<Ingredient> findIngredientsWithoutAllergens(Map<Allergen, Ingredient> allergenIngredientMap) {
        Set<Ingredient> ingredientsWithoutAllergens = new HashSet<>(allIngredients);
        ingredientsWithoutAllergens.removeAll(allergenIngredientMap.values());
        System.out.println("number of ingredients without allergen: " + ingredientsWithoutAllergens.size());
        return ingredientsWithoutAllergens;
    }

    private static Map<Allergen, Ingredient> getAllergenIngredientMap() {
        Map<Allergen, Ingredient> allergenIngredientMap = new HashMap<>();
        Map<Allergen, Set<Ingredient>> possibleIngredientsMap = new HashMap<>();
        allAllergens.forEach(allergen -> possibleIngredientsMap.put(allergen, new HashSet<>(allIngredients)));
        while (allergenIngredientMap.size() < allAllergens.size()) {
            Set<Allergen> allergensToAssign = new HashSet<>(allAllergens);
            allergensToAssign.removeAll(allergenIngredientMap.keySet());
            allergensToAssign.forEach(allergen -> {
                List<Food> relevantFoods = allFoods.stream().filter(food -> food.allergens.contains(allergen)).collect(Collectors.toList());
                for (Food food : relevantFoods) {
                    Set<Ingredient> ingredients = food.ingredients;
                    Set<Ingredient> possibleIngredients = possibleIngredientsMap.get(allergen);
                    possibleIngredients.retainAll(ingredients);
                    if (possibleIngredients.size() == 1) {
                        Ingredient ingredient = possibleIngredients.stream().findFirst().orElseThrow();
                        allergenIngredientMap.put(allergen, ingredient);
                        possibleIngredientsMap.remove(allergen);
                        possibleIngredientsMap.values().forEach(s -> {
                            s.remove(ingredient);
                        });
                        break;
                    }
                };
            });
        }
        return allergenIngredientMap;
    }
}
