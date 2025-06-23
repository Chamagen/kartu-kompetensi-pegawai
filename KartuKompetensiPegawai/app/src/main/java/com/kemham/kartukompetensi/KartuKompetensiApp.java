package com.kemham.kartukompetensi;

import android.app.Application;
import android.os.StrictMode;

import androidx.appcompat.app.AppCompatDelegate;

import com.kemham.kartukompetensi.data.AppDatabase;
import com.kemham.kartukompetensi.repository.KompetensiRepository;
import com.kemham.kartukompetensi.util.AppExecutors;
import com.kemham.kartukompetensi.util.AppSettings;
import com.kemham.kartukompetensi.util.PreferenceManager;

import java.util.concurrent.TimeUnit;

/**
 * Custom Application class for initialization and dependency management.
 */
public class KartuKompetensiApp extends Application {
    private KompetensiRepository repository;
    private PreferenceManager preferenceManager;
    private AppExecutors appExecutors;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize components
        initializeComponents();

        // Setup preferences
        setupPreferences();

        // Configure strict mode for development
        if (AppSettings.DEBUG) {
            setupStrictMode();
        }

        // Schedule periodic tasks
        schedulePeriodicTasks();
    }

    private void initializeComponents() {
        // Initialize executors
        appExecutors = AppExecutors.getInstance();

        // Initialize preferences
        preferenceManager = PreferenceManager.getInstance(this);

        // Initialize database
        AppDatabase database = AppDatabase.getInstance(this);

        // Initialize repository
        repository = KompetensiRepository.getInstance(this);
    }

    private void setupPreferences() {
        // Set theme based on preferences
        boolean isDarkMode = preferenceManager.isDarkMode();
        AppCompatDelegate.setDefaultNightMode(
            isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        // Handle first launch
        if (preferenceManager.isFirstLaunch()) {
            onFirstLaunch();
        }
    }

    private void setupStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()
            .penaltyLog()
            .build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects()
            .detectLeakedClosableObjects()
            .penaltyLog()
            .build());
    }

    private void schedulePeriodicTasks() {
        // Schedule auto-refresh if enabled
        if (preferenceManager.isAutoRefreshEnabled()) {
            int interval = preferenceManager.getRefreshInterval();
            appExecutors.executeAtFixedRate(
                this::refreshData,
                interval,
                interval,
                TimeUnit.MINUTES
            );
        }

        // Schedule daily maintenance tasks
        scheduleDailyMaintenance();
    }

    private void scheduleDailyMaintenance() {
        appExecutors.executeAtFixedRate(
            this::performMaintenance,
            24,
            24,
            TimeUnit.HOURS
        );
    }

    private void onFirstLaunch() {
        // Perform first launch initialization
        appExecutors.diskIO().execute(() -> {
            // Pre-populate database if needed
            repository.prepopulateData();

            // Mark first launch complete
            preferenceManager.setFirstLaunchComplete();
        });
    }

    private void refreshData() {
        repository.refreshData();
    }

    private void performMaintenance() {
        appExecutors.diskIO().execute(() -> {
            // Clean up old data
            repository.cleanupOldData();

            // Optimize database
            repository.optimizeDatabase();

            // Update statistics
            repository.updateStatistics();
        });
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // Clear non-essential caches
        repository.clearCache();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level >= TRIM_MEMORY_BACKGROUND) {
            // Clear non-essential resources
            repository.trimMemory();
        }
    }

    // Getters for dependencies
    public KompetensiRepository getRepository() {
        return repository;
    }

    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    public AppExecutors getAppExecutors() {
        return appExecutors;
    }

    // Cleanup
    @Override
    public void onTerminate() {
        super.onTerminate();
        cleanup();
    }

    private void cleanup() {
        // Cancel scheduled tasks
        appExecutors.shutdown();

        // Cleanup repository
        repository.cleanup();

        // Close database
        AppDatabase.destroyInstance();
    }
}
