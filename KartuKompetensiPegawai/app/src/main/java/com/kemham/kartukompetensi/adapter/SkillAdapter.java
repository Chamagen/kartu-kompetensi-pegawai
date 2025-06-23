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
import com.kemham.kartukompetensi.util.AppSettings;
import com.kemham.kartukompetensi.util.KompetensiUtil;
import com.kemham.kartukompetensi.view.ScoreProgressBar;

import java.util.Date;

/**
 * Adapter for displaying skills in a RecyclerView.
 */
public class SkillAdapter extends ListAdapter<Skill, SkillAdapter.SkillViewHolder> {

    private final Context context;
    private final SkillClickListener listener;

    public interface SkillClickListener {
        void onSkillClick(Skill skill);
        void onEditClick(Skill skill);
        void onDeleteClick(Skill skill);
    }

    public SkillAdapter(Context context, SkillClickListener listener) {
        super(new SkillDiffCallback());
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SkillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_skill, parent, false);
        return new SkillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkillViewHolder holder, int position) {
        Skill skill = getItem(position);
        holder.bind(skill);
    }

    class SkillViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView categoryText;
        private final TextView descriptionText;
        private final ScoreProgressBar scoreProgress;
        private final TextView targetScoreText;
        private final TextView lastAssessedText;
        private final ImageView priorityIcon;
        private final View editButton;
        private final View deleteButton;
        private final View statusIndicator;

        SkillViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.text_skill_name);
            categoryText = itemView.findViewById(R.id.text_category);
            descriptionText = itemView.findViewById(R.id.text_description);
            scoreProgress = itemView.findViewById(R.id.progress_score);
            targetScoreText = itemView.findViewById(R.id.text_target_score);
            lastAssessedText = itemView.findViewById(R.id.text_last_assessed);
            priorityIcon = itemView.findViewById(R.id.icon_priority);
            editButton = itemView.findViewById(R.id.button_edit);
            deleteButton = itemView.findViewById(R.id.button_delete);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
        }

        void bind(Skill skill) {
            // Set basic info
            nameText.setText(skill.getName());
            categoryText.setText(getCategoryText(skill.getCategory()));
            
            String description = skill.getDescription();
            if (description != null && !description.isEmpty()) {
                descriptionText.setVisibility(View.VISIBLE);
                descriptionText.setText(description);
            } else {
                descriptionText.setVisibility(View.GONE);
            }

            // Set score progress
            int score = skill.getScore();
            int targetScore = skill.getTargetScore();
            scoreProgress.setProgress(score);
            scoreProgress.setMax(targetScore);
            scoreProgress.setProgressColor(KompetensiUtil.getProgressColor(context, score, targetScore));

            // Set target score
            targetScoreText.setText(context.getString(R.string.target_score_format, targetScore));

            // Set last assessed date
            Date lastAssessed = skill.getLastAssessed();
            if (lastAssessed != null) {
                lastAssessedText.setVisibility(View.VISIBLE);
                lastAssessedText.setText(context.getString(R.string.last_assessed_format, 
                    KompetensiUtil.formatDate(lastAssessed)));
            } else {
                lastAssessedText.setVisibility(View.GONE);
            }

            // Set priority indicator
            setPriorityIndicator(skill.getPriorityLevel());

            // Set status indicator color
            setStatusIndicator(score, targetScore);

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSkillClick(skill);
                }
            });

            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(skill);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(skill);
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

        private void setStatusIndicator(int score, int target) {
            int color;
            if (score >= target) {
                color = context.getResources().getColor(R.color.status_complete);
            } else if (score >= target * 0.8) {
                color = context.getResources().getColor(R.color.status_on_track);
            } else if (score >= target * 0.6) {
                color = context.getResources().getColor(R.color.status_in_progress);
            } else {
                color = context.getResources().getColor(R.color.status_needs_attention);
            }
            statusIndicator.setBackgroundColor(color);
        }
    }

    private static class SkillDiffCallback extends DiffUtil.ItemCallback<Skill> {
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
