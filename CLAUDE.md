# CLAUDE.md

## Layout

- One Maven module per year (`2015`, `2019`, ..., `2025`), each depending on `utility`.
- Day N lives in `<year>/src/main/java/dayN/DayN.java` with a `public static void execute()` that prints its answers.
- `<year>/src/main/java/Application.java` calls the day currently being worked on.
- Input is read via `FileReader.getFileReader().readFile("inputN.csv")` from `<year>/src/main/resources/inputN.csv`.
- New year modules must also be added to the Maven projects list in `.idea/misc.xml`.

## Puzzle input

Fetch the personal input yourself; do not ask the user to download it.

1. Open `https://adventofcode.com/<year>/day/<N>/input` in the built-in browser pane (the user is logged in there).
2. If the page says "Puzzle inputs differ by user. Please log in", ask the user to log in in the browser pane.
3. Save the input to `<year>/src/main/resources/inputN.csv`.
4. Verify the saved file against the page (line count plus a hash computed in the browser) before using it.

## Submitting answers

Submit answers on `https://adventofcode.com/<year>/day/<N>` (browser pane, `level` 1 or 2 form) without asking, but only when sure:

- The solution gives the expected result on every example in the puzzle text.
- The answer is confirmed by an independent check (e.g. a brute-force or second implementation).
- For part 2, the part 2 text has been read after part 1 was accepted, and the implementation matches it.

If any of these fails, do not submit; report to the user instead. After submitting, read the response and report whether it was accepted.

## Branches and commits

Each year has its own branch `feature/<year>` (branched from `master`, merged back via PR). One commit per step, short subject, no body:

- `Advent of code <year> initial files`: module pom, `Application.java`, Day 1 skeleton, empty `input1.csv`
- `Day N init`: `DayN.java` skeleton (only reads `inputN.csv`), empty `inputN.csv`, `Application.java` switched to day N
- `Day N` when both parts are done in one go, otherwise `Day Na` and `Day Nb`

The puzzle text for part 2 is only visible on `https://adventofcode.com/<year>/day/<N>` after part 1 is solved.
