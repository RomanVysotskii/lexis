# Lexica — offline-first vocabulary cards

Lexica is a mobile-first progressive web app (PWA) for studying English vocabulary in Russian. It works without an account and stores cards locally in the browser.

## Features

- **Two separate study directions:** English → Russian and Russian → English, each with independent progress.
- **Study sessions:** cards are ordered from oldest to newest; unfinished sessions restore the current card and counters after closing/reopening the app, while a new pass starts fresh.
- **Flashcards:** tap to flip, swipe left for “Still learning”, right for “Mastered”, previous-card control and session progress.
- **Quiz mode:** randomized English ↔ Russian multiple-choice questions. Correct answers enter the “mastered” counter, incorrect answers enter “still learning”; a card is not asked again until quiz results are reset, but it can still appear as a distractor.
- **Pronunciation:** audio from an unofficial Google Translate TTS endpoint, with device speech synthesis as fallback.
- **Translation suggestions:** MyMemory, Google's unofficial Translate endpoint and public Lingva instances. Suggestions are optional; a translation can always be entered manually.
- **Vocabulary management:** local IndexedDB storage, duplicate detection, card deletion, JSON backup/export, and import from JSON, CSV, TSV, pipe-delimited files or plain text.
- **TXT import:** one card per line in the format `word or phrase - translation or phrase`. The separator is a hyphen surrounded by spaces, so hyphens inside terms such as `well-known` are not treated as separators. Multi-word expressions on either side are supported. Re-importing a TXT list also restores its line order for matching existing cards without resetting their study progress.
- **Merge review:** asks before removing exact duplicate cards, combining different translations for the same English term, or combining English terms that share the same Russian translation.
- **Progress backup:** export vocabulary to JSON regularly. Importing a backup restores study-direction progress and quiz classifications.

## Try the PWA

Serve the project root via HTTPS (GitHub Pages is supported) and open the resulting URL in Chrome on Android or another modern browser. Choose **Add to Home screen** on supported devices. Opening `index.html` as a local file is useful for a quick desktop look, but PWA installation and service workers require HTTPS or localhost.

## Deploy to GitHub Pages

1. Open the repository on GitHub and go to **Settings → Pages**.
2. Under **Build and deployment**, choose **Deploy from a branch**.
3. Select branch `main` and folder `/ (root)`, then save.
4. Wait for GitHub Pages to publish the site, then open the URL shown on the Pages settings screen.

The app uses relative asset paths so it can also be deployed under a repository subpath.

## Data and privacy

Vocabulary cards and study progress are stored in the browser's local IndexedDB on that device. Lexica does not upload the vocabulary database to a Lexica server. Browser/site data cleanup can erase local cards, so export JSON backups regularly.

When you request translation suggestions, the entered term is sent to third-party translation providers. Pronunciation may also contact Google's unofficial text-to-speech endpoint. These services can fail, be rate-limited, or change without notice; manually entered translations and device speech synthesis remain available as fallbacks.

## Import formats

- **TXT:** `English word or phrase - Russian translation or phrase`, one card per line.
- **CSV:** comma-delimited, or semicolon-delimited.
- **TSV:** tab-delimited.
- **Pipe-delimited text:** fields separated by `|`.
- **JSON:** a Lexica backup exported by the app, or a JSON array/object with a `words` array. Each record needs an English field (`en`, `english`, or `word`) and a Russian translation field (`ru`, `translation`, or `meaning`).

The delimited formats use the first two columns for English and Russian. Select whether the first row contains headers when importing. Duplicate English–Russian pairs are skipped.

## Technical notes

The project is a self-contained PWA in `index.html`, with `manifest.webmanifest`, `sw.js` and `icon.svg`. There is no backend, build step, or native Android wrapper in the current project.
