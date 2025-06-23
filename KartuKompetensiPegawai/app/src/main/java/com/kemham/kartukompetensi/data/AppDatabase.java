package com.kemham.kartukompetensi.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.kemham.kartukompetensi.model.CategoryConverter;
import com.kemham.kartukompetensi.model.DateConverter;
import com.kemham.kartukompetensi.model.Employee;
import com.kemham.kartukompetensi.model.Skill;
import com.kemham.kartukompetensi.util.AppExecutors;

/**
 * The Room database for the application.
 */
@Database(
    entities = {Employee.class, Skill.class},
    version = 1,
    exportSchema = false
)
@TypeConverters({DateConverter.class, CategoryConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "kartu_kompetensi.db";
    private static volatile AppDatabase instance;

    // DAOs
    public abstract EmployeeDao employeeDao();
    public abstract SkillDao skillDao();

    /**
     * Gets the singleton instance of the database.
     */
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = buildDatabase(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    /**
     * Builds and pre-populates the database.
     */
    private static AppDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        // Pre-populate database in background
                        AppExecutors.getInstance().diskIO().execute(() -> {
                            AppDatabase database = AppDatabase.getInstance(context);
                            prepopulateDatabase(database);
                        });
                    }

                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);
                        // Enable foreign key constraints
                        db.execSQL("PRAGMA foreign_keys = ON");
                    }
                })
                .addMigrations() // Add migrations here when updating schema
                .fallbackToDestructiveMigration() // Wipes database on schema change
                .build();
    }

    /**
     * Pre-populates the database with initial data.
     */
    private static void prepopulateDatabase(AppDatabase database) {
        // Add sample employee
        Employee sampleEmployee = new Employee.Builder("199901010001")
                .name("John Doe")
                .unit("IT Department")
                .position("Software Engineer")
                .grade("L3")
                .email("john.doe@kemham.go.id")
                .build();
        database.employeeDao().insert(sampleEmployee);

        // Add sample skills
        Skill basicSkill = new Skill.Builder()
                .employeeNip("199901010001")
                .name("Java Programming")
                .description("Core Java programming skills")
                .category(Skill.Category.BASIC)
                .score(80)
                .targetScore(100)
                .priorityLevel(Skill.Level.HIGH)
                .build();
        database.skillDao().insert(basicSkill);

        Skill technicalSkill = new Skill.Builder()
                .employeeNip("199901010001")
                .name("Android Development")
                .description("Android app development skills")
                .category(Skill.Category.TECHNICAL)
                .score(75)
                .targetScore(100)
                .priorityLevel(Skill.Level.HIGH)
                .build();
        database.skillDao().insert(technicalSkill);

        Skill emergingSkill = new Skill.Builder()
                .employeeNip("199901010001")
                .name("Kotlin Programming")
                .description("Modern Android development with Kotlin")
                .category(Skill.Category.EMERGING)
                .score(60)
                .targetScore(100)
                .priorityLevel(Skill.Level.MEDIUM)
                .build();
        database.skillDao().insert(emergingSkill);
    }

    /**
     * Clears all data in the database.
     */
    public void clearAllTables() {
        if (instance != null) {
            AppExecutors.getInstance().diskIO().execute(() -> {
                synchronized (AppDatabase.class) {
                    instance.clearAllTables();
                }
            });
        }
    }

    /**
     * Closes the database.
     */
    public static void destroyInstance() {
        if (instance != null && instance.isOpen()) {
            synchronized (AppDatabase.class) {
                instance.close();
                instance = null;
            }
        }
    }

    /**
     * Checks if the database is open.
     */
    public boolean isOpen() {
        return instance != null && instance.getOpenHelper().getWritableDatabase().isOpen();
    }

    /**
     * Gets the database version.
     */
    public int getVersion() {
        return instance != null ? instance.getOpenHelper().getWritableDatabase().getVersion() : 0;
    }

    /**
     * Gets the database path.
     */
    public String getPath() {
        return instance != null ? instance.getOpenHelper().getWritableDatabase().getPath() : null;
    }

    /**
     * Runs a transaction.
     */
    public void runInTransaction(Runnable body) {
        if (instance != null) {
            instance.getOpenHelper().getWritableDatabase().beginTransaction();
            try {
                body.run();
                instance.getOpenHelper().getWritableDatabase().setTransactionSuccessful();
            } finally {
                instance.getOpenHelper().getWritableDatabase().endTransaction();
            }
        }
    }
}
