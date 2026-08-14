# Sarkari Sewayojan - Govt Jobs & Career Portal 📱🇮🇳

**Sarkari Sewayojan** is a modern, high-performance Android application built with **Kotlin** and **Jetpack Compose (Material Design 3)** for job aspirants across India. It aggregates real-time government recruitment notices, exam results, admit cards, official answer keys, and provides specialized career utility tools.

---

## 🌟 Key Features

- **⚡ Live Job Ticker & Updates:** Real-time updates for Central & State government vacancies (SSC, UPSC, UPPSC, UPSSSC, Railways RRB, Banking IBPS, Police, Teaching, Defence).
- **📋 Admit Cards & Results:** Direct verified links to official notifications, exam city intimation, answer keys, and scorecards.
- **🧮 Government Exam Age Calculator:** Accurate eligibility calculator with age relaxation criteria for 50+ official Indian competitive exams.
- **📷 Passport Photo Maker:** Custom backgrounds (White, Light Blue), standard photo cropping (3.5x4.5 cm), and instant printable 4x6 / 8x12 sheet creation.
- **🏷️ Name & Date on Photo (DOP):** Official photo stamper required for SSC, UP Police, and NTA forms.
- **📄 Image / Document Resizer:** Compression tool to convert file sizes to exact KB/MB requirements for online recruitment portals.
- **💾 Offline Job Bookmarks:** Local persistence using Room Database to access saved posts offline without internet.

---

## 🏛️ Government Non-Affiliation Disclaimer & Official Sources

### ⚠️ Non-Government Entity Disclaimer (अस्वीकरण)
*Sarkari Sewayojan is an independent informational and educational application. We are **NOT affiliated with, associated with, authorized by, endorsed by, or in any way officially connected with any government agency, entity, or department**.*

*The application aggregates publicly available recruitment notifications and examination announcements strictly for the educational convenience of students and aspirants.*

### 🔗 Primary Official Government Sources:
- **Staff Selection Commission (SSC):** [https://ssc.gov.in](https://ssc.gov.in)
- **Union Public Service Commission (UPSC):** [https://upsc.gov.in](https://upsc.gov.in)
- **UP Public Service Commission (UPPSC):** [https://uppsc.up.nic.in](https://uppsc.up.nic.in)
- **UP Subordinate Services Selection Commission (UPSSSC):** [https://upsssc.gov.in](https://upsssc.gov.in)
- **UP Rojgar Sangam / Sewayojan Portal:** [https://sewayojan.up.nic.in](https://sewayojan.up.nic.in)
- **Railway Recruitment Boards (RRB):** [https://indianrailways.gov.in](https://indianrailways.gov.in)
- **Institute of Banking Personnel Selection (IBPS):** [https://ibps.in](https://ibps.in)
- **National Testing Agency (NTA):** [https://nta.ac.in](https://nta.ac.in)

---

## 🛠️ Tech Stack & Architecture

- **Language:** 100% Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture pattern
- **Local Persistence:** AndroidX Room Database + SQLite
- **Networking:** Retrofit 2 + OkHttp + Moshi Converter
- **Asynchronous Flow:** Kotlin Coroutines + StateFlow
- **Image Loading:** Coil Compose
- **Target SDK:** Android 14+ (API 36 Ready)

---

## 🚀 Building & Generating Play Store Release Bundle

### Generating Android App Bundle (.aab) for Google Play:
```bash
./gradlew bundleRelease
```
The output will be generated at:
`app/build/outputs/bundle/release/app-release.aab`

### Generating Release APK:
```bash
./gradlew assembleRelease
```
The output will be generated at:
`app/build/outputs/apk/release/app-release.apk`

---

## 🔒 Privacy Policy & Support
- **Privacy Policy:** [https://sarkarisewayojan.com/privacy-policy/](https://sarkarisewayojan.com/privacy-policy/)
- **Official Website:** [https://sarkarisewayojan.com](https://sarkarisewayojan.com)
- **Contact Email:** Helpdesk@sarkarisewayojan.com
