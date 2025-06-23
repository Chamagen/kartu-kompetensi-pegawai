package com.kemham.kartukompetensi.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

/**
 * Entity class representing an employee and their competency data.
 */
@Entity(tableName = "employees")
public class Employee {

    @PrimaryKey
    @NonNull
    private String nip;
    private String name;
    private String unit;
    private String position;
    private String grade;
    private String email;
    private String phoneNumber;

    @TypeConverters(DateConverter.class)
    private Date joinDate;
    private String supervisor;
    private int overallScore;
    private int targetScore;

    @TypeConverters(DateConverter.class)
    private Date lastAssessment;
    private String notes;

    public enum EmployeeStatus {
        ACTIVE,
        INACTIVE,
        ON_LEAVE,
        TRANSFERRED
    }

    @TypeConverters(CategoryConverter.class)
    private EmployeeStatus status;

    public Employee(@NonNull String nip) {
        this.nip = nip;
        this.status = EmployeeStatus.ACTIVE;
        this.targetScore = 100;
    }

    // Getters and Setters
    @NonNull
    public String getNip() {
        return nip;
    }

    public void setNip(@NonNull String nip) {
        this.nip = nip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public int getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(int overallScore) {
        this.overallScore = overallScore;
    }

    public int getTargetScore() {
        return targetScore;
    }

    public void setTargetScore(int targetScore) {
        this.targetScore = targetScore;
    }

    public Date getLastAssessment() {
        return lastAssessment;
    }

    public void setLastAssessment(Date lastAssessment) {
        this.lastAssessment = lastAssessment;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    // Helper methods
    public boolean isActive() {
        return status == EmployeeStatus.ACTIVE;
    }

    public String getInitials() {
        if (name == null || name.isEmpty()) {
            return "";
        }

        StringBuilder initials = new StringBuilder();
        String[] parts = name.split("\\s+");
        for (String part : parts) {
            if (!part.isEmpty()) {
                initials.append(part.charAt(0));
                if (initials.length() >= 2) break;
            }
        }
        return initials.toString().toUpperCase();
    }

    public int getCompletionPercentage() {
        return targetScore > 0 ? (overallScore * 100) / targetScore : 0;
    }

    public boolean needsAssessment() {
        if (lastAssessment == null) {
            return true;
        }
        // Check if last assessment was more than 6 months ago
        long sixMonths = 6L * 30 * 24 * 60 * 60 * 1000; // approximately 6 months in milliseconds
        return System.currentTimeMillis() - lastAssessment.getTime() > sixMonths;
    }

    // Object methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return nip.equals(employee.nip);
    }

    @Override
    public int hashCode() {
        return nip.hashCode();
    }

    @Override
    public String toString() {
        return "Employee{" +
                "nip='" + nip + '\'' +
                ", name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", position='" + position + '\'' +
                ", status=" + status +
                ", overallScore=" + overallScore +
                '}';
    }

    // Builder pattern for convenient object creation
    public static class Builder {
        private final Employee employee;

        public Builder(@NonNull String nip) {
            employee = new Employee(nip);
        }

        public Builder name(String name) {
            employee.setName(name);
            return this;
        }

        public Builder unit(String unit) {
            employee.setUnit(unit);
            return this;
        }

        public Builder position(String position) {
            employee.setPosition(position);
            return this;
        }

        public Builder grade(String grade) {
            employee.setGrade(grade);
            return this;
        }

        public Builder email(String email) {
            employee.setEmail(email);
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            employee.setPhoneNumber(phoneNumber);
            return this;
        }

        public Builder joinDate(Date joinDate) {
            employee.setJoinDate(joinDate);
            return this;
        }

        public Builder supervisor(String supervisor) {
            employee.setSupervisor(supervisor);
            return this;
        }

        public Builder status(EmployeeStatus status) {
            employee.setStatus(status);
            return this;
        }

        public Builder overallScore(int score) {
            employee.setOverallScore(score);
            return this;
        }

        public Builder targetScore(int score) {
            employee.setTargetScore(score);
            return this;
        }

        public Builder lastAssessment(Date date) {
            employee.setLastAssessment(date);
            return this;
        }

        public Builder notes(String notes) {
            employee.setNotes(notes);
            return this;
        }

        public Employee build() {
            return employee;
        }
    }
}
