package com.kemham.kartukompetensi.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

/**
 * Entity class representing an employee's competency skill.
 */
@Entity(
    tableName = "skills",
    foreignKeys = @ForeignKey(
        entity = Employee.class,
        parentColumns = "nip",
        childColumns = "employeeNip",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("employeeNip")}
)
public class Skill implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String employeeNip;
    private String name;
    private String description;
    private int score;
    private int targetScore;

    public enum Category {
        BASIC,
        TECHNICAL,
        EMERGING,
        POTENTIAL
    }

    @TypeConverters(CategoryConverter.class)
    private Category category;

    public enum Level {
        LOW,
        MEDIUM,
        HIGH
    }

    @TypeConverters(CategoryConverter.class)
    private Level priorityLevel;

    @TypeConverters(DateConverter.class)
    private Date lastAssessed;
    private String recommendation;
    private String assessor;
    private String notes;

    // Constructor
    public Skill() {
        this.targetScore = 100;
        this.category = Category.BASIC;
        this.priorityLevel = Level.MEDIUM;
        this.lastAssessed = new Date();
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getEmployeeNip() {
        return employeeNip;
    }

    public void setEmployeeNip(@NonNull String employeeNip) {
        this.employeeNip = employeeNip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTargetScore() {
        return targetScore;
    }

    public void setTargetScore(int targetScore) {
        this.targetScore = targetScore;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Level getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(Level priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public Date getLastAssessed() {
        return lastAssessed;
    }

    public void setLastAssessed(Date lastAssessed) {
        this.lastAssessed = lastAssessed;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getAssessor() {
        return assessor;
    }

    public void setAssessor(String assessor) {
        this.assessor = assessor;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Helper methods
    public int getProgressPercentage() {
        return targetScore > 0 ? (score * 100) / targetScore : 0;
    }

    public int getScoreGap() {
        return targetScore - score;
    }

    public boolean isTargetAchieved() {
        return score >= targetScore;
    }

    public boolean needsAssessment() {
        if (lastAssessed == null) {
            return true;
        }
        // Check if last assessment was more than 3 months ago
        long threeMonths = 3L * 30 * 24 * 60 * 60 * 1000; // approximately 3 months in milliseconds
        return System.currentTimeMillis() - lastAssessed.getTime() > threeMonths;
    }

    // Parcelable implementation
    protected Skill(Parcel in) {
        id = in.readLong();
        employeeNip = in.readString();
        name = in.readString();
        description = in.readString();
        score = in.readInt();
        targetScore = in.readInt();
        category = Category.valueOf(in.readString());
        priorityLevel = Level.valueOf(in.readString());
        long tmpLastAssessed = in.readLong();
        lastAssessed = tmpLastAssessed != -1 ? new Date(tmpLastAssessed) : null;
        recommendation = in.readString();
        assessor = in.readString();
        notes = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(employeeNip);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(score);
        dest.writeInt(targetScore);
        dest.writeString(category.name());
        dest.writeString(priorityLevel.name());
        dest.writeLong(lastAssessed != null ? lastAssessed.getTime() : -1);
        dest.writeString(recommendation);
        dest.writeString(assessor);
        dest.writeString(notes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Skill> CREATOR = new Creator<Skill>() {
        @Override
        public Skill createFromParcel(Parcel in) {
            return new Skill(in);
        }

        @Override
        public Skill[] newArray(int size) {
            return new Skill[size];
        }
    };

    // Object methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill = (Skill) o;
        return id == skill.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", score=" + score +
                ", targetScore=" + targetScore +
                ", priorityLevel=" + priorityLevel +
                '}';
    }

    // Builder pattern
    public static class Builder {
        private final Skill skill;

        public Builder() {
            skill = new Skill();
        }

        public Builder employeeNip(String nip) {
            skill.setEmployeeNip(nip);
            return this;
        }

        public Builder name(String name) {
            skill.setName(name);
            return this;
        }

        public Builder description(String description) {
            skill.setDescription(description);
            return this;
        }

        public Builder score(int score) {
            skill.setScore(score);
            return this;
        }

        public Builder targetScore(int targetScore) {
            skill.setTargetScore(targetScore);
            return this;
        }

        public Builder category(Category category) {
            skill.setCategory(category);
            return this;
        }

        public Builder priorityLevel(Level level) {
            skill.setPriorityLevel(level);
            return this;
        }

        public Builder lastAssessed(Date date) {
            skill.setLastAssessed(date);
            return this;
        }

        public Builder recommendation(String recommendation) {
            skill.setRecommendation(recommendation);
            return this;
        }

        public Builder assessor(String assessor) {
            skill.setAssessor(assessor);
            return this;
        }

        public Builder notes(String notes) {
            skill.setNotes(notes);
            return this;
        }

        public Skill build() {
            return skill;
        }
    }
}
