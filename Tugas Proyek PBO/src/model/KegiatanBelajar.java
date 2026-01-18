package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;

/**
 * Class KegiatanBelajar
 * Menerapkan PEWARISAN (extends Kegiatan)
 * Menerapkan POLIMORFISME (override tampilkanDetail)
 */
public class KegiatanBelajar extends Kegiatan {
    // Atribut tambahan khusus untuk kegiatan belajar
    private String mataKuliah;
    private String topik;
    private int durasi; // dalam menit

    // Constructor
    public KegiatanBelajar(int id, String judul, LocalDate tanggal,
                           LocalTime waktuMulai, LocalTime waktuSelesai,
                           String mataKuliah, String topik) {
        super(id, judul, tanggal, waktuMulai, waktuSelesai);
        this.mataKuliah = mataKuliah;
        this.topik = topik;

        // Hitung durasi otomatis
        Duration duration = Duration.between(waktuMulai, waktuSelesai);
        this.durasi = (int) duration.toMinutes();
    }

    // POLIMORFISME - Override method abstract dari parent
    @Override
    public String tampilkanDetail() {
        return String.format(
                "=== KEGIATAN BELAJAR ===\n" +
                        "Judul: %s\n" +
                        "Mata Kuliah: %s\n" +
                        "Topik: %s\n" +
                        "Tanggal: %s\n" +
                        "Waktu: %s - %s (%d menit)\n" +
                        "Status: %s",
                getJudul(), mataKuliah, topik,
                getTanggal(), getWaktuMulai(), getWaktuSelesai(),
                durasi, getStatus()
        );
    }

    // Method khusus untuk KegiatanBelajar
    public String getJenisKegiatan() {
        return "Belajar";
    }

    // Getter dan Setter
    public String getMataKuliah() {
        return mataKuliah;
    }

    public void setMataKuliah(String mataKuliah) {
        this.mataKuliah = mataKuliah;
    }

    public String getTopik() {
        return topik;
    }

    public void setTopik(String topik) {
        this.topik = topik;
    }

    public int getDurasi() {
        return durasi;
    }

    // Update durasi jika waktu berubah
    public void updateDurasi() {
        Duration duration = Duration.between(getWaktuMulai(), getWaktuSelesai());
        this.durasi = (int) duration.toMinutes();
    }
}