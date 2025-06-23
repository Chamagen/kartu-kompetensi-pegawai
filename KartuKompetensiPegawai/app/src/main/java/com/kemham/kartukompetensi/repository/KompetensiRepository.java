package com.kemham.kartukompetensi.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.kemham.kartukompetensi.data.AppDatabase;
import com.kemham.kartukompetensi.data.EmployeeDao;
import com.kemham.kartukompetensi.data.SkillDao;
import com.kemham.kartukompetensi.model.Employee;
import com.kemham.kartukompetensi.model.Skill;
import com.kemham.kartukompetensi.util.AppExecutors;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Repository that handles data operations between the database and UI.
 */
public class KompetensiRepository {

    private static volatile KompetensiRepository instance;
    private final AppDatabase database;
    private final EmployeeDao employeeDao;
    private final SkillDao skillDao;
    private final AppExecutors executors;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private KompetensiRepository(Context context) {
        database = AppDatabase.getInstance(context);
        employeeDao = database.employeeDao();
        skillDao = database.skillDao();
        executors = AppExecutors.getInstance();
    }

    public static KompetensiRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (KompetensiRepository.class) {
                if (instance == null) {
                    instance = new KompetensiRepository(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    // Employee Operations

    public LiveData<Employee> getEmployee(String nip) {
        return employeeDao.getEmployee(nip);
    }

    public LiveData<List<Employee>> getAllEmployees() {
        return employeeDao.getAllEmployees();
    }

    public LiveData<List<Employee>> getActiveEmployees() {
        return employeeDao.getActiveEmployees();
    }

    public void insertEmployee(Employee employee) {
        executors.diskIO().execute(() -> {
            try {
                employeeDao.insert(employee);
            } catch (Exception e) {
                errorMessage.postValue("Failed to insert employee: " + e.getMessage());
            }
        });
    }

    public void updateEmployee(Employee employee) {
        executors.diskIO().execute(() -> {
            try {
                employeeDao.update(employee);
            } catch (Exception e) {
                errorMessage.postValue("Failed to update employee: " + e.getMessage());
            }
        });
    }

    public void deleteEmployee(Employee employee) {
        executors.diskIO().execute(() -> {
            try {
                employeeDao.delete(employee);
            } catch (Exception e) {
                errorMessage.postValue("Failed to delete employee: " + e.getMessage());
            }
        });
    }

    // Skill Operations

    public LiveData<Skill> getSkill(long skillId) {
        return skillDao.getSkill(skillId);
    }

    public LiveData<List<Skill>> getEmployeeSkills(String employeeNip) {
        return skillDao.getEmployeeSkills(employeeNip);
    }

    public LiveData<List<Skill>> getEmployeeSkillsByCategory(String employeeNip, Skill.Category category) {
        return skillDao.getEmployeeSkillsByCategory(employeeNip, category);
    }

    public void insertSkill(Skill skill) {
        executors.diskIO().execute(() -> {
            try {
                skillDao.insert(skill);
                updateEmployeeScore(skill.getEmployeeNip());
            } catch (Exception e) {
                errorMessage.postValue("Failed to insert skill: " + e.getMessage());
            }
        });
    }

    public void updateSkill(Skill skill) {
        executors.diskIO().execute(() -> {
            try {
                skillDao.update(skill);
                updateEmployeeScore(skill.getEmployeeNip());
            } catch (Exception e) {
                errorMessage.postValue("Failed to update skill: " + e.getMessage());
            }
        });
    }

    public void deleteSkill(Skill skill) {
        executors.diskIO().execute(() -> {
            try {
                skillDao.delete(skill);
                updateEmployeeScore(skill.getEmployeeNip());
            } catch (Exception e) {
                errorMessage.postValue("Failed to delete skill: " + e.getMessage());
            }
        });
    }

    // Development Areas

    public LiveData<List<Skill>> getSkillsNeedingDevelopment(String employeeNip) {
        return skillDao.getSkillsNeedingDevelopment(employeeNip);
    }

    public LiveData<List<Skill>> getHighPrioritySkills(String employeeNip) {
        return skillDao.getHighPrioritySkills(employeeNip);
    }

    // Assessment Management

    public LiveData<List<Employee>> getEmployeesNeedingAssessment() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -6); // 6 months ago
        return employeeDao.getEmployeesNeedingAssessment(calendar.getTime());
    }

    public LiveData<List<Skill>> getSkillsNeedingAssessment(String employeeNip) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -3); // 3 months ago
        return skillDao.getSkillsNeedingAssessment(employeeNip, calendar.getTime());
    }

    // Score Management

    private void updateEmployeeScore(String employeeNip) {
        executors.diskIO().execute(() -> {
            try {
                LiveData<SkillDao.SkillProgress> progressLiveData = skillDao.getOverallProgress(employeeNip);
                MediatorLiveData<Void> mediator = new MediatorLiveData<>();
                mediator.addSource(progressLiveData, progress -> {
                    if (progress != null) {
                        Employee employee = employeeDao.getEmployee(employeeNip).getValue();
                        if (employee != null) {
                            employee.setOverallScore((int) progress.averageProgress);
                            employee.setLastAssessment(new Date());
                            employeeDao.update(employee);
                        }
                        mediator.removeSource(progressLiveData);
                    }
                });
            } catch (Exception e) {
                errorMessage.postValue("Failed to update employee score: " + e.getMessage());
            }
        });
    }

    // Status Management

    public LiveData<Boolean> getLoadingStatus() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void clearError() {
        errorMessage.setValue(null);
    }

    // Cleanup

    public void cleanup() {
        executors.shutdown();
        AppDatabase.destroyInstance();
        instance = null;
    }

    // Utility Methods

    public void refreshData() {
        isLoading.setValue(true);
        executors.diskIO().execute(() -> {
            try {
                // Perform any necessary data refresh operations
                Thread.sleep(1000); // Simulate network delay
                isLoading.postValue(false);
            } catch (Exception e) {
                errorMessage.postValue("Failed to refresh data: " + e.getMessage());
                isLoading.postValue(false);
            }
        });
    }

    public void schedulePeriodicUpdate() {
        executors.scheduled().scheduleAtFixedRate(
            this::refreshData,
            0,
            15,
            TimeUnit.MINUTES
        );
    }
}
