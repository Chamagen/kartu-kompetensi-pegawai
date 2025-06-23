package com.kemham.kartukompetensi.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.kemham.kartukompetensi.model.Employee;
import com.kemham.kartukompetensi.model.Employee.EmployeeStatus;

import java.util.Date;
import java.util.List;

/**
 * Data Access Object for the employees table.
 */
@Dao
public interface EmployeeDao {

    /**
     * Insert an employee into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Employee employee);

    /**
     * Insert multiple employees into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Employee> employees);

    /**
     * Update an employee in the database.
     */
    @Update
    int update(Employee employee);

    /**
     * Delete an employee from the database.
     */
    @Delete
    int delete(Employee employee);

    /**
     * Get an employee by NIP.
     */
    @Query("SELECT * FROM employees WHERE nip = :nip")
    LiveData<Employee> getEmployee(String nip);

    /**
     * Get all employees.
     */
    @Query("SELECT * FROM employees ORDER BY name ASC")
    LiveData<List<Employee>> getAllEmployees();

    /**
     * Get active employees.
     */
    @Query("SELECT * FROM employees WHERE status = 'ACTIVE' ORDER BY name ASC")
    LiveData<List<Employee>> getActiveEmployees();

    /**
     * Get employees by status.
     */
    @Query("SELECT * FROM employees WHERE status = :status ORDER BY name ASC")
    LiveData<List<Employee>> getEmployeesByStatus(EmployeeStatus status);

    /**
     * Get employees by unit.
     */
    @Query("SELECT * FROM employees WHERE unit = :unit ORDER BY name ASC")
    LiveData<List<Employee>> getEmployeesByUnit(String unit);

    /**
     * Get employees needing assessment.
     */
    @Query("SELECT * FROM employees WHERE lastAssessment IS NULL OR lastAssessment < :cutoffDate ORDER BY name ASC")
    LiveData<List<Employee>> getEmployeesNeedingAssessment(Date cutoffDate);

    /**
     * Get employees with scores below target.
     */
    @Query("SELECT * FROM employees WHERE overallScore < targetScore ORDER BY (targetScore - overallScore) DESC")
    LiveData<List<Employee>> getEmployeesBelowTarget();

    /**
     * Get count of all employees.
     */
    @Query("SELECT COUNT(*) FROM employees")
    LiveData<Integer> getEmployeeCount();

    /**
     * Get count of employees by status.
     */
    @Query("SELECT COUNT(*) FROM employees WHERE status = :status")
    LiveData<Integer> getEmployeeCountByStatus(EmployeeStatus status);

    /**
     * Search employees by name or NIP.
     */
    @Query("SELECT * FROM employees WHERE name LIKE '%' || :query || '%' OR nip LIKE '%' || :query || '%' ORDER BY name ASC")
    LiveData<List<Employee>> searchEmployees(String query);

    /**
     * Get all unique units.
     */
    @Query("SELECT DISTINCT unit FROM employees WHERE unit IS NOT NULL ORDER BY unit ASC")
    LiveData<List<String>> getAllUnits();

    /**
     * Update employee status.
     */
    @Query("UPDATE employees SET status = :status WHERE nip = :nip")
    int updateEmployeeStatus(String nip, EmployeeStatus status);

    /**
     * Update employee score.
     */
    @Query("UPDATE employees SET overallScore = :score, lastAssessment = :assessmentDate WHERE nip = :nip")
    int updateEmployeeScore(String nip, int score, Date assessmentDate);

    /**
     * Delete inactive employees.
     */
    @Query("DELETE FROM employees WHERE status = 'INACTIVE'")
    int deleteInactiveEmployees();

    /**
     * Delete all employees.
     */
    @Query("DELETE FROM employees")
    int deleteAllEmployees();

    /**
     * Get average score by unit.
     */
    @Query("SELECT AVG(overallScore) FROM employees WHERE unit = :unit")
    LiveData<Float> getAverageScoreByUnit(String unit);

    /**
     * Get employees with recent assessments.
     */
    @Query("SELECT * FROM employees WHERE lastAssessment >= :cutoffDate ORDER BY lastAssessment DESC")
    LiveData<List<Employee>> getRecentlyAssessedEmployees(Date cutoffDate);

    /**
     * Update employee supervisor.
     */
    @Transaction
    @Query("UPDATE employees SET supervisor = :supervisorNip WHERE nip = :employeeNip")
    int updateSupervisor(String employeeNip, String supervisorNip);

    /**
     * Get employees by supervisor.
     */
    @Query("SELECT * FROM employees WHERE supervisor = :supervisorNip ORDER BY name ASC")
    LiveData<List<Employee>> getEmployeesBySupervisor(String supervisorNip);

    /**
     * Get top performing employees.
     */
    @Query("SELECT * FROM employees WHERE status = 'ACTIVE' ORDER BY overallScore DESC LIMIT :limit")
    LiveData<List<Employee>> getTopPerformingEmployees(int limit);

    /**
     * Get employees needing development.
     */
    @Query("SELECT * FROM employees WHERE status = 'ACTIVE' AND overallScore < :threshold ORDER BY overallScore ASC")
    LiveData<List<Employee>> getEmployeesNeedingDevelopment(int threshold);
}
