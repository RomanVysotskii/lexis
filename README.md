# Lexica v0.4 — offline-first vocabulary cards

Lexica is a local-first English/Russian flashcard app. The repository contains a mobile-first PWA and an Android Studio wrapper with a native Samsung/Android home-screen streak widget.

## Features

- **System theme:** follows the device/browser light/dark setting.
- **System language:** English UI on English-language devices; Russian fallback otherwise. Reload the app after changing the system language.
- **Study mode:** sequential cards, tap to flip, swipe left for “Still learning”, right for “Mastered”, drag-follow animation, previous-card button, progress count.
- **Quiz mode:** randomized cards, questions in either translation direction, multiple-choice answers and score summary.
- **Translation suggestions:** combines MyMemory and Google's unofficial Translate endpoint with a Lingva public-instance fallback pool. Suggested translations are displayed below the input; the input remains blank until a suggestion is tapped. Manual translation is always available.
- **Pronunciation:** speaker button streams audio from an unofficial Google Translate TTS endpoint; built-in device speech synthesis is used as fallback. Audio is not intentionally saved as a file.
- **Vocabulary management:** local IndexedDB storage, duplicate detection, delete, JSON backup export, CSV/TSV/text/JSON import. CSV-like files support auto or explicit comma, semicolon, tab and pipe delimiters, plus header detection.
- **Study streak:** tracks calendar days with at least one study/test answer. The native Android home-screen widget shows the streak on Samsung One UI and other Android launchers.
- **Progress actions:** continue with only unmastered cards or reset learned flags after confirmation; cards themselves are kept.

## Try the PWA

Serve the project root via HTTPS (GitHub Pages is supported) and open the resulting URL in Chrome on Android. Choose **Add to Home screen**. Opening `index.html` as a local file is useful for a quick desktop look, but service workers and PWA installation require HTTPS or localhost.

Card data stays in browser IndexedDB and is not uploaded to a Lexica server. When you request suggestions, the entered term is sent to external translation providers. Browser cleanup can delete local cards, so export JSON backups regularly.

## Deploy to GitHub Pages

A workflow is included at `.github/workflows/pages.yml`. Push the files to the repository's `main` branch, then open **Settings → Pages** and set the build/deployment source to **GitHub Actions** (one-time setup). Subsequent pushes deploy the PWA automatically. A second workflow builds the Android debug APK and attaches it as a GitHub Actions artifact; after a successful run, download `lexica-debug-apk` from the workflow run page. The Pages workflow publishes only `index.html`, `manifest.webmanifest`, `sw.js` and `icon.svg`.

## Build the Android wrapper and widget

1. Open `android-widget/` in a current Android Studio release. Install Android SDK 36 and use JDK 17 or newer.
2. Let Gradle sync, then run the `app` configuration on a device or emulator. This environment does not include the Android SDK, so a prebuilt APK is not included.
3. On the Samsung phone, long-press an empty home-screen area → **Widgets** → **Lexica** → add **Study streak**. One UI may place the widget under the app-name group.
4. If importing cards from the PWA, open the web app's menu → export vocabulary, then import that JSON file in the Android app. PWA browser storage and Android WebView storage are separate; JSON backup is the transfer path.

The native widget reads only the streak count/date from Android `SharedPreferences`. Vocabulary is stored in the WebView's local IndexedDB. The widget opens Lexica when tapped.

## Import format

Delimited import expects two columns: English word/phrase, Russian translation. CSV quotes and escaped double quotes are supported. Choose the file, select a delimiter or auto-detect it, set whether the first row contains headers, then import. JSON files exported by Lexica are also accepted. Duplicate word+translation pairs are skipped.

## Important limitations

- Translation providers are third-party and may fail due to CORS, rate limits, downtime or API changes. Google Translate's browser endpoint and TTS endpoint are unofficial and have no service guarantee; public Lingva instances are also unstable.
- Browser speech synthesis depends on the speech voices installed on the device.
- The Android widget refreshes when the app sends an updated streak, plus its normal periodic refresh; after long periods without opening the app, its last-known date is checked against the current day.
- The Android Studio project is source code only; no APK has been built or tested on a physical Samsung device in this environment. The included GitHub Actions workflow is set up to build a debug APK after you push the project to GitHub.
- ChatGPT's standard GitHub connector is read-only; direct file commits/pushes require a write-capable coding workflow such as Codex. The project includes a Pages workflow so deployment is automatic once the files are in your repository.
