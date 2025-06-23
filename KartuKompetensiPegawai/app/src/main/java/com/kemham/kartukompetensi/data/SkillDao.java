package com.kemham.kartukompetensi.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.kemham.kartukompetensi.model.Skill;
import com.kemham.kartukompetensi.model.Skill.Category;
import com.kemham.kartukompetensi.model.Skill.Level;

import java.util.Date;
import java.util.List;

/**
 * Data Access Object for the skills table.
 */
@Dao
public interface SkillDao {

    /**
     * Insert a skill into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Skill skill);

    /**
     * Insert multiple skills into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Skill> skills);

    /**
     * Update a skill in the database.
     */
    @Update
    int update(Skill skill);

    /**
     * Delete a skill from the database.
     */
    @Delete
    int delete(Skill skill);

    /**
     * Get a skill by ID.
     */
    @Query("SELECT * FROM skills WHERE id = :skillId")
    LiveData<Skill> getSkill(long skillId);

    /**
     * Get all skills for an employee.
     */
    @Query("SELECT * FROM skills WHERE employeeNip = :employeeNip ORDER BY category, name")
    LiveData<List<Skill>> getEmployeeSkills(String employeeNip);

    /**
     * Get skills by category for an employee.
     */
    @Query("SELECT * FROM skills WHERE employeeNip = :employeeNip AND category = :category ORDER BY name")
    LiveData<List<Skill>> getEmployeeSkillsByCategory(String employeeNip, Category category);

    /**
     * Get skills needing development for an employee.
     */
    @Query("SELECT * FROM skills WHERE employeeNip = :employeeNip AND score < targetScore ORDER BY (targetScore - score) DESC")
    LiveData<List<Skill>> getSkillsNeedingDevelopment(String employeeNip);

    /**
     * Get high priority skills for an employee.
     */
    @Query("SELECT * FROM skills WHERE employeeNip = :employeeNip AND priorityLevel = 'HIGH' ORDER BY name")
    LiveData<List<Skill>> getHighPrioritySkills(String employeeNip);

    /**
     * Get skills needing assessment for an employee.
     */
    @Query("SELECT * FROM skills WHERE employeeNip = :employeeNip AND (lastAssessed IS NULL OR lastAssessed < :cutoffDate) ORDER BY name")
    LiveData<List<Skill>> getSkillsNeedingAssessment(String employeeNip, Date cutoffDate);

    /**
     * Get count of skills by category for an employee.
     */
    @Query("SELECT COUNT(*) FROM skills WHERE employeeNip = :employeeNip AND category = :category")
    LiveData<Integer> getSkillCountByCategory(String employeeNip, Category category);

    /**
     * Get average score by category for an employee.
     */
    @Query("SELECT AVG(score) FROM skills WHERE employeeNip = :employeeNip AND category = :category")
    LiveData<Float> getAverageScoreByCategory(String employeeNip, Category category);

    /**
     * Update skill score and assessment date.
     */
    @Query("UPDATE skills SET score = :score, lastAssessed = :assessmentDate WHERE id = :skillId")
    int updateSkillScore(long skillId, int score, Date assessmentDate);

    /**
     * Update skill priority level.
     */
    @Query("UPDATE skills SET priorityLevel = :level WHERE id = :skillId")
    int updateSkillPriority(long skillId, Level level);

    /**
     * Delete all skills for an employee.
     */
    @Query("DELETE FROM skills WHERE employeeNip = :employeeNip")
    int deleteEmployeeSkills(String employeeNip);

    /**
     * Get recently assessed skills for an employee.
     */
    @Query("SELECT * FROM skills WHERE employeeNip = :employeeNip AND lastAssessed >= :cutoffDate ORDER BY lastAssessed DESC")
    LiveData<List<Skill>> getRecentlyAssessedSkills(String employeeNip, Date cutoffDate);

    /**
     * Search skills by name for an employee.
     */
    @Query("SELECT * FROM skills WHERE employeeNip = :employeeNip AND name LIKE '%' || :query || '%' ORDER BY name")
    LiveData<List<Skill>> searchSkills(String employeeNip, String query);

    /**
     * Get top skills by score for an employee.
     */
    @Query("SELECT * FROM skills WHERE employeeNip = :employeeNip ORDER BY score DESC LIMIT :limit")
    LiveData<List<Skill>> getTopSkills(String employeeNip, int limit);

    /**
     * Get skills with recommendations for an employee.
     */
    @Query("SELECT * FROM skills WHERE employeeNip = :employeeNip AND recommendation IS NOT NULL AND recommendation != '' ORDER BY name")
    LiveData<List<Skill>> getSkillsWithRecommendations(String employeeNip);

    /**
     * Get overall progress for an employee.
     */
    @Transaction
    @Query("SELECT " +
           "COUNT(*) as totalSkills, " +
           "SUM(CASE WHEN score >= targetScore THEN 1 ELSE 0 END) as achievedSkills, " +
           "AVG(CAST(score AS FLOAT) / targetScore * 100) as averageProgress " +
           "FROM skills WHERE employeeNip = :employeeNip")
    LiveData<SkillProgress> getOverallProgress(String employeeNip);

    /**
     * Data class for overall skill progress.
     */
    class SkillProgress {
        public int totalSkills;
        public int achievedSkills;
        public float averageProgress;
    }
}
