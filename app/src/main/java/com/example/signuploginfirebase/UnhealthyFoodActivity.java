package com.example.signuploginfirebase;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnhealthyFoodActivity extends AppCompatActivity {

    private static final String TAG = UnhealthyFoodActivity.class.getSimpleName();

    private ListView foodListView;
    private List<Food> foodList;
    private List<Food> filteredFoodList;
    private FoodAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unhealthy_main);

        foodListView = findViewById(R.id.foodListView_unhealthy);

        String json = loadJSONFromAsset();
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray healthyArray = jsonObject.getJSONArray("unhealthy");

                foodList = new ArrayList<>();
                for (int i = 0; i < healthyArray.length(); i++) {
                    JSONObject foodObject = healthyArray.getJSONObject(i);
                    String foodName = foodObject.getString("food name");
                    String foodImage = foodObject.getString("food image");
                    String foodDescription = foodObject.getString("food description");
                    String foodCategory = foodObject.getString("food category");
                    String foodType = foodObject.getString("food type");
                    String foodDisbenefit = foodObject.getString("food disbenefit");
                    String foodAlternative = foodObject.getString("food alternative");
                    int foodCalories = foodObject.getInt("food calories");


                    List<String> ingredients = new ArrayList<>();

                    for (int j = 1; j <= 3; j++){
                        String ingredientKey = "ingredient " + j;

                        if (foodObject.has(ingredientKey)) {
                            String ingredient = foodObject.getString(ingredientKey);

                            ingredients.add(ingredient);
                        }
                    }

                    Food food = new Food(foodName, foodDescription, foodImage, foodCategory, foodType, foodCalories, foodDisbenefit,foodAlternative, ingredients);
                    foodList.add(food);
                    food.setImage(foodImage);
                }
                Spinner spinnerTags = findViewById(R.id.spinner_tags_unhealthy);
                ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.food_disbenefits, android.R.layout.simple_spinner_item);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTags.setAdapter(spinnerAdapter);
                spinnerTags.setSelection(spinnerAdapter.getPosition("All"));

                spinnerTags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedBenefit = parent.getItemAtPosition(position).toString();
                        filterFoodByBenefit(selectedBenefit);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                filteredFoodList = new ArrayList<>(foodList);

                adapter = new FoodAdapter(filteredFoodList);
                foodListView.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.searchEditText_unhealthy);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterFoodList(newText);
                return true;
            }
        });

        Button buttonDish = findViewById(R.id.button_dish_unhealthy);
        Button buttonDrink = findViewById(R.id.button_drink_unhealthy);
        Button buttonIngredient = findViewById(R.id.button_ingredient_unhealthy);
        Button buttonSnack = findViewById(R.id.button_snack_unhealthy);
        Button buttonMisc = findViewById(R.id.button_misc_unhealthy);

        buttonDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterFoodByType("Dish");
            }
        });

        buttonDrink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {filterFoodByType("Drink");}
        });

        buttonIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterFoodByType("Ingredient");
            }
        });

        buttonSnack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterFoodByType("Snack");
            }
        });

        buttonMisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterFoodByType("Misc.");
            }
        });
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("unhealthyfood.json");

            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    private void filterFoodList(String query) {
        filteredFoodList.clear();

        if (TextUtils.isEmpty(query)) {
            filteredFoodList.addAll(foodList);
        } else {
            query = query.toLowerCase();
            for (Food food : foodList) {
                if (food.getName().toLowerCase().contains(query)) {
                    filteredFoodList.add(food);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void filterFoodByType(String foodType){
        filteredFoodList.clear();

        for(Food food : foodList){
            if(food.getType().equalsIgnoreCase(foodType)){
                filteredFoodList.add(food);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private class Food {
        private String name;
        private String description;
        private String category;
        private String type;
        private int calories;
        private String disbenefit;
        private String alternative;
        private List<String> ingredients;
        private String image;


        public Food(String name, String description, String image, String category, String type, int calories, String disbenefit, String alternative, List<String> ingredients) {
            this.name = name;
            this.description = description;
            this.category = category;
            this.type = type;
            this.calories = calories;
            this.disbenefit = disbenefit;
            this.ingredients = ingredients;
            this.alternative = alternative;
            this.image = image;
        }

        public void setIngredients(List<String> ingredients) {
            this.ingredients = ingredients;
        }

        public String getName() {
            return name;
        }
        public String getDescription() {
            return description;
        }
        public String getCategory() {
            return category;
        }
        public String getType() {
            return type;
        }
        public int getCalories() {
            return calories;
        }
        public String getAlternative(){return alternative;}
        public String getDisbenefit() {
            return disbenefit;
        }
        public List<String> getIngredients() {
            return ingredients;
        }
        public void setImage(String image) {
            this.image = image;
        }
        public String getImage() {
            return image;
        }

    }

    private class FoodAdapter extends ArrayAdapter<Food> {

        public FoodAdapter(List<Food> foodList) {
            super(UnhealthyFoodActivity.this, 0, foodList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_food_unhealthy, parent, false);
            }

            Food food = getItem(position);

            TextView foodNameTextView = convertView.findViewById(R.id.foodNameTextView);
            TextView foodTypeTextView = convertView.findViewById(R.id.foodTypeTextView);
            TextView foodDisbenefitTextView = convertView.findViewById(R.id.foodDisbenefitTextView);
            ImageView foodImageView = convertView.findViewById(R.id.imageView_food);

            String resourceName = food.getImage();
            int resourceId = getResources().getIdentifier(resourceName, "drawable", getPackageName());

            if (resourceId != 0) {
                // Set the image resource
                foodImageView.setImageResource(resourceId);
            } else {
                // Set a default image resource or handle the case when the image resource is not found
                foodImageView.setImageResource(R.drawable.no_image);
            }

            foodNameTextView.setText(food.getName());
            foodTypeTextView.setText(food.getType());
            foodDisbenefitTextView.setText(food.getDisbenefit());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFoodDetailsLayout(food);
                }
            });

            return convertView;
        }
    }

    private void filterFoodByBenefit(String disbenefit) {
        filteredFoodList.clear();

        if (disbenefit.equalsIgnoreCase("All")) {
            filteredFoodList.addAll(foodList);
        } else {
            for (Food food : foodList) {
                if (food.getDisbenefit().equalsIgnoreCase(disbenefit)) {
                    filteredFoodList.add(food);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void openFoodDetailsLayout(Food food) {
        Intent intent = new Intent(UnhealthyFoodActivity.this, UnhealthyFoodDetailsActivity.class);
        intent.putExtra("food_name", food.getName());
        intent.putExtra("food_alternative", food.getAlternative());
        intent.putExtra("food_image", food.getImage());
        intent.putExtra("food_description", food.getDescription());
        intent.putExtra("food_category", food.getCategory());
        intent.putExtra("food_type", food.getType());
        intent.putExtra("food_calories", food.getCalories());
        intent.putExtra("food_disbenefit", food.getDisbenefit());
        intent.putExtra("food_ingredients", new ArrayList<>(food.getIngredients()));
        startActivity(intent);
    }
}
