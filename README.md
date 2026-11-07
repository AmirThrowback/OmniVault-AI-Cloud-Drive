# OmniVault - AI Cloud Drive & Encrypted Vault for Android

A smart personal cloud drive and client-encrypted vault built with Kotlin and Jetpack Compose for Android. Securely organize personal data, inspect documents with Gemini visual & OCR intelligence, listen to files with Text-To-Speech (TTS), and explore cloud storage architectures (Cloudflare R2, AWS S3, Supabase, and self-hosted Nextcloud/MinIO).

## 🚀 Key Features

1. **Smart Cloud Drive & Vault:**
   - Organize files, documents, photos, and secure notes.
   - Real-time search across filenames, tags, OCR extracted text, and document contents.
   - Storage usage quota indicator with category breakdowns.
   - Client-side zero-knowledge AES-256 encrypted vault with PIN lock protection.

2. **AI Visual & Document Intelligence Engine:**
   - Automatic classification (Receipts, Invoices, Government IDs, Source Code, Photographs).
   - High-precision OCR transcription and bilingual (Hindi/English) summary.
   - Automated safety and sensitivity assessment.
   - Key entity and data point extraction.

3. **Text-To-Speech (TTS) Narration:**
   - Built-in audio narration of files, notes, and OCR summaries.
   - Audio playback controls with play/pause and rate adjustments.

4. **OmniVault AI Cloud Storage Assistant:**
   - Multi-turn conversational AI consultant.
   - Answers how to design, host, and run cost-efficient online cloud drives.
   - In-depth comparisons of zero-egress Cloudflare R2 vs AWS S3 vs Supabase Storage vs self-hosted homelabs.

5. **Cloud Storage Architecture & Egress Cost Dashboard:**
   - Interactive capacity slider and monthly storage + bandwidth cost calculator.
   - Detailed architectural blueprints, durability metrics, and protocol specs.

## 📱 Tech Stack

- **Platform:** Android (minSdk 26, targetSdk 35)
- **Language:** Kotlin 2.0
- **UI Framework:** Jetpack Compose with Material Design 3 (M3)
- **Architecture:** Clean Architecture & MVVM (StateFlow, Coroutines)
- **Vector Assets & Adaptive Icon:** Custom adaptive launcher icon with Material You layers
