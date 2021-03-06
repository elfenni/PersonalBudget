/*
 * Copyright (c) This code was written by iClaude. All rights reserved.
 */

package com.flingsoftware.personalbudget.app.budgets;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flingsoftware.personalbudget.R;
import com.flingsoftware.personalbudget.customviews.TextViewWithBackground;
import com.flingsoftware.personalbudget.database.DBCSpeseBudget;
import com.flingsoftware.personalbudget.database.DBCSpeseVoci;
import com.flingsoftware.personalbudget.database.DBCVociAbs;
import com.flingsoftware.personalbudget.oggetti.Budget;
import com.flingsoftware.personalbudget.utility.ListViewIconeVeloce;
import com.flingsoftware.personalbudget.utility.NumberFormatter;
import com.flingsoftware.personalbudget.utility.UtilityVarious;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Fragment used to display the history of budgets of the same type of this one.
 */

public class BudgetDetailsHistory extends Fragment implements BudgetDetails.ReloadingData {

    // Constants.
    private static final String TAG = "BUDGETS";
    private static final String BUDGET_ID = "BUDGET_ID";

    // Variables.
    private long budgetId;
    private Bitmap iconBitmap;

    // Widgets and layout.
    private TextView tvLoading;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;


    // Add possible variables to pass to the Fragment.
    public static BudgetDetailsHistory newInstance(long id) {
        Bundle args = new Bundle();
        args.putLong(BUDGET_ID, id);
        BudgetDetailsHistory budgetDetailsHistory = new BudgetDetailsHistory();
        budgetDetailsHistory.setArguments(args);
        return budgetDetailsHistory;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        budgetId = getArguments().getLong(BUDGET_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.budget_history, container, false);

        // Get the budgets' history.
        new GetBudgetHistoryTask().execute(budgetId);

        tvLoading = (TextView) view.findViewById(R.id.tvLoading);
        // Set up RecyclerView.
        recyclerView = (RecyclerView) view.findViewById(R.id.rvBudgets);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BudgetAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }

    // Retrieve the history of budgets of the same type, passing the id of this budget.
    private class GetBudgetHistoryTask extends AsyncTask<Long, Object, Void> {
        private final List<Budget> listBudget = new ArrayList<>(10);
        private final DBCVociAbs dbcVociAbs = new DBCSpeseVoci(getActivity());

        protected Void doInBackground(Long... params) {
            // Get the history of budgets.
            // Get the id of the original budget.
            DBCSpeseBudget dbcSpeseBudget = new DBCSpeseBudget(getActivity());
            dbcSpeseBudget.openLettura();
            Cursor cursor = dbcSpeseBudget.getSpesaBudget(params[0]);
            cursor.moveToFirst();
            long startBudget = cursor.getLong(cursor.getColumnIndex("budget_iniziale"));
            // Get a cursor containing the history of budgets of the same type.
            cursor = dbcSpeseBudget.getSpeseBudgetElencoBudgetAnaloghi(startBudget);
            // Convert the cursor in a List.
            while (cursor.moveToNext()) {
                listBudget.add(Budget.makeBudgetFromCursor(cursor, getActivity()));
            }
            cursor.close();
            dbcSpeseBudget.close();

            if (listBudget.size() == 0) return null;

            // Get the bitmap for the icon.
            // Get the icon id from the tag.
            dbcVociAbs.openLettura();
            Cursor curVoci = dbcVociAbs.getTutteLeVociFiltrato(listBudget.get(0).getTag()); // all budgets are of the same type, so the icon is the same
            int iconaId = R.drawable.tag_1;
            if (curVoci.moveToFirst()) {
                int icona = curVoci.getInt(curVoci.getColumnIndex("icona"));
                iconaId = ListViewIconeVeloce.arrIconeId[icona];
            }
            final int iconaIdDef = iconaId;
            curVoci.close();
            dbcVociAbs.close();
            // Create the bitmap.
            iconBitmap = ListViewIconeVeloce.decodeSampledBitmapFromResource(getResources(), iconaIdDef, 56, 56);

            return null;
        }

        protected void onPostExecute(Void nothing) {
            if (listBudget.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                tvLoading.setVisibility(View.GONE);
                ((BudgetAdapter) adapter).setList(listBudget);
            }
        }
    }

    // Adapter used by the RecyclerView to get its data.
    public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

        // ViewHolder.
        public class BudgetViewHolder extends RecyclerView.ViewHolder {
            private final TextView tvPeriod;
            private final TextView tvTag;
            private final TextView tvSaved;
            private final TextView tvPerc;
            private final ProgressBar pbBudget;
            private final TextView tvAmount;
            private final TextView tvSpent;
            private final TextView tvBudgetType;
            private final TextView tvEndDate;
            private final ImageView ivIcon;
            private final TextView tvShowHide;
            private final ImageButton ibShowHide;
            private final View vExpandedContent;

