package com.kemham.kartukompetensi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.kemham.kartukompetensi.R;
import com.kemham.kartukompetensi.model.Skill;
import com.kemham.kartukompetensi.util.AppSettings;
import com.kemham.kartukompetensi.util.KompetensiUtil;

/**
 * Dialog for adding or editing a skill.
 */
public class AddSkillDialog extends DialogFragment {

    private EditText nameInput;
    private Spinner categorySpinner;
    private EditText descriptionInput;
    private SeekBar scoreSeekBar;
    private TextView scoreText;
    private SeekBar targetScoreSeekBar;
    private TextView targetScoreText;
    private Spinner prioritySpinner;
    private EditText recommendationInput;

    private String employeeNip;
    private Skill existingSkill;
    private SkillDialogListener listener;

    public interface SkillDialogListener {
        void onSkillSaved(Skill skill);
    }

    public static AddSkillDialog newInstance(String employeeNip) {
        AddSkillDialog dialog = new AddSkillDialog();
        Bundle args = new Bundle();
        args.putString("employee_nip", employeeNip);
        dialog.setArguments(args);
        return dialog;
    }

    public static AddSkillDialog newInstance(String employeeNip, Skill skill) {
        AddSkillDialog dialog = new AddSkillDialog();
        Bundle args = new Bundle();
        args.putString("employee_nip", employeeNip);
        args.putParcelable("skill", skill);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (SkillDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SkillDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_skill, null);

        // Get arguments
        Bundle args = getArguments();
        if (args != null) {
            employeeNip = args.getString("employee_nip");
            existingSkill = args.getParcelable("skill");
        }

        // Initialize views
        initializeViews(view);
        setupSpinners();
        setupSeekBars();

        // Set existing data if editing
        if (existingSkill != null) {
            populateExistingData();
        }

        // Build dialog
        builder.setView(view)
               .setTitle(existingSkill != null ? R.string.edit_skill : R.string.add_skill)
               .setPositiveButton(R.string.save, null) // Set listener later to prevent auto-dismiss
               .setNegativeButton(R.string.cancel, (dialog, id) -> dismiss());

        AlertDialog dialog = builder.create();

        // Override positive button click to validate before dismissing
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                if (validateInput()) {
                    saveSkill();
                    dismiss();
                }
            });
        });

        return dialog;
    }

    private void initializeViews(View view) {
        nameInput = view.findViewById(R.id.input_name);
        categorySpinner = view.findViewById(R.id.spinner_category);
        descriptionInput = view.findViewById(R.id.input_description);
        scoreSeekBar = view.findViewById(R.id.seekbar_score);
        scoreText = view.findViewById(R.id.text_score);
        targetScoreSeekBar = view.findViewById(R.id.seekbar_target_score);
        targetScoreText = view.findViewById(R.id.text_target_score);
        prioritySpinner = view.findViewById(R.id.spinner_priority);
        recommendationInput = view.findViewById(R.id.input_recommendation);
    }

    private void setupSpinners() {
        // Setup category spinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.skill_categories,
            android.R.layout.simple_spinner_item
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Setup priority spinner
        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.priority_levels,
            android.R.layout.simple_spinner_item
        );
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(priorityAdapter);
    }

    private void setupSeekBars() {
        // Setup score seekbar
        scoreSeekBar.setMax(AppSettings.MAX_SCORE);
        scoreSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                scoreText.setText(getString(R.string.score_format, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Setup target score seekbar
        targetScoreSeekBar.setMax(AppSettings.MAX_SCORE);
        targetScoreSeekBar.setProgress(AppSettings.DEFAULT_TARGET_SCORE);
        targetScoreSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                targetScoreText.setText(getString(R.string.target_score_format, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void populateExistingData() {
        nameInput.setText(existingSkill.getName());
        categorySpinner.setSelection(existingSkill.getCategory().ordinal());
        descriptionInput.setText(existingSkill.getDescription());
        scoreSeekBar.setProgress(existingSkill.getScore());
        targetScoreSeekBar.setProgress(existingSkill.getTargetScore());
        prioritySpinner.setSelection(existingSkill.getPriorityLevel().ordinal());
        recommendationInput.setText(existingSkill.getRecommendation());
    }

    private boolean validateInput() {
        String name = nameInput.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            nameInput.setError(getString(R.string.error_name_required));
            return false;
        }

        int score = scoreSeekBar.getProgress();
        int targetScore = targetScoreSeekBar.getProgress();
        if (targetScore < score) {
            targetScoreSeekBar.requestFocus();
            return false;
        }

        return true;
    }

    private void saveSkill() {
        Skill skill = existingSkill != null ? existingSkill : new Skill();
        skill.setEmployeeNip(employeeNip);
        skill.setName(nameInput.getText().toString().trim());
        skill.setCategory(Skill.Category.values()[categorySpinner.getSelectedItemPosition()]);
        skill.setDescription(descriptionInput.getText().toString().trim());
        skill.setScore(scoreSeekBar.getProgress());
        skill.setTargetScore(targetScoreSeekBar.getProgress());
        skill.setPriorityLevel(Skill.Level.values()[prioritySpinner.getSelectedItemPosition()]);
        skill.setRecommendation(recommendationInput.getText().toString().trim());

        listener.onSkillSaved(skill);
    }
}
