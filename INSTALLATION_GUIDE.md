# Panduan Instalasi Detail - Kartu Kompetensi Pegawai

## 1. Instalasi Android Studio

### Windows
1. Download Android Studio dari [https://developer.android.com/studio](https://developer.android.com/studio)
2. Jalankan file installer yang didownload
3. Ikuti wizard instalasi dengan pengaturan default
4. Pada tahap "Choose Components", pastikan centang:
   - Android Studio
   - Android Virtual Device
   - Performance (Intel HAXM)
5. Pilih lokasi instalasi (biarkan default jika ragu)
6. Tunggu proses instalasi selesai

### macOS
1. Download Android Studio untuk macOS
2. Buka file .dmg yang didownload
3. Drag Android Studio ke folder Applications
4. Buka Android Studio dari folder Applications
5. Ikuti setup wizard

### Linux
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-11-jdk
sudo snap install android-studio --classic

# Fedora
sudo dnf install java-11-openjdk-devel
sudo flatpak install flathub com.google.AndroidStudio
```

## 2. Konfigurasi Android SDK

1. Buka Android Studio
2. Pilih "Tools > SDK Manager"
3. Di tab "SDK Platforms", install:
   - Android 13.0 (API 33)
   - Android 12.0 (API 31)
   - Android 7.0 (API 24)
4. Di tab "SDK Tools", install:
   - Android SDK Build-Tools
   - Android Emulator
   - Android SDK Platform-Tools
   - Intel x86 Emulator Accelerator (HAXM)

## 3. Setup Emulator

1. Pilih "Tools > Device Manager"
2. Klik "Create Virtual Device"
3. Pilih "Phone" dan select "Pixel 2"
4. Download dan pilih system image:
   - Pilih tab "Recommended"
   - Download API 33 (Android 13.0)
5. Klik "Next" dan "Finish"

## 4. Import Proyek

1. Clone repository:
```bash
git clone https://github.com/yourusername/KartuKompetensiPegawai.git
```

2. Di Android Studio:
   - File > Open
   - Navigasi ke folder KartuKompetensiPegawai
   - Klik "OK"
   - Tunggu proses sync gradle selesai

## 5. Menjalankan Aplikasi

1. Pilih device target:
   - Dari dropdown di toolbar, pilih emulator yang sudah dibuat
   - Atau hubungkan device Android fisik dengan USB debugging aktif

2. Klik tombol Run (ikon play hijau) atau tekan Shift + F10

3. Tunggu proses build selesai dan aplikasi akan terbuka di emulator/device

## Troubleshooting

### Masalah Umum

1. Gradle sync failed
   ```
   File > Invalidate Caches / Restart
   ```

2. HAXM tidak terinstall
   ```
   Buka SDK Manager > SDK Tools
   Install/Reinstall Intel x86 Emulator Accelerator
   ```

3. Emulator lambat
   - Pastikan virtualization enabled di BIOS
   - Tambah RAM di konfigurasi emulator
   - Gunakan device fisik

4. Error "SDK location not found"
   - Buat file local.properties di root proyek
   - Isi dengan path SDK:
     ```
     sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
     ```

### Persyaratan Sistem Minimum

- 8GB RAM (16GB direkomendasikan)
- 4GB disk space
- 1280 x 800 minimum screen resolution
- 64-bit OS (Windows, macOS, or Linux)

## Bantuan Tambahan

Jika mengalami masalah, silakan:
1. Cek [dokumentasi resmi Android](https://developer.android.com/studio/intro)
2. Buka issue di repository
3. Hubungi tim pengembang
