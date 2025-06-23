package com.kemham.kartukompetensi;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kemham.kartukompetensi.model.Employee;
import com.kemham.kartukompetensi.model.Skill;
import com.kemham.kartukompetensi.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private Spinner employeeSpinner;
    private TextView nameText, nipText, positionText, rankText, supervisorText;
    private TextView initialsText, ovrScore;
    private ChipGroup specialSkillsChipGroup;
    private RecyclerView developmentAreasRecyclerView;
    private FloatingActionButton addSkillFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Initialize views
        initializeViews();
        setupEmployeeSpinner();
        setupObservers();
        setupClickListeners();
    }

    private void initializeViews() {
        employeeSpinner = findViewById(R.id.employeeSpinner);
        nameText = findViewById(R.id.nameText);
        nipText = findViewById(R.id.nipText);
        positionText = findViewById(R.id.positionText);
        rankText = findViewById(R.id.rankText);
        supervisorText = findViewById(R.id.supervisorText);
        initialsText = findViewById(R.id.initialsText);
        ovrScore = findViewById(R.id.ovrScore);
        specialSkillsChipGroup = findViewById(R.id.specialSkillsChipGroup);
        developmentAreasRecyclerView = findViewById(R.id.developmentAreasRecyclerView);
        addSkillFab = findViewById(R.id.addSkillFab);

        // Setup RecyclerView
        developmentAreasRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupEmployeeSpinner() {
        viewModel.getAllEmployees().observe(this, employees -> {
            List<String> employeeNames = new ArrayList<>();
            for (Employee employee : employees) {
                employeeNames.add(employee.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                employeeNames
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            employeeSpinner.setAdapter(adapter);
        });

        employeeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = parent.getItemAtPosition(position).toString();
                viewModel.getAllEmployees().getValue().stream()
                    .filter(emp -> emp.getName().equals(selectedName))
                    .findFirst()
                    .ifPresent(employee -> viewModel.setCurrentEmployee(employee.getNip()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupObservers() {
        // Observe current employee
        viewModel.getCurrentEmployee().observe(this, employee -> {
            if (employee != null) {
                updateEmployeeUI(employee);
            }
        });

        // Observe special skills
        viewModel.getSpecialSkills().observe(this, skills -> {
            specialSkillsChipGroup.removeAllViews();
            for (Skill skill : skills) {
                Chip chip = new Chip(this);
                chip.setText(skill.getName());
                chip.setChipBackgroundColorResource(R.color.teal_700);
                chip.setTextColor(getResources().getColor(android.R.color.white));
                specialSkillsChipGroup.addView(chip);
            }
        });

        // Observe overall score
        viewModel.getOverallScore().observe(this, score -> {
            if (score != null) {
                ovrScore.setText(String.valueOf(score));
            }
        });

        // Observe development areas
        viewModel.getDevelopmentAreas().observe(this, skills -> {
            // Update development areas RecyclerView
            // Implementation of adapter would be needed
        });
    }

    private void updateEmployeeUI(Employee employee) {
        nameText.setText(employee.getName());
        nipText.setText(employee.getNip());
        positionText.setText(employee.getPosition());
        rankText.setText(employee.getRank());
        supervisorText.setText(employee.getSupervisor());
        initialsText.setText(employee.getInitials());
    }

    private void setupClickListeners() {
        addSkillFab.setOnClickListener(v -> {
            // Show dialog to add new skill
            // Implementation needed
        });
    }
}
