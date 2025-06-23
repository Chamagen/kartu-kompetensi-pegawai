package com.kemham.kartukompetensi.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.kemham.kartukompetensi.KartuKompetensiApp;
import com.kemham.kartukompetensi.data.EmployeeDao;
import com.kemham.kartukompetensi.model.Employee;
import com.kemham.kartukompetensi.model.Skill;
import com.kemham.kartukompetensi.repository.KompetensiRepository;
import com.kemham.kartukompetensi.util.AppSettings;
import com.kemham.kartukompetensi.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final KompetensiRepository repository;
    private final PreferenceManager preferenceManager;
    
    private final MutableLiveData<String> selectedEmployeeNip;
    private final LiveData<Employee> selectedEmployee;
    private final LiveData<List<Employee>> allEmployees;
    
    private final MediatorLiveData<List<Skill>> basicSkills;
    private final MediatorLiveData<List<Skill>> technicalSkills;
    private final MediatorLiveData<List<Skill>> emergingSkills;
    private final MediatorLiveData<List<Skill>> potentialSkills;
    private final MediatorLiveData<List<Skill>> developmentAreas;
    
    private final LiveData<Integer> overallScore;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> errorMessage;
    private final MutableLiveData<Boolean> isEditMode;
    private final MutableLiveData<String> searchQuery;
    private final MutableLiveData<Employee.EmployeeStatus> selectedStatus;

    public MainViewModel(Application application) {
        super(application);
        
        KartuKompetensiApp app = (KartuKompetensiApp) application;
        repository = app.getRepository();
        preferenceManager = PreferenceManager.getInstance();
        
        isEditMode = new MutableLiveData<>(false);
        searchQuery = new MutableLiveData<>("");
        selectedStatus = new MutableLiveData<>(Employee.EmployeeStatus.ACTIVE);
        
        selectedEmployeeNip = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();

        // Initialize employee data
        allEmployees = repository.getAllEmployees();
        selectedEmployee = Transformations.switchMap(selectedEmployeeNip,
            repository::getEmployee);

        // Initialize skills data
        basicSkills = new MediatorLiveData<>();
        technicalSkills = new MediatorLiveData<>();
        emergingSkills = new MediatorLiveData<>();
        potentialSkills = new MediatorLiveData<>();
        developmentAreas = new MediatorLiveData<>();

        // Setup skills observers
        setupSkillsObservers();

        // Initialize overall score
        overallScore = Transformations.switchMap(selectedEmployeeNip,
            repository::getOverallScore);

        // Load last selected employee
        String lastSelectedNip = preferenceManager.getLastSelectedEmployee();
        if (lastSelectedNip != null) {
            selectedEmployeeNip.setValue(lastSelectedNip);
        }
    }

    private void setupSkillsObservers() {
        // Basic Skills
        basicSkills.addSource(
            Transformations.switchMap(selectedEmployeeNip,
                nip -> repository.getSkillsByCategory(nip, Skill.Category.BASIC)),
            skills -> basicSkills.setValue(filterAndSortSkills(skills))
        );

        // Technical Skills
        technicalSkills.addSource(
            Transformations.switchMap(selectedEmployeeNip,
                nip -> repository.getSkillsByCategory(nip, Skill.Category.TECHNICAL)),
            skills -> technicalSkills.setValue(filterAndSortSkills(skills))
        );

        // Emerging Skills
        emergingSkills.addSource(
            Transformations.switchMap(selectedEmployeeNip,
                nip -> repository.getSkillsByCategory(nip, Skill.Category.EMERGING)),
            skills -> emergingSkills.setValue(filterAndSortSkills(skills))
        );

        // Potential Skills
        potentialSkills.addSource(
            Transformations.switchMap(selectedEmployeeNip,
                nip -> repository.getSkillsByCategory(nip, Skill.Category.POTENTIAL)),
            skills -> potentialSkills.setValue(filterAndSortSkills(skills))
        );

        // Development Areas
        developmentAreas.addSource(
            Transformations.switchMap(selectedEmployeeNip,
                repository::getDevelopmentAreas),
            skills -> developmentAreas.setValue(filterAndSortSkills(skills))
        );
    }

    private List<Skill> filterAndSortSkills(List<Skill> skills) {
        if (skills == null) return new ArrayList<>();

        List<Skill> filteredSkills = new ArrayList<>(skills);
        
        // Apply minimum score filter if enabled
        int minScore = preferenceManager.getMinimumSkillScore();
        if (minScore > AppSettings.MIN_SCORE) {
            filteredSkills.removeIf(skill -> skill.getScore() < minScore);
        }

        // Sort based on preference
        String sortOrder = preferenceManager.getSkillSortOrder();
        if (sortOrder.equals("score_desc")) {
            filteredSkills.sort((s1, s2) -> Integer.compare(s2.getScore(), s1.getScore()));
        } else if (sortOrder.equals("score_asc")) {
            filteredSkills.sort((s1, s2) -> Integer.compare(s1.getScore(), s2.getScore()));
        } else {
            filteredSkills.sort((s1, s2) -> s1.getName().compareTo(s2.getName()));
        }

        return filteredSkills;
    }

    // Employee operations
    public void selectEmployee(String nip) {
        selectedEmployeeNip.setValue(nip);
        preferenceManager.setLastSelectedEmployee(nip);
    }

    public LiveData<List<Employee>> getAllEmployees() {
        return allEmployees;
    }

    public LiveData<Employee> getSelectedEmployee() {
        return selectedEmployee;
    }

    // Employee Management
    public void addEmployee(Employee employee) {
        repository.insertEmployee(employee);
    }

    public void updateEmployee(Employee employee) {
        repository.updateEmployee(employee);
    }

    public void deleteEmployee(Employee employee) {
        repository.deleteEmployee(employee);
    }

    // Skills operations
    public LiveData<List<Skill>> getBasicSkills() {
        return basicSkills;
    }

    public LiveData<List<Skill>> getTechnicalSkills() {
        return technicalSkills;
    }

    public LiveData<List<Skill>> getEmergingSkills() {
        return emergingSkills;
    }

    public LiveData<List<Skill>> getPotentialSkills() {
        return potentialSkills;
    }

    public LiveData<List<Skill>> getDevelopmentAreas() {
        return developmentAreas;
    }

    public LiveData<Integer> getOverallScore() {
        return overallScore;
    }

    // Data operations
    public void addSkill(Skill skill) {
        repository.insertSkill(skill);
    }

    public void updateSkill(Skill skill) {
        repository.updateSkill(skill);
    }

    public void deleteSkill(Skill skill) {
        repository.deleteSkill(skill);
    }

    public void refreshData() {
        isLoading.setValue(true);
        repository.refreshData();
    }

    // State handling
    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void clearError() {
        errorMessage.setValue(null);
    }

    // Search and filter operations
    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }

    public void setSelectedStatus(Employee.EmployeeStatus status) {
        selectedStatus.setValue(status);
    }

    public LiveData<Employee.EmployeeStatus> getSelectedStatus() {
        return selectedStatus;
    }

    public LiveData<List<Employee>> getFilteredEmployees() {
        return Transformations.switchMap(searchQuery, query ->
            Transformations.switchMap(selectedStatus, status ->
                repository.getFilteredEmployees(query, status)
            )
        );
    }

    // Edit mode operations
    public void setEditMode(boolean editMode) {
        isEditMode.setValue(editMode);
    }

    public LiveData<Boolean> isEditMode() {
        return isEditMode;
    }

    // Statistics operations
    public LiveData<List<Employee>> getTopPerformers() {
        return repository.getTopPerformers(5);
    }

    public LiveData<List<EmployeeDao.UnitScores>> getUnitAverageScores() {
        return repository.getUnitAverageScores();
    }

    // Assessment Management
    public LiveData<List<Employee>> getEmployeesNeedingAssessment() {
        return repository.getEmployeesNeedingAssessment();
    }

    public LiveData<List<Skill>> getSkillsNeedingAssessment() {
        return Transformations.switchMap(selectedEmployeeNip,
            repository::getSkillsNeedingAssessment);
    }

    // Score Calculations
    public int calculateOverallProgress(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            return 0;
        }

        int totalScore = 0;
        int totalTarget = 0;

        for (Skill skill : skills) {
            totalScore += skill.getScore();
            totalTarget += skill.getTargetScore();
        }

        return totalTarget > 0 ? (totalScore * 100) / totalTarget : 0;
    }

    public boolean isSkillBelowTarget(Skill skill) {
        return skill != null && skill.getScore() < skill.getTargetScore();
    }

    public int getSkillGap(Skill skill) {
        return skill != null ? skill.getTargetScore() - skill.getScore() : 0;
    }

    public boolean needsAssessment(Skill skill) {
        return skill != null && skill.needsAssessment();
    }

    // Preference Management
    public void updatePreferences(String sortOrder, int minScore) {
        preferenceManager.setSkillSortOrder(sortOrder);
        preferenceManager.setMinimumSkillScore(minScore);
        refreshData();
    }

    public String getSkillSortOrder() {
        return preferenceManager.getSkillSortOrder();
    }

    public int getMinimumSkillScore() {
        return preferenceManager.getMinimumSkillScore();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cleanup();
    }
}
