package model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Abstract Class Kegiatan
 * Parent class untuk semua jenis kegiatan
 * Menerapkan ENKAPSULASI dan PEWARISAN
 */
public abstract class Kegiatan {
    // Atribut PRIVATE - Enkapsulasi
    private int id;
    private String judul;
    private LocalDate tanggal;
    private LocalTime waktuMulai;
    private LocalTime waktuSelesai;
    private String status; // "Belum Mulai", "Sedang Berjalan", "Selesai"

    // Constructor
    public Kegiatan(int id, String judul, LocalDate tanggal,
                    LocalTime waktuMulai, LocalTime waktuSelesai) {
        this.id = id;
        this.judul = judul;
        this.tanggal = tanggal;
        this.waktuMulai = waktuMulai;
        this.waktuSelesai = waktuSelesai;
        this.status = "Belum Mulai";
    }

    // Abstract Method - untuk POLIMORFISME
    // Setiap child class harus implement method ini
    public abstract String tampilkanDetail();

    // Method untuk mengubah status
    public void ubahStatus(String statusBaru) {
        if (statusBaru.equals("Belum Mulai") ||
                statusBaru.equals("Sedang Berjalan") ||
                statusBaru.equals("Selesai")) {
            this.status = statusBaru;
        }
    }

    // Getter dan Setter - Enkapsulasi
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public LocalTime getWaktuMulai() {
        return waktuMulai;
    }

    public void setWaktuMulai(LocalTime waktuMulai) {
        this.waktuMulai = waktuMulai;
    }

    public LocalTime getWaktuSelesai() {
        return waktuSelesai;
    }

    public void setWaktuSelesai(LocalTime waktuSelesai) {
        this.waktuSelesai = waktuSelesai;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s)",
                tanggal, waktuMulai, waktuSelesai, judul);
    }
}