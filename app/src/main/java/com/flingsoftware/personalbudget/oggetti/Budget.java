/*
 * Copyright (c) This code was written by iClaude. All rights reserved.
 */

package com.flingsoftware.personalbudget.oggetti;

import android.content.Context;

import com.flingsoftware.personalbudget.R;

/**
 * POJO representing a budget.
 */

public class Budget {

    // Variables.
    private long id;
    private String tag;
    private double amount;
    private String repetition;
    private double expenses;
    private long dateStart;
    private long dateEnd;
    private int addRest;
    private long firstBudget;
    private int lastAdded;
    private double savings;


    public Budget() {
    }

    public Budget(int addRest, double amount, long dateEnd, long dateStart, double expenses, long firstBudget, long id, int lastAdded, String repetition, String tag) {
        this.addRest = addRest;
        this.amount = amount;
        this.dateEnd = dateEnd;
        this.dateStart = dateStart;
        this.expenses = expenses;
        this.firstBudget = firstBudget;
        this.id = id;
        this.lastAdded = lastAdded;
        this.repetition = repetition;
        this.tag = tag;
    }

    public int getAddRest() {
        return addRest;
    }

    public double getAmount() {
        return amount;
    }

    public long getDateEnd() {
        return dateEnd;
    }

    public long getDateStart() {
        return dateStart;
    }

    public double getExpenses() {
        return expenses;
    }

    public long getFirstBudget() {
        return firstBudget;
    }

    public long getId() {
        return id;
    }

    public int getLastAdded() {
        return lastAdded;
    }

    public String getRepetition() {
        return repetition;
    }

    public String getTag() {
        return tag;
    }

    public double getSavings() {
        return savings;
    }

    public void setAddRest(int addRest) {
        this.addRest = addRest;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDateEnd(long dateEnd) {
        this.dateEnd = dateEnd;
    }

    public void setDateStart(long dateStart) {
        this.dateStart = dateStart;
    }

    public void setExpenses(double expenses) {
        this.expenses = expenses;
    }

    public void setFirstBudget(long firstBudget) {
        this.firstBudget = firstBudget;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLastAdded(int lastAdded) {
        this.lastAdded = lastAdded;
    }

    public void setRepetition(String repetition) {
        this.repetition = repetition;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setSavings(double savings) {
        this.savings = savings;
    }

    // Used for display in the UI.
    public String getTagWithoutComma() {
        String newTag = getTag();
        if (newTag.endsWith(",")) {
            newTag = tag.substring(0, tag.length() - 1);
        }
        return newTag;
    }

    /*
    Given the String representing the repetition of this budget, returns a String representing
    the repetition in the correct language (bad code: legacy approach).
 */
    public String getBudgetType(Context context) {
        String[] budgetTypes = context.getResources().getStringArray(R.array.ripetizioni_budget);
        String budgetType = getRepetition();
        if (budgetType.equals("una_tantum")) {
            budgetType = budgetTypes[0];
        } else if (budgetType.equals("giornaliero")) {
            budgetType = budgetTypes[1];
        } else if (budgetType.equals("settimanale")) {
            budgetType = budgetTypes[2];
        } else if (budgetType.equals("bisettimanale")) {
            budgetType = budgetTypes[3];
        } else if (budgetType.equals("mensile")) {
            budgetType = budgetTypes[4];
        } else if (budgetType.equals("annuale")) {
            budgetType = budgetTypes[5];
        }

        return budgetType;
    }
}
