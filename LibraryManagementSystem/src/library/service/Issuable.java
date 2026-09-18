package library.service;

import library.exceptions.*;

// any class that can lend out items should implement this
// (kept it separate from Library so it's clear which methods are the
// "core" lending behaviour vs the extra admin stuff)
public interface Issuable {
    void issueBook(String bookId, String memberId)
            throws BookNotFoundException, MemberNotFoundException, BookNotAvailableException, BorrowLimitExceededException;

    double returnBook(String bookId, String memberId)
            throws BookNotFoundException, MemberNotFoundException;
}
