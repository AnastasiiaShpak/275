/*
List of categories
Maximum number of categories = 50

CMPT275 Project
Group 21
 */

package com.company;
import java.util.Vector;

public class CategoriesList {
    Vector<Category> categories;

    public CategoriesList(){
        categories = new Vector<Category>();
    }
    public int getSize(){
        return categories.size();
    }

    //-1 if invalid name
    //-2 if name already exists
    // -3 exceed number of categories
    //0 if successful
    public int addCategory(String name){
        if(name.length() > 25 || name.equals(""))
            return -1;

        if(categories.size() == 50)
            return -3;

        for(Category c: categories){
            if(c.getName().equals(name))
                return -2;
        }
        categories.add(new Category(name));
        return 0;
    }

    //-1 if there is no category with this name
    //0 is successful
    public int removeCategory(String name){
        for(Category c: categories){
            if(c.getName().equals(name)){
                c.clear();
                categories.remove(c);
                return 0;
            }
        }
        return -1;
    }

    public Category getCategory(String name){
        for(Category c: categories) {
            if (c.getName().equals(name))
                return c;
        }
        return null;
    }
}
