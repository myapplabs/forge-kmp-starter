# Feature spec — CSV Export (example)

This is a completed example of `specs/feature.md`.
Copy `specs/feature.md` (not this file) as your starting point.

```bash
forge feature --spec specs/my-feature.md --project .
```

---

## CSV Export

**Summary:** Add a CSV export option to the Settings screen that writes all
transactions to a `.csv` file and shares it via the system share sheet.

---

## Background

- `TransactionRepository` already exists in `shared:domain` — query all transactions from there, do not create a new data access path
- `SettingsScreen` already exists in `shared:ui` — add the export row to it, do not create a new screen
- The share sheet is platform-specific (`ACTION_SEND` on Android, `UIActivityViewController` on iOS) — use `expect/actual` in `shared:ui`
- Currency formatting already exists in the ViewModel layer — reuse it for the amount column

---

## Design

- The export row sits below the Theme row in Settings, labeled "Export transactions"
- Right side: a share icon (use `Icons.Default.Share`)
- Tapping it shows a loading indicator in the row while the file is being written, then opens the system share sheet
- No new screen needed

---

## Screens and UI

### Settings screen (modified)

- Add row: "Export transactions" with share icon on the right
- While export is in progress: row shows a circular progress indicator instead of the icon
- On error: show a snackbar — "Export failed. Please try again."

---

## Acceptance criteria

- [ ] Tapping "Export transactions" generates a `.csv` file
- [ ] CSV contains columns: Date, Description, Category, Amount, Currency
- [ ] Rows are sorted by date, newest first
- [ ] System share sheet opens with the file attached
- [ ] Loading state shown while file is being written
- [ ] Error snackbar shown if export fails
- [ ] Works on Android — share sheet accepts the file
- [ ] Unit tests cover CSV formatting (date format, amount format, special characters in description)

---

## What NOT to do

- Do not create a new screen — the export action lives entirely within the existing Settings screen
- Do not add a file browser or local file management — hand off to the system share sheet immediately
- Do not add scheduled or automatic exports — manual only
- Do not modify `TransactionRepository` — read-only access only
- No changes to `shared:domain` or `shared:data` in this run — UI and a new export utility only

---

## New dependencies

N/A — CSV generation is plain string building, no library needed. File writing
uses `java.io` on Android and `Foundation` on iOS via `expect/actual`.

---

## Technical notes

- The CSV writer lives in `shared:ui` as a pure function: `fun List<Transaction>.toCsv(): String` — no ViewModel logic, easy to unit test
- The share sheet trigger is an `expect/actual`: `expect fun shareFile(context: PlatformContext, content: String, filename: String)`
- Emit the share trigger as a `UiEffect` from the ViewModel — do not call the platform API directly from the Composable
- Use ISO 8601 for dates in the CSV (`2026-10-06`) so the file is importable by spreadsheet apps without locale issues
