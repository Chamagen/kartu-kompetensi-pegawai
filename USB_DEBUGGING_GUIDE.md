# Panduan USB Debugging - Kartu Kompetensi Pegawai

## Mengaktifkan USB Debugging di Device Android

### Langkah 1: Aktifkan Mode Developer
1. Buka Settings (Pengaturan) di device Android
2. Scroll ke bawah dan pilih "About phone" (Tentang ponsel)
3. Temukan "Build number" (Nomor build)
4. Tap "Build number" 7 kali berturut-turut
5. Akan muncul pesan "You are now a developer!" (Anda sekarang developer!)

### Langkah 2: Aktifkan USB Debugging
1. Kembali ke menu Settings
2. Pilih "System" > "Developer options" (Opsi pengembang)
3. Aktifkan toggle "Developer options"
4. Scroll dan temukan "USB debugging"
5. Aktifkan toggle "USB debugging"
6. Konfirmasi dengan tap "OK" pada dialog yang muncul

## Menghubungkan Device ke Komputer

### Windows
1. Install driver USB:
   - Samsung: Download Samsung USB Driver
   - Google: Download Google USB Driver
   - Xiaomi: Download Mi USB Driver
   - Atau gunakan Universal ADB Driver

2. Hubungkan device:
   ```bash
   # Cek device terdeteksi
   adb devices
   
   # Jika muncul seperti ini, device sudah terhubung:
   List of devices attached
   XXXXXXXXXXX    device
   ```

### macOS
1. Tidak perlu install driver tambahan
2. Hubungkan device dengan USB
3. Buka Terminal:
   ```bash
   # Install ADB jika belum ada
   brew install android-platform-tools
   
   # Cek device
   adb devices
   ```

### Linux
1. Buat rules file:
   ```bash
   sudo nano /etc/udev/rules.d/51-android.rules
   ```

2. Tambahkan rule (ganti VENDOR_ID):
   ```
   SUBSYSTEM=="usb", ATTR{idVendor}=="VENDOR_ID", MODE="0666", GROUP="plugdev"
   ```

3. Set permissions:
   ```bash
   sudo chmod a+r /etc/udev/rules.d/51-android.rules
   ```

## Troubleshooting

### Device Tidak Terdeteksi
1. Pastikan:
   - USB debugging aktif
   - Device terhubung dengan mode "File Transfer"
   - Driver terinstall (Windows)
   - Device muncul di `adb devices`

2. Jika tidak muncul:
   ```bash
   # Restart ADB server
   adb kill-server
   adb start-server
   
   # Cek lagi
   adb devices
   ```

### Security Dialog Tidak Muncul
1. Revoke USB debugging authorization:
   - Developer options > Revoke USB debugging authorizations
2. Disconnect dan reconnect device
3. Tunggu dialog "Allow USB debugging?"
4. Centang "Always allow from this computer"
5. Tap "Allow"

### Tips Penggunaan

1. Wireless Debugging (Android 11+):
   ```bash
   # Hubungkan device dan PC ke WiFi yang sama
   # Di device: Developer options > Wireless debugging
   # Ikuti instruksi pairing di layar
   ```

2. Logcat Real-time:
   ```bash
   # Di terminal
   adb logcat
   
   # Filter by app
   adb logcat | grep "com.kemham.kartukompetensi"
   ```

3. Install APK langsung:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

4. Screenshot device:
   ```bash
   adb shell screencap -p /sdcard/screenshot.png
   adb pull /sdcard/screenshot.png
   ```

## Keamanan

1. Selalu gunakan device development khusus
2. Jangan aktifkan USB debugging di device pribadi
3. Revoke authorization saat selesai development
4. Nonaktifkan Developer options setelah selesai

## Performa

1. Disable animations di Developer options:
   - Window animation scale -> off
   - Transition animation scale -> off
   - Animator duration scale -> off

2. Don't keep activities -> off (untuk testing)

3. Enable GPU profiling jika perlu debug render

## Bantuan Lebih Lanjut

Jika mengalami masalah:
1. Cek [Android Developer Guide](https://developer.android.com/studio/command-line/adb)
2. Gunakan `adb -h` untuk bantuan command
3. Cek driver manufacturer device Anda
4. Konsultasi dengan tim development
