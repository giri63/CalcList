package com.aks.calclist;

import java.util.List;

public class ListItemEntry {
    String listID;
    String listName;
    int listCount;
    int listAmount;
    String listTime;

    public List<String> getListItems() {
        return listItems;
    }

    public void setListItems(List<String> listItems) {
        this.listItems = listItems;
    }

    List<String> listItems;

    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public int getListCount() {
        return listCount;
    }

    public void setListCount(int listCount) {
        this.listCount = listCount;
    }

    public int getListAmount() {
        return listAmount;
    }

    public void setListAmount(int listAmount) {
        this.listAmount = listAmount;
    }

    public String getListTime() {
        return listTime;
    }

    public void setListTime(String listTime) {
        this.listTime = listTime;
    }

}
