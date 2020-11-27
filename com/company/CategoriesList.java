package com.company;
import java.util.Vector;

public class CategoriesList {
    Vector<Category> categories;

    CategoriesList(){
        categories = new Vector<Category>();
    }

    //add category
    //-1 if hours already exist
    //0 if successful
    int addTask(Task t, int category){
        if(categories.get(category).getTasks().contains(t.getName())){
            return -1;
        }
        categories.get(category).addTask(t);
        return 0;
    }

    void addCategory(String color){
        categories.add(new Category(color));
    }

    public Vector<Category> getCategories() {
        return categories;
    }

    public void setCategories(Vector<Category> categories) {
        this.categories = categories;
    }
}
