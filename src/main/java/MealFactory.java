import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class MealFactory {

    private List<Meal> mealList;

    public MealFactory(){
        MealsScraper meals = new MealsScraper();
        mealList = new ArrayList<>();
        // Plus 2 due to rowspan of date column
        for (int i = 0; i < 14; i+=2) {
            Meal meal = new Meal();
            Elements fixLunchTds = meals.getFix().get(i).getElementsByTag("td");
            Elements fixDinnerTds = meals.getFix().get(i + 1).getElementsByTag("td");
            Elements alternativeTds = meals.getAlternative().get(i / 2).getElementsByTag("td");
            // Scrape Date
            String date = fixLunchTds.get(0).text().split(" ")[0];
            meal.setDate(date);
          //  meal.setLunchHTML(meals.getFix().get(i).html());
           // meal.setDinnerHTML(meals.getFix().get(i + 1).html());


            // Scrape Fix Lunch Meals
            meal.setLunch(scrapeMeals(fixLunchTds.get(1)));
            meal.setDinner(scrapeMeals(fixDinnerTds.get(0)));
            meal.setAlternative(scrapeMeals(alternativeTds.get(1)));

            // Scrape Fix Lunch Nutritions
            String[] nutritions = fixLunchTds.get(2).text().replaceAll("[^\\d\\s]", "").trim().replaceAll("\\s{2,}", " ").split(" ");
            Meal.NutritionFacts nutritionFacts = meal.new NutritionFacts();
            nutritionFacts.setEnergyByCal(Integer.parseInt(nutritions[0]));
            nutritionFacts.setCarbohydratePercentage(Integer.parseInt(nutritions[1]));
            nutritionFacts.setProteinPercentage(Integer.parseInt(nutritions[2]));
            nutritionFacts.setFatPercentage(Integer.parseInt(nutritions[3]));
            meal.setNutritionFacts(nutritionFacts);

            mealList.add(meal);
        }
    }
    public List<Meal> getMealList(){
        return mealList;
    }
    private List<String[]> scrapeMeals(Element tds) {
        List<String[]> result = new ArrayList<>();
        String[] lines = tds.toString().replaceAll("&nbsp;", "").split("<br>");
        for (int a = 1; a < lines.length; a++){
            String line = Jsoup.parse(lines[a]).text();

            // String arrays consist of foods by 2 language
            if (line.contains("veya / or") && lines.length == 5){
                String[] secondLine = line.split("veya / or");

                String[] secondLineSplitted0 = secondLine[0].split("/");
                String[] food0 = new String[]{
                        secondLineSplitted0[0].trim(),
                        secondLineSplitted0[1].trim()
                };
                String[] secondLineSplitted1 = secondLine[1].split("/");
                String[] food1 = new String[]{
                        secondLineSplitted1[0].trim(),
                        secondLineSplitted1[1].trim()
                };
                result.add(food0);
                result.add(food1);
            }else{
                String[] lineSplitted = line.replaceAll("veya / or", "").split("/");
                String[] food = new String[]{
                        lineSplitted[0].trim(),
                        lineSplitted[1].trim()
                };
                result.add(food);
            }
        }
        return result;
    }
/*
    private String regexp(String str, String patternString){
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);
        return matcher.group();
    }*/
}
