# Panduan Setup Database - Kartu Kompetensi Pegawai

## Struktur Database

### 1. Tabel Employee
```sql
CREATE TABLE employees (
    nip TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    position TEXT NOT NULL,
    rank TEXT NOT NULL,
    supervisor TEXT,
    status TEXT NOT NULL,
    created_at INTEGER,
    updated_at INTEGER
);
```

### 2. Tabel Skill
```sql
CREATE TABLE skills (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    employee_nip TEXT NOT NULL,
    name TEXT NOT NULL,
    category TEXT NOT NULL,
    score INTEGER NOT NULL,
    target_score INTEGER NOT NULL,
    last_assessment INTEGER,
    FOREIGN KEY (employee_nip) REFERENCES employees(nip)
);
```

## Cara Setup Database

### 1. Inisialisasi Data Awal

```kotlin
// Di class KartuKompetensiApp.kt
private fun initializeData() {
    val repository = getRepository()
    
    // Sample Employee
    val employee = Employee(
        nip = "198604272007031001",
        name = "Syafril Zakaria",
        position = "Analis Hukum Ahli Muda",
        rank = "Penata Tk.I (III/d)",
        supervisor = "I Made Agus Dwiana",
        status = Employee.EmployeeStatus.ACTIVE
    )
    
    // Sample Skills
    val basicSkills = listOf(
        Skill(
            name = "Analisis Kebijakan",
            category = Skill.Category.BASIC,
            score = 85,
            targetScore = 90,
            employeeNip = employee.nip
        ),
        // Tambahkan skill lainnya
    )
    
    // Insert data dalam background thread
    AppExecutors.diskIO().execute {
        repository.insertEmployee(employee)
        basicSkills.forEach { repository.insertSkill(it) }
    }
}
```

### 2. Migrasi Database

Buat class Migration untuk versi database:

```kotlin
// Di AppDatabase.kt
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Contoh migrasi: menambah kolom baru
        database.execSQL(
            "ALTER TABLE skills ADD COLUMN priority INTEGER NOT NULL DEFAULT 0"
        )
    }
}

// Daftarkan migrasi
Room.databaseBuilder(context, AppDatabase::class.java, "kompetensi.db")
    .addMigrations(MIGRATION_1_2)
    .build()
```

## Cara Backup & Restore

### 1. Backup Database

```bash
# Via ADB (root tidak diperlukan)
adb exec-out run-as com.kemham.kartukompetensi cat /data/data/com.kemham.kartukompetensi/databases/kompetensi.db > backup.db

# Atau dari aplikasi
val backupFile = File(context.getExternalFilesDir(null), "backup.db")
val dbFile = context.getDatabasePath("kompetensi.db")
dbFile.copyTo(backupFile, overwrite = true)
```

### 2. Restore Database

```kotlin
// Di aplikasi
fun restoreDatabase(backupFile: File) {
    val dbFile = context.getDatabasePath("kompetensi.db")
    
    // Tutup koneksi database yang ada
    AppDatabase.getInstance(context).close()
    
    // Copy file backup
    backupFile.copyTo(dbFile, overwrite = true)
    
    // Reinisialisasi database
    AppDatabase.getInstance(context)
}
```

## Troubleshooting

### 1. Database Corrupt
```kotlin
// Hapus dan buat ulang database
context.deleteDatabase("kompetensi.db")
AppDatabase.getInstance(context)
initializeData()
```

### 2. Migrasi Gagal
```kotlin
// Fallback: hapus dan buat ulang (HANYA untuk development)
Room.databaseBuilder(context, AppDatabase::class.java, "kompetensi.db")
    .fallbackToDestructiveMigration()
    .build()
```

### 3. Data Tidak Muncul
```kotlin
// Cek data di database
adb shell
run-as com.kemham.kartukompetensi
sqlite3 databases/kompetensi.db
.tables
SELECT * FROM employees;
SELECT * FROM skills;
```

## Best Practices

1. Selalu gunakan transaksi untuk operasi multiple:
```kotlin
database.runInTransaction {
    employeeDao.insert(employee)
    skillDao.insertAll(skills)
}
```

2. Backup otomatis:
```kotlin
// Setup work manager untuk backup berkala
val backupWork = PeriodicWorkRequestBuilder<DatabaseBackupWorker>(1, TimeUnit.DAYS)
    .setConstraints(Constraints.Builder()
        .setRequiresCharging(true)
        .build())
    .build()

WorkManager.getInstance(context).enqueue(backupWork)
```

3. Validasi data:
```kotlin
@Entity(tableName = "skills")
data class Skill(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "score")
    @IntRange(from = 0, to = 100)
    val score: Int,
    
    // ... other fields
)
```

4. Logging dan monitoring:
```kotlin
database.queryExecutor = object : RoomDatabase.QueryCallback {
    override fun onQuery(sqlQuery: String, bindArgs: List<Any?>) {
        Timber.d("SQL Query: $sqlQuery")
        Timber.d("Args: $bindArgs")
    }
}
```

## Keamanan

1. Enkripsi database (opsional):
```kotlin
Room.databaseBuilder(context, AppDatabase::class.java, "kompetensi.db")
    .openHelperFactory(SafeHelperFactory("password".toCharArray()))
    .build()
```

2. Validasi input:
```kotlin
fun validateEmployee(employee: Employee): Boolean {
    return employee.nip.matches(Regex("\\d{18}")) &&
           employee.name.isNotBlank() &&
           employee.position.isNotBlank()
}
```

3. Batasi akses:
```kotlin
@Dao
interface EmployeeDao {
    @Query("SELECT * FROM employees WHERE status = :status")
    fun getActiveEmployees(status: EmployeeStatus = EmployeeStatus.ACTIVE): LiveData<List<Employee>>
}
