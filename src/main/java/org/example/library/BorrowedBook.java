package org.example.library;

import java.time.LocalDate;

public class BorrowedBook {
    private int id;
    private LocalDate borrowedDate;
    private int duration;
    private LocalDate returnDate; // Tambahkan field ini

    // Getter dan setter untuk field returnDate
    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    // Getter dan setter lainnya
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(LocalDate borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
