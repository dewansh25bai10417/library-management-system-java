# Problem Statement

## Problem
Manually keeping track of library books — who has borrowed what, when it's due, and how much fine is owed — gets messy pretty fast once you're doing it on paper or a spreadsheet. A small library (like a department or hostel library) needs a simple system that keeps this organized without requiring a full database setup.

## Scope
This project is a command-line Library Management System that handles the core lending workflow: cataloging books, registering members, issuing and returning books, and tracking overdue items with automatic fine calculation. It is scoped to single-library, single-branch use with plain-text file storage — it does not cover multi-branch inventory, online reservations, or a GUI.

## Target Users
- **Librarian/admin** — the actual user of the CLI, who adds books, registers members and processes issue/return requests at a desk
- **Students and Faculty** — modeled as two member types with different borrowing limits, loan durations and fine rates, reflecting how most college libraries actually operate

## High-Level Features
1. **Book Management** — add, remove, and search the catalog by title or author, with per-book copy counts
2. **Member Management** — register members as Student or Faculty, each with different privileges
3. **Issue / Return Workflow** — issue a book (checked against availability and borrow limits), return a book (with automatic late-fine calculation based on member type)
4. **Overdue Tracking** — a background thread continuously monitors for overdue books and can generate an on-demand overdue report
5. **Persistence** — all data is saved to text files so the catalog isn't lost between runs
