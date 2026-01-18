package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Class KegiatanTugas
 * Menerapkan PEWARISAN (extends Kegiatan)
 * Menerapkan POLIMORFISME (override tampilkanDetail)
 */
public class KegiatanTugas extends Kegiatan {
    // Atribut tambahan khusus untuk tugas
    private String mataKuliah;
    private LocalDate deadline;
    private String prioritas; // "Tinggi", "Sedang", "Rendah"
    private int progress; // 0-100%

    // Constructor
    public KegiatanTugas(int id, String judul, LocalDate tanggal,
                         LocalTime waktuMulai, LocalTime waktuSelesai,
                         String mataKuliah, LocalDate deadline, String prioritas) {
        super(id, judul, tanggal, waktuMulai, waktuSelesai);
        this.mataKuliah = mataKuliah;
        this.deadline = deadline;
        this.prioritas = prioritas;
        this.progress = 0;
    }

    // POLIMORFISME - Override method abstract dari parent
    @Override
    public String tampilkanDetail() {
        long sisaHari = hitungSisaWaktu();
        String statusDeadline = sisaHari > 0 ? sisaHari + " hari lagi" : "TERLAMBAT!";

        return String.format(
                "=== TUGAS ===\n" +
                        "Judul: %s\n" +
                        "Mata Kuliah: %s\n" +
                        "Deadline: %s (%s)\n" +
                        "Prioritas: %s\n" +
                        "Progress: %d%%\n" +
                        "Waktu Pengerjaan: %s - %s\n" +
                        "Status: %s",
                getJudul(), mataKuliah, deadline, statusDeadline,
                prioritas, progress,
                getWaktuMulai(), getWaktuSelesai(),
                getStatus()
        );
    }

    // Method khusus untuk menghitung sisa waktu
    public long hitungSisaWaktu() {
        LocalDate sekarang = LocalDate.now();
        return ChronoUnit.DAYS.between(sekarang, deadline);
    }

    // Method untuk update progress
    public void updateProgress(int progressBaru) {
        if (progressBaru >= 0 && progressBaru <= 100) {
            this.progress = progressBaru;

            // Auto update status berdasarkan progress
            if (progressBaru == 0) {
                setStatus("Belum Mulai");
            } else if (progressBaru == 100) {
                setStatus("Selesai");
            } else {
                setStatus("Sedang Dikerjakan");
            }
        }
    }

    // Method khusus
    public String getJenisKegiatan() {
        return "Tugas";
    }

    public boolean isTerlambat() {
        return LocalDate.now().isAfter(deadline) && progress < 100;
    }

    // Getter dan Setter
    public String getMataKuliah() {
        return mataKuliah;
    }

    public void setMataKuliah(String mataKuliah) {
        this.mataKuliah = mataKuliah;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getPrioritas() {
        return prioritas;
    }

    public void setPrioritas(String prioritas) {
        this.prioritas = prioritas;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}