package com.kemham.kartukompetensi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kemham.kartukompetensi.R;
import com.kemham.kartukompetensi.model.Skill;
import com.kemham.kartukompetensi.util.KompetensiUtil;
import com.kemham.kartukompetensi.view.ScoreProgressBar;

/**
 * Adapter for displaying development areas in a RecyclerView.
 */
public class DevelopmentAreaAdapter extends ListAdapter<Skill, DevelopmentAreaAdapter.DevelopmentAreaViewHolder> {

    private final Context context;
    private final DevelopmentAreaClickListener listener;

    public interface DevelopmentAreaClickListener {
        void onDevelopmentAreaClick(Skill skill);
        void onActionButtonClick(Skill skill);
    }

    public DevelopmentAreaAdapter(Context context, DevelopmentAreaClickListener listener) {
        super(new DevelopmentAreaDiffCallback());
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DevelopmentAreaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_development_area, parent, false);
        return new DevelopmentAreaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DevelopmentAreaViewHolder holder, int position) {
        Skill skill = getItem(position);
        holder.bind(skill);
    }

    class DevelopmentAreaViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView categoryText;
        private final ScoreProgressBar scoreProgress;
        private final TextView gapText;
        private final TextView priorityText;
        private final ImageView priorityIcon;
        private final TextView recommendationText;
        private final View actionButton;
        private final View categoryIndicator;

        DevelopmentAreaViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.text_skill_name);
            categoryText = itemView.findViewById(R.id.text_category);
            scoreProgress = itemView.findViewById(R.id.progress_score);
            gapText = itemView.findViewById(R.id.text_gap);
            priorityText = itemView.findViewById(R.id.text_priority);
            priorityIcon = itemView.findViewById(R.id.icon_priority);
            recommendationText = itemView.findViewById(R.id.text_recommendation);
            actionButton = itemView.findViewById(R.id.button_action);
            categoryIndicator = itemView.findViewById(R.id.category_indicator);
        }

        void bind(Skill skill) {
            // Set basic info
            nameText.setText(skill.getName());
            categoryText.setText(getCategoryText(skill.getCategory()));

            // Set score progress
            int currentScore = skill.getScore();
            int targetScore = skill.getTargetScore();
            scoreProgress.setProgress(currentScore);
            scoreProgress.setMax(targetScore);
            scoreProgress.setProgressColor(KompetensiUtil.getProgressColor(context, currentScore, targetScore));

            // Calculate and display gap
            int gap = targetScore - currentScore;
            gapText.setText(context.getString(R.string.score_gap_format, gap));

            // Set priority level
            Skill.Level priority = skill.getPriorityLevel();
            setPriorityIndicator(priority);
            priorityText.setText(getPriorityText(priority));

            // Set recommendation if available
            String recommendation = skill.getRecommendation();
            if (recommendation != null && !recommendation.isEmpty()) {
                recommendationText.setVisibility(View.VISIBLE);
                recommendationText.setText(recommendation);
            } else {
                recommendationText.setVisibility(View.GONE);
            }

            // Set category indicator color
            setCategoryIndicator(skill.getCategory());

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDevelopmentAreaClick(skill);
                }
            });

            actionButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onActionButtonClick(skill);
                }
            });
        }

        private String getCategoryText(Skill.Category category) {
            int stringRes;
            switch (category) {
                case BASIC:
                    stringRes = R.string.category_basic;
                    break;
                case TECHNICAL:
                    stringRes = R.string.category_technical;
                    break;
                case EMERGING:
                    stringRes = R.string.category_emerging;
                    break;
                case POTENTIAL:
                    stringRes = R.string.category_potential;
                    break;
                default:
                    stringRes = R.string.category_unknown;
            }
            return context.getString(stringRes);
        }

        private void setPriorityIndicator(Skill.Level priority) {
            int iconRes;
            int tintRes;
            switch (priority) {
                case HIGH:
                    iconRes = R.drawable.ic_priority_high;
                    tintRes = R.color.priority_high;
                    break;
                case MEDIUM:
                    iconRes = R.drawable.ic_priority_medium;
                    tintRes = R.color.priority_medium;
                    break;
                case LOW:
                    iconRes = R.drawable.ic_priority_low;
                    tintRes = R.color.priority_low;
                    break;
                default:
                    priorityIcon.setVisibility(View.GONE);
                    return;
            }
            priorityIcon.setVisibility(View.VISIBLE);
            priorityIcon.setImageResource(iconRes);
            priorityIcon.setColorFilter(context.getResources().getColor(tintRes));
        }

        private String getPriorityText(Skill.Level priority) {
            int stringRes;
            switch (priority) {
                case HIGH:
                    stringRes = R.string.priority_high;
                    break;
                case MEDIUM:
                    stringRes = R.string.priority_medium;
                    break;
                case LOW:
                    stringRes = R.string.priority_low;
                    break;
                default:
                    stringRes = R.string.priority_unknown;
            }
            return context.getString(stringRes);
        }

        private void setCategoryIndicator(Skill.Category category) {
            int colorRes;
            switch (category) {
                case BASIC:
                    colorRes = R.color.category_basic;
                    break;
                case TECHNICAL:
                    colorRes = R.color.category_technical;
                    break;
                case EMERGING:
                    colorRes = R.color.category_emerging;
                    break;
                case POTENTIAL:
                    colorRes = R.color.category_potential;
                    break;
                default:
                    colorRes = R.color.category_default;
            }
            categoryIndicator.setBackgroundColor(context.getResources().getColor(colorRes));
        }
    }

    private static class DevelopmentAreaDiffCallback extends DiffUtil.ItemCallback<Skill> {
        @Override
        public boolean areItemsTheSame(@NonNull Skill oldItem, @NonNull Skill newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Skill oldItem, @NonNull Skill newItem) {
            return oldItem.equals(newItem);
        }
    }
}
