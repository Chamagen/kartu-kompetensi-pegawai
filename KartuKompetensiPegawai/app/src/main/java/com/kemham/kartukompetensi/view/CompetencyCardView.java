package com.kemham.kartukompetensi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.kemham.kartukompetensi.R;
import com.kemham.kartukompetensi.model.Skill;
import com.kemham.kartukompetensi.util.KompetensiUtil;

import java.util.List;

/**
 * Custom view that displays a competency card with score and details.
 */
public class CompetencyCardView extends CardView {

    private TextView titleText;
    private TextView scoreText;
    private TextView countText;
    private ScoreProgressBar progressBar;
    private ImageView iconView;
    private LinearLayout skillsContainer;
    private View expandButton;
    private View addButton;

    private String title;
    private int score;
    private int targetScore;
    private int iconResId;
    private boolean isExpanded;
    private List<Skill> skills;
    private CompetencyCardListener listener;

    public interface CompetencyCardListener {
        void onCardClick();
        void onExpandClick();
        void onAddClick();
        void onSkillClick(Skill skill);
    }

    public CompetencyCardView(Context context) {
        super(context);
        init(null);
    }

    public CompetencyCardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CompetencyCardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        // Inflate layout
        LayoutInflater.from(getContext()).inflate(R.layout.view_competency_card, this, true);

        // Find views
        titleText = findViewById(R.id.text_title);
        scoreText = findViewById(R.id.text_score);
        countText = findViewById(R.id.text_count);
        progressBar = findViewById(R.id.progress_score);
        iconView = findViewById(R.id.icon_competency);
        skillsContainer = findViewById(R.id.container_skills);
        expandButton = findViewById(R.id.button_expand);
        addButton = findViewById(R.id.button_add);

        // Get attributes
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CompetencyCardView);
            title = a.getString(R.styleable.CompetencyCardView_cardTitle);
            score = a.getInteger(R.styleable.CompetencyCardView_cardScore, 0);
            targetScore = a.getInteger(R.styleable.CompetencyCardView_cardTargetScore, 100);
            iconResId = a.getResourceId(R.styleable.CompetencyCardView_cardIcon, 0);
            isExpanded = a.getBoolean(R.styleable.CompetencyCardView_isExpanded, false);
            a.recycle();
        }

        // Set initial values
        titleText.setText(title);
        updateScore(score, targetScore);
        if (iconResId != 0) {
            iconView.setImageResource(iconResId);
        }
        updateExpandState();

        // Set click listeners
        setOnClickListener(v -> {
            if (listener != null) {
                listener.onCardClick();
            }
        });

        expandButton.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            updateExpandState();
            if (listener != null) {
                listener.onExpandClick();
            }
        });

        addButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddClick();
            }
        });
    }

    private void updateScore(int score, int targetScore) {
        this.score = score;
        this.targetScore = targetScore;

        // Update progress bar
        progressBar.setMax(targetScore);
        progressBar.setProgress(score);
        progressBar.setProgressColor(KompetensiUtil.getProgressColor(getContext(), score, targetScore));

        // Update score text
        scoreText.setText(String.format("%d/%d", score, targetScore));

        // Update count text if skills are available
        if (skills != null) {
            countText.setText(String.format("%d %s", skills.size(), 
                getResources().getString(R.string.skills_count)));
        }
    }

    private void updateExpandState() {
        skillsContainer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        expandButton.setRotation(isExpanded ? 180 : 0);
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
        updateSkillsList();
        updateScore(score, targetScore);
    }

    private void updateSkillsList() {
        skillsContainer.removeAllViews();
        if (skills != null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            for (Skill skill : skills) {
                View skillView = inflater.inflate(R.layout.item_skill_compact, skillsContainer, false);
                bindSkillView(skillView, skill);
                skillsContainer.addView(skillView);
            }
        }
    }

    private void bindSkillView(View view, Skill skill) {
        TextView nameText = view.findViewById(R.id.text_skill_name);
        ScoreProgressBar scoreBar = view.findViewById(R.id.progress_score);
        TextView scoreText = view.findViewById(R.id.text_score);

        nameText.setText(skill.getName());
        scoreBar.setMax(skill.getTargetScore());
        scoreBar.setProgress(skill.getScore());
        scoreBar.setProgressColor(KompetensiUtil.getProgressColor(getContext(), 
            skill.getScore(), skill.getTargetScore()));
        scoreText.setText(String.format("%d/%d", skill.getScore(), skill.getTargetScore()));

        view.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSkillClick(skill);
            }
        });
    }

    public void setListener(CompetencyCardListener listener) {
        this.listener = listener;
    }

    public void setTitle(String title) {
        this.title = title;
        titleText.setText(title);
    }

    public void setScore(int score, int targetScore) {
        updateScore(score, targetScore);
    }

    public void setIcon(int resId) {
        this.iconResId = resId;
        iconView.setImageResource(resId);
    }

    public void setExpanded(boolean expanded) {
        this.isExpanded = expanded;
        updateExpandState();
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public String getTitle() {
        return title;
    }

    public int getScore() {
        return score;
    }

    public int getTargetScore() {
        return targetScore;
    }

    public List<Skill> getSkills() {
        return skills;
    }
}