            public BudgetViewHolder(View view) {
                super(view);
                tvPeriod = (TextView) view.findViewById(R.id.tvPeriod);
                tvTag = (TextView) view.findViewById(R.id.tvTag);
                tvSaved = (TextView) view.findViewById(R.id.tvSaved);
                tvPerc = (TextView) view.findViewById(R.id.tvPerc);
                pbBudget = (ProgressBar) view.findViewById(R.id.pbEarnings);
                tvAmount = (TextView) view.findViewById(R.id.tvAmount);
                tvSpent = (TextView) view.findViewById(R.id.tvSpent);
                tvBudgetType = (TextView) view.findViewById(R.id.tvBudgetType);
                tvEndDate = (TextView) view.findViewById(R.id.tvEndDate);
                tvShowHide = (TextView) view.findViewById(R.id.tvShowHide);
                vExpandedContent = view.findViewById(R.id.layout_expanded);
                vExpandedContent.setVisibility(View.GONE);
                ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
                ibShowHide = (ImageButton) view.findViewById(R.id.ibShowHide);
                ibShowHide.setOnClickListener(new View.OnClickListener() {
                                                  boolean collapsed = true;

                                                  @Override
                                                  public void onClick(View view) {
                                                      if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                          TransitionManager.beginDelayedTransition(recyclerView);
                                                      }
                                                      if (collapsed) {
                                                          ibShowHide.setImageResource(R.drawable.ic_navigation_collapse);
                                                          tvShowHide.setText(getString(R.string.budgets_hide));
                                                          vExpandedContent.setVisibility(View.VISIBLE);
                                                          collapsed = false;
                                                      } else {
                                                          ibShowHide.setImageResource(R.drawable.ic_navigation_expand);
                                                          tvShowHide.setText(getString(R.string.budgets_show));
                                                          vExpandedContent.setVisibility(View.GONE);
                                                          collapsed = true;
                                                      }

                                                      int pos = getAdapterPosition();
                                                      expanded[pos] = !collapsed;
                                                  }
                                              }
                );
            }
        }

        // BudgetsAdapter class.
        private List<Budget> listBudget = new ArrayList<>();
        private int tagColor;
        private boolean[] expanded;

        public BudgetAdapter() {
            tagColor = ContextCompat.getColor(getActivity(), R.color.tag_color_01);
        }


        public void setList(List<Budget> listBudget) {
            this.listBudget = listBudget;
            expanded = new boolean[listBudget.size()];
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return listBudget.size();
        }

        @Override
        public BudgetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_history_item, parent, false);
            return new BudgetViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(BudgetViewHolder holder, int position) {
            Budget budget = listBudget.get(position);

            if (budget != null) {
                Context context = getActivity();
                // Show budget's details in the layout.
                holder.ivIcon.setImageBitmap(iconBitmap);
                DateFormat dateFormat = UtilityVarious.getDateFormatShort();
                String period = dateFormat.format(new Date(budget.getDateStart())) + " - " + dateFormat.format(new Date(budget.getDateEnd()));
                holder.tvPeriod.setText(period);
                String tag = budget.getTag().contains(",") ? getString(R.string.budgets_history_multiple) : budget.getTag();
                holder.tvTag.setText(tag);
                ((TextViewWithBackground) holder.tvTag).setBackgroundColorPreserveBackground(tagColor);
                double saved = budget.getAmount() - budget.getExpenses();
                holder.tvSaved.setText(NumberFormatter.formatAmountColorCurrencySuperscript(saved, context), TextView.BufferType.SPANNABLE);

                int perc = Math.min(((int) ((budget.getExpenses() * 100) / budget.getAmount())), 100);
                holder.tvPerc.setText(getString(R.string.budget_dettaglio_speso) + ": " + perc + "%");
                if (perc >= 100) {
                    holder.pbBudget.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_accent));
                } else {
                    holder.pbBudget.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_standard));
                }
                holder.pbBudget.setProgress(perc);
                holder.tvAmount.setText(NumberFormatter.formatAmountMainCurrency(budget.getAmount(), context));
                holder.tvSpent.setText(NumberFormatter.formatAmountMainCurrency(budget.getExpenses(), context));
                holder.tvBudgetType.setText(budget.getBudgetType(context));
                holder.tvEndDate.setText(dateFormat.format(new Date(budget.getDateEnd())));

                /* This item's view could be recycled. So I check if the current item is expanded
                or recycled and show or hide the details. */
                if (expanded[position]) {
                    holder.ibShowHide.setImageResource(R.drawable.ic_navigation_collapse);
                    holder.tvShowHide.setText(getString(R.string.budgets_hide));
                    holder.vExpandedContent.setVisibility(View.VISIBLE);
                } else {
                    holder.ibShowHide.setImageResource(R.drawable.ic_navigation_expand);
                    holder.tvShowHide.setText(getString(R.string.budgets_show));
                    holder.vExpandedContent.setVisibility(View.GONE);
                }
            }
        }
    }

    /*
        Called from the BudgetDetails Activity when we have to reload data from the database (
        when the budget has been edited).
    */
    @Override
    public void reloadData() {
        new GetBudgetHistoryTask().execute(budgetId);
    }
}
