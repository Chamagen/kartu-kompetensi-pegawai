# Panduan Lengkap Aplikasi Kartu Kompetensi Pegawai

## Daftar Isi
1. [Pengenalan](#pengenalan)
2. [Fitur Utama](#fitur-utama)
3. [Arsitektur](#arsitektur)
4. [Panduan Instalasi](#panduan-instalasi)
5. [Penggunaan Aplikasi](#penggunaan-aplikasi)
6. [Troubleshooting](#troubleshooting)

## Pengenalan

Kartu Kompetensi Pegawai adalah aplikasi Android untuk mengelola dan memantau kompetensi pegawai di lingkungan Kementerian HAM. Aplikasi ini memungkinkan pencatatan dan pemantauan:
- Kompetensi dasar
- Kompetensi teknis
- Emerging skills
- Area pengembangan
- Penilaian kinerja

## Fitur Utama

### 1. Manajemen Pegawai
- Profil lengkap pegawai
- Status aktif/non-aktif
- Hierarki supervisor
- Riwayat kompetensi

### 2. Penilaian Kompetensi
- Skor kompetensi (0-100)
- Target pencapaian
- Tracking progress
- Area pengembangan

### 3. Dashboard
- Overall score
- Special skills
- Development areas
- Progress tracking

### 4. Reporting
- Export data
- Analisis trend
- Rekomendasi pengembangan

## Arsitektur

### 1. Pattern
- MVVM (Model-View-ViewModel)
- Repository Pattern
- Single Activity
- Clean Architecture

### 2. Komponen Utama
```
com.kemham.kartukompetensi/
├── data/
│   ├── AppDatabase
│   ├── EmployeeDao
│   └── SkillDao
├── model/
│   ├── Employee
│   └── Skill
├── repository/
│   └── KompetensiRepository
├── viewmodel/
│   └── MainViewModel
├── view/
│   ├── CompetencyCardView
│   └── ScoreProgressBar
└── adapter/
    ├── EmployeeSpinnerAdapter
    └── SkillAdapter
```

## Panduan Instalasi

### Persyaratan Sistem
- Android Studio Hedgehog | 2023.1.1
- JDK 11 atau lebih tinggi
- Android SDK API 24+
- RAM 8GB minimum
- Storage 256GB recommended

### Setup Development
1. Clone repository
2. Import project di Android Studio
3. Sync gradle
4. Setup emulator/device
5. Run aplikasi

## Penggunaan Aplikasi

### 1. Login & Setup Awal
- Gunakan NIP untuk login
- Setup profil pegawai
- Pilih supervisor

### 2. Input Kompetensi
- Pilih kategori kompetensi
- Input skor dan target
- Tambah catatan jika perlu
- Simpan perubahan

### 3. Monitoring
- Cek overall score
- Review progress
- Identifikasi area pengembangan
- Export laporan

### 4. Maintenance
- Backup database berkala
- Update profil
- Sync data

## Troubleshooting

### 1. Database Issues
- Cek koneksi
- Validasi input
- Restore dari backup
- Reset database jika perlu

### 2. Performance Issues
- Clear cache
- Restart aplikasi
- Update index
- Optimize query

### 3. UI Issues
- Refresh layout
- Clear view cache
- Restart activity
- Update resources

## Referensi Dokumentasi

1. [INSTALLATION_GUIDE.md](INSTALLATION_GUIDE.md)
   - Setup Android Studio
   - Konfigurasi project
   - Build & run

2. [DATABASE_SETUP_GUIDE.md](DATABASE_SETUP_GUIDE.md)
   - Schema database
   - Migrasi
   - Backup & restore

3. [USB_DEBUGGING_GUIDE.md](USB_DEBUGGING_GUIDE.md)
   - Setup device
   - Debug aplikasi
   - Troubleshooting

4. [PERFORMANCE_GUIDE.md](PERFORMANCE_GUIDE.md)
   - Optimasi
   - Best practices
   - Monitoring

## Kontak Support

Untuk bantuan teknis:
- Email: support@kemham.go.id
- Telp: (021) xxx-xxxx
- Helpdesk: https://helpdesk.kemham.go.id

## Lisensi

Copyright © 2024 Kementerian Hukum dan HAM Republik Indonesia
All rights reserved.

---

## Quick Reference

### Keyboard Shortcuts
```
Ctrl + F5    : Run aplikasi
Shift + F10  : Build project
Ctrl + F11   : Build & run
Alt + Enter  : Quick fixes
```

### Common Commands
```bash
# Build APK
./gradlew assembleDebug

# Install ke device
adb install app/build/outputs/apk/debug/app-debug.apk

# Logcat
adb logcat | grep "com.kemham.kartukompetensi"
```

### Database Queries
```sql
-- Cek pegawai aktif
SELECT * FROM employees WHERE status = 'ACTIVE';

-- Cek skill dengan skor rendah
SELECT * FROM skills WHERE score < target_score;
```

### Coding Standards
```kotlin
// Naming conventions
ClassName
functionName
variable_name
CONSTANT_NAME

// File structure
imports
class declaration
companion object
properties
init block
lifecycle methods
public methods
private methods
