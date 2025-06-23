package com.kemham.kartukompetensi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kemham.kartukompetensi.R;
import com.kemham.kartukompetensi.adapter.DevelopmentAreaAdapter;
import com.kemham.kartukompetensi.model.Skill;

import java.util.List;

/**
 * Custom view that displays development areas with recommendations.
 */
public class DevelopmentAreaView extends CardView {

    private TextView titleText;
    private TextView countText;
    private TextView emptyText;
    private RecyclerView recyclerView;
    private View expandButton;
    private LinearLayout contentContainer;

    private DevelopmentAreaAdapter adapter;
    private boolean isExpanded;
    private DevelopmentAreaListener listener;
    private List<Skill> developmentAreas;

    public interface DevelopmentAreaListener {
        void onDevelopmentAreaClick(Skill skill);
        void onActionButtonClick(Skill skill);
        void onExpandClick(boolean isExpanded);
    }

    public DevelopmentAreaView(Context context) {
        super(context);
        init(null);
    }

    public DevelopmentAreaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DevelopmentAreaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        // Inflate layout
        LayoutInflater.from(getContext()).inflate(R.layout.view_development_area, this, true);

        // Find views
        titleText = findViewById(R.id.text_title);
        countText = findViewById(R.id.text_count);
        emptyText = findViewById(R.id.text_empty);
        recyclerView = findViewById(R.id.recycler_development_areas);
        expandButton = findViewById(R.id.button_expand);
        contentContainer = findViewById(R.id.container_content);

        // Get attributes
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DevelopmentAreaView);
            String title = a.getString(R.styleable.DevelopmentAreaView_areaTitle);
            isExpanded = a.getBoolean(R.styleable.DevelopmentAreaView_isExpanded, false);
            a.recycle();

            if (title != null) {
                titleText.setText(title);
            }
        }

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DevelopmentAreaAdapter(getContext(), new DevelopmentAreaAdapter.DevelopmentAreaClickListener() {
            @Override
            public void onDevelopmentAreaClick(Skill skill) {
                if (listener != null) {
                    listener.onDevelopmentAreaClick(skill);
                }
            }

            @Override
            public void onActionButtonClick(Skill skill) {
                if (listener != null) {
                    listener.onActionButtonClick(skill);
                }
            }
        });
        recyclerView.setAdapter(adapter);

        // Setup expand button
        expandButton.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            updateExpandState();
            if (listener != null) {
                listener.onExpandClick(isExpanded);
            }
        });

        // Initial state
        updateExpandState();
    }

    private void updateExpandState() {
        contentContainer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        expandButton.setRotation(isExpanded ? 180 : 0);
    }

    private void updateEmptyState() {
        boolean isEmpty = developmentAreas == null || developmentAreas.isEmpty();
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    public void setDevelopmentAreas(List<Skill> areas) {
        this.developmentAreas = areas;
        adapter.submitList(areas);
        updateEmptyState();

        // Update count
        int count = areas != null ? areas.size() : 0;
        countText.setText(getResources().getQuantityString(
            R.plurals.development_areas_count, count, count));
    }

    public void setListener(DevelopmentAreaListener listener) {
        this.listener = listener;
    }

    public void setExpanded(boolean expanded) {
        if (this.isExpanded != expanded) {
            this.isExpanded = expanded;
            updateExpandState();
        }
    }

    public void setTitle(String title) {
        titleText.setText(title);
    }

    public void setEmptyMessage(String message) {
        emptyText.setText(message);
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public List<Skill> getDevelopmentAreas() {
        return developmentAreas;
    }

    public void addDevelopmentArea(Skill skill) {
        if (developmentAreas != null) {
            developmentAreas.add(skill);
            adapter.submitList(developmentAreas);
            updateEmptyState();
        }
    }

    public void removeDevelopmentArea(Skill skill) {
        if (developmentAreas != null) {
            developmentAreas.remove(skill);
            adapter.submitList(developmentAreas);
            updateEmptyState();
        }
    }

    public void updateDevelopmentArea(Skill skill) {
        if (developmentAreas != null) {
            int index = developmentAreas.indexOf(skill);
            if (index != -1) {
                developmentAreas.set(index, skill);
                adapter.submitList(developmentAreas);
            }
        }
    }

    public void clearDevelopmentAreas() {
        if (developmentAreas != null) {
            developmentAreas.clear();
            adapter.submitList(developmentAreas);
            updateEmptyState();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Cleanup
        recyclerView.setAdapter(null);
    }
}
