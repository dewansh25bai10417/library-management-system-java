# Library Management System (Java, CLI)

A command-line library management system built for the CSE2006 Programming in Java flipped-course project. It lets a librarian add/search books, register student and faculty members, issue and return books, and see which books are overdue — all through a terminal menu, with data persisted to plain text files between runs.

## Features

- **Book management** — add, remove and search books by title or author
- **Member management** — register members as either Student or Faculty (each has a different borrowing limit, loan period and fine rate)
- **Issue / return** — issues a book to a member with a due date, calculates a late fine on return
- **Overdue tracking** — a background thread checks every 60 seconds for overdue books and prints a reminder without blocking the menu; option 7 shows the full overdue list on demand
- **Persistent storage** — books, members and transaction history are saved to `data/*.txt` files using buffered file I/O, so the catalog survives a restart

## Technologies used

- Java 17+ (Standard Edition only, no external libraries)
- Core concepts used: abstract classes and interfaces, inheritance and polymorphism, custom checked exceptions, `HashMap`/`ArrayList` collections, multithreading (`Runnable`, daemon thread, `synchronized`), buffered file I/O, `java.time` for dates

## Project structure

```
LibraryManagementSystem/
├── src/library/
│   ├── Main.java                 - CLI menu, program entry point
│   ├── model/                    - Book, Member (+ Student/Faculty), Transaction, LibraryItem
│   ├── service/                  - Library (core logic), Issuable interface, OverdueChecker (thread)
│   ├── util/                     - FileManager (all file read/write)
│   └── exceptions/                - custom checked exceptions
├── data/                          - books.txt / members.txt / transactions.txt (auto-created on first run)
├── README.md
└── statement.md
```

## How to set up and run

### 1. Prerequisites
- JDK 17 or later installed. Check with:
  ```
  java -version
  javac -version
  ```
  If these aren't found, install a JDK first (e.g. `sudo apt install openjdk-17-jdk` on Ubuntu, or download from [Adoptium](https://adoptium.net/)).

### 2. Clone the repository
```
git clone https://github.com/<your-username>/<repo-name>.git
cd <repo-name>
```

### 3. Compile
From the project root:
```
javac -d out $(find src -name "*.java")
```
On Windows (PowerShell), use instead:
```
javac -d out (Get-ChildItem -Recurse -Filter *.java -Path src).FullName
```

### 4. Run
```
java -cp out library.Main
```

The program creates a `data/` folder on first launch and writes `books.txt`, `members.txt` and `transactions.txt` there automatically — no manual setup needed.

### 5. Using the program
On launch you'll see a numbered menu. Enter the number for whatever you want to do (add a book, register a member, issue/return a book, etc.) and follow the prompts. Enter `0` to save and exit.

Suggested first run to try it out:
1. Option `1` → add a book (give it a Book ID like `B1`)
2. Option `4` → register a member (Member ID like `M1`, type `1` for Student)
3. Option `5` → issue `B1` to `M1`
4. Option `8` → view all books and check the available copy count went down
5. Option `6` → return `B1` from `M1`

### Testing
There's no separate test framework — validation is done through the exception-handling paths (invalid book ID, member not found, no copies available, borrow limit exceeded all show a friendly error instead of crashing). Manually run through the "suggested first run" steps above to confirm the core flow works end to end.

## Notes on design

- **Why file-based storage instead of a database?** The project intentionally sticks to core Java (no JDBC/external DB) so it stays runnable from any terminal with just a JDK — no database server setup required for evaluation.
- **Why pipe-delimited (`|`) text files?** Book titles and author names can contain commas, so `|` is used as the field separator to avoid parsing bugs.
- Fine rates: Students are charged ₹5/day late (max 3 books, 14-day loan), Faculty ₹2/day late (max 5 books, 30-day loan).
