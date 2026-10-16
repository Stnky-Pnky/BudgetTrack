# BudgetTrack v0.2 — Android Studio source project

Native Android Kotlin + SQLite; offline and no internet permission. This is **source code, not an APK**. It has **not been compiled or device-tested** in this environment.

## Run
Open the folder in Android Studio with Android SDK 35 and JDK 17, sync Gradle, then run on an emulator or Android phone. Build > Build Bundle(s) / APK(s) > Build APK(s) produces a debug APK.

## Implemented
- Multiple recurring income sources and bills, classified as Fixed / Adjustable / Discretionary.
- One-time dated everyday expenses, with category totals and deletion.
- Monthly income and bills estimates, scaled period averages, debt-payment estimate (not formal DTI).
- Optional starting balance and user-defined low-balance threshold.
- 30-day dated cash-flow forecast for weekly/biweekly income or bills, monthly bills, twice-monthly entries, and quarterly/yearly entries with anchors. Highlights first threshold breach and lowest projected balance.
- On-device SQLite storage; v0.1 data migration preserves existing entries.

## Known limitations / important
- This version **does not yet send Android notifications**. Low-balance warnings are visible only inside the app.
- Weekly and biweekly schedules require an ISO occurrence date. Monthly entries use day-of-month and clamp to month end. Twice-monthly uses day-of-month plus the 15th, which may not match every employer.
- Quarterly/yearly entries need an anchor date to appear in dated forecasts.
- Paid bill tracking and reconciliation are not implemented. Scheduled bills are always deducted, so forecasts can double-count if the starting balance already reflects a payment.
- The 30-day forecast deducts recorded expenses dated today or later; earlier spending should already be in the starting balance. This is not a live bank balance.
- No editing existing records yet (delete and re-add), no export/backup, no bill notifications, no permission handling, no production tests or Play Store release signing.
- A formal DTI requires gross income and qualifying debt obligations; the current estimate is not a lender-grade DTI.
- The project uses Gradle plugin versions resolved in Android Studio; test Gradle sync and compile before using.

**Do not publish as a finished financial app.** This is an early prototype for iterative development.
