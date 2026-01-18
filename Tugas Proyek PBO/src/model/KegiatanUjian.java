package model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Class KegiatanUjian
 * Menerapkan PEWARISAN (extends Kegiatan)
 * Menerapkan POLIMORFISME (override tampilkanDetail)
 */
public class KegiatanUjian extends Kegiatan {
    // Atribut tambahan khusus untuk ujian
    private String mataKuliah;
    private String ruangan;
    private String jenisUjian; // "UTS", "UAS", "Quiz", "Praktikum"
    private String materiUjian;

    // Constructor
    public KegiatanUjian(int id, String judul, LocalDate tanggal,
                         LocalTime waktuMulai, LocalTime waktuSelesai,
                         String mataKuliah, String ruangan,
                         String jenisUjian, String materiUjian) {
        super(id, judul, tanggal, waktuMulai, waktuSelesai);
        this.mataKuliah = mataKuliah;
        this.ruangan = ruangan;
        this.jenisUjian = jenisUjian;
        this.materiUjian = materiUjian;
    }

    // POLIMORFISME - Override method abstract dari parent
    @Override
    public String tampilkanDetail() {
        return String.format(
                "=== UJIAN ===\n" +
                        "Judul: %s\n" +
                        "Mata Kuliah: %s\n" +
                        "Jenis: %s\n" +
                        "Tanggal: %s\n" +
                        "Waktu: %s - %s\n" +
                        "Ruangan: %s\n" +
                        "Materi: %s\n" +
                        "Status: %s",
                getJudul(), mataKuliah, jenisUjian,
                getTanggal(), getWaktuMulai(), getWaktuSelesai(),
                ruangan, materiUjian, getStatus()
        );
    }

    // Method khusus
    public String getJenisKegiatan() {
        return "Ujian";
    }

    public boolean isUjianHariIni() {
        return getTanggal().isEqual(LocalDate.now());
    }

    // Getter dan Setter
    public String getMataKuliah() {
        return mataKuliah;
    }

    public void setMataKuliah(String mataKuliah) {
        this.mataKuliah = mataKuliah;
    }

    public String getRuangan() {
        return ruangan;
    }

    public void setRuangan(String ruangan) {
        this.ruangan = ruangan;
    }

    public String getJenisUjian() {
        return jenisUjian;
    }

    public void setJenisUjian(String jenisUjian) {
        this.jenisUjian = jenisUjian;
    }

    public String getMateriUjian() {
        return materiUjian;
    }

    public void setMateriUjian(String materiUjian) {
        this.materiUjian = materiUjian;
    }
}