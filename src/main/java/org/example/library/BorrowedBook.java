package org.example.library;

import java.time.LocalDate;
import java.util.Objects;

public class BorrowedBook {
    private int id;
    private LocalDate borrowedDate;
    private int duration;
    private int extensionCount;  // Baru ditambahkan

    public LocalDate getReturnDate() {
        return borrowedDate.plusDays(duration);
    }

    // Other getters and setters
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

    public int getExtensionCount() {  // Getter untuk extensionCount
        return extensionCount;
    }

    public void setExtensionCount(int extensionCount) {  // Setter untuk extensionCount
        this.extensionCount = extensionCount;
    }

    public void incrementExtensionCount() {  // Method untuk menambah extensionCount
        this.extensionCount++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowedBook that = (BorrowedBook) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BorrowedBook{" +
                "id=" + id +
                ", borrowedDate=" + borrowedDate +
                ", duration=" + duration +
                ", extensionCount=" + extensionCount +  // Tambahkan ke toString
                '}';
    }
}
