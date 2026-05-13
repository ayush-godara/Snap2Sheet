<p align="center">
  <h1 align="center">📸 Snap2Sheet</h1>
  <p align="center">
    <b>AI-Powered Expense Tracker for Android</b>
    <br />
    Scan receipts → Extract data with OCR → Track & analyze spending
    <br /><br />
    <img src="https://img.shields.io/badge/Platform-Android-green?style=for-the-badge&logo=android" />
    <img src="https://img.shields.io/badge/Language-Kotlin-purple?style=for-the-badge&logo=kotlin" />
    <img src="https://img.shields.io/badge/Min%20SDK-24-blue?style=for-the-badge" />
    <img src="https://img.shields.io/badge/Architecture-MVVM-orange?style=for-the-badge" />
  </p>
</p>

---

## ✨ About

**Snap2Sheet** is a modern Android application that simplifies expense tracking by leveraging **Google ML Kit OCR** to automatically extract merchant names, amounts, and dates from receipt photos. Simply snap a picture of your receipt and let AI do the data entry for you.

All data is stored locally using **Room Database**, ensuring your financial information stays private and accessible offline.

---

## 📱 Screenshots

<p align="center">
  <img src="screenshots/scan_receipt.png" alt="Scan Receipt" width="220" />
  &nbsp;&nbsp;
  <img src="screenshots/expenses_list.png" alt="Expenses List" width="220" />
  &nbsp;&nbsp;
  <img src="screenshots/analytics_dashboard.png" alt="Analytics Dashboard" width="220" />
  &nbsp;&nbsp;
  <img src="screenshots/settings.png" alt="Settings" width="220" />
</p>

<p align="center">
  <em>Scan Receipt &nbsp;•&nbsp; Expenses List &nbsp;•&nbsp; Analytics Dashboard &nbsp;•&nbsp; Settings</em>
</p>

---

## 🚀 Features

| Feature | Description |
|---------|-------------|
| 📷 **Receipt Scanning** | Capture receipts using CameraX and auto-extract merchant, amount & date via Google ML Kit OCR |
| 📋 **Expense Management** | View, search, and swipe-to-delete expenses in a clean list view |
| 📊 **Analytics Dashboard** | Visualize spending with interactive Pie Charts (by category) and Bar Charts (by month) |
| 📁 **CSV Export** | Export all expenses to a CSV file and share via any app |
| 🔍 **Smart Search** | Filter expenses instantly by merchant name |
| 🏷️ **Categories** | Organize expenses into Food, Travel, Shopping, Bills, Health, Education & more |
| 🗑️ **Data Management** | Clear all data with a single tap (with confirmation dialog) |
| 🔒 **Offline & Private** | All data stored locally on-device using Room Database |

---

## 🏗️ Architecture & Tech Stack

```
┌─────────────────────────────────────────────┐
│                    UI Layer                  │
│  Fragments  •  ViewBinding  •  Material 3   │
├─────────────────────────────────────────────┤
│               ViewModel Layer               │
│    LiveData  •  StateFlow  •  Coroutines    │
├─────────────────────────────────────────────┤
│               Repository Layer              │
│           ExpenseRepository (SSOT)          │
├─────────────────────────────────────────────┤
│                Data Layer                   │
│    Room DB  •  DAO  •  ML Kit OCR Engine    │
└─────────────────────────────────────────────┘
```

### Libraries & Dependencies

| Library | Purpose |
|---------|---------|
| [Kotlin](https://kotlinlang.org/) | Primary language |
| [Room Database](https://developer.android.com/training/data-storage/room) | Local persistence with SQLite |
| [CameraX](https://developer.android.com/training/camerax) | Camera capture API |
| [Google ML Kit](https://developers.google.com/ml-kit/vision/text-recognition) | On-device OCR text recognition |
| [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) | Pie & Bar chart visualizations |
| [Jetpack Navigation](https://developer.android.com/guide/navigation) | Fragment navigation with SafeArgs |
| [Material Design 3](https://m3.material.io/) | UI components & theming |
| [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) | Asynchronous programming |

---

## 📂 Project Structure

```
com.ayush.snap2sheet/
├── data/
│   ├── Expense.kt              # Room Entity
│   ├── ExpenseDao.kt           # Data Access Object
│   ├── ExpenseDatabase.kt      # Room Database
│   ├── ExpenseRepository.kt    # Repository pattern (SSOT)
│   └── ReportData.kt           # Analytics data classes
├── di/
│   └── ...                     # Dependency Injection
├── ui/
│   ├── scan/
│   │   ├── ScanFragment.kt     # Camera capture & OCR
│   │   └── ScanViewModel.kt
│   ├── expenses/
│   │   ├── ExpensesFragment.kt # Expense list with search
│   │   ├── ExpenseAdapter.kt   # RecyclerView adapter
│   │   └── ExpensesViewModel.kt
│   ├── analytics/
│   │   ├── AnalyticsFragment.kt # Charts & spending insights
│   │   └── AnalyticsViewModel.kt
│   └── settings/
│       ├── SettingsFragment.kt  # CSV export & data clear
│       └── SettingsViewModel.kt
├── utils/
│   ├── CsvExporter.kt          # CSV file generation
│   └── ViewModelFactory.kt     # ViewModel factory
├── MainActivity.kt             # Host activity with nav
└── Snap2SheetApp.kt            # Application class
```

---

## ⚙️ Setup & Installation

### Prerequisites
- **Android Studio** Dolphin (2021.3.1) or later
- **JDK 11**
- Android device or emulator with **API 24+**

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/ayush-godara/Snap2Sheet.git
   cd Snap2Sheet
   ```

2. **Open in Android Studio**
   - File → Open → Select the cloned directory
   - Wait for Gradle sync to complete

3. **Build & Run**
   - Select a target device/emulator (API 24+)
   - Click ▶️ Run or press `Shift + F10`

4. **Grant Permissions**
   - The app requires **Camera** permission to scan receipts
   - **Storage** permission is needed for CSV export

---

## 📖 How It Works

1. **Scan** → Open the Scan tab and tap "Capture from Camera" to photograph a receipt
2. **Extract** → Google ML Kit OCR processes the image and auto-fills merchant name, amount, and date
3. **Review & Save** → Verify the extracted data, select a category, and save the expense
4. **Track** → View all saved expenses in the Expenses tab with search functionality
5. **Analyze** → Check the Analytics tab for spending breakdowns by category and month
6. **Export** → Go to Settings and export your data as a CSV file to share or archive

---

## 🤝 Contributing

Contributions are welcome! Feel free to:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

<p align="center">
  Built with ❤️ by <a href="https://github.com/ayush-godara">Ayush</a>
</p>
