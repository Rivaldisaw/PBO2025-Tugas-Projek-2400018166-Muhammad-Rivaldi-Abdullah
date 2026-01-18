package manager;

import model.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * StatistikManager
 * Mengelola perhitungan statistik dan analisis data kegiatan
 */
public class StatistikManager {
    private JadwalManager jadwalManager;

    // Constructor
    public StatistikManager(JadwalManager jadwalManager) {
        this.jadwalManager = jadwalManager;
    }

    // ============ STATISTIK JAM BELAJAR ============

    // Hitung total jam belajar (semua kegiatan belajar yang sudah selesai)
    public double hitungTotalJamBelajar() {
        return jadwalManager.getKegiatanBelajar().stream()
                .filter(k -> k.getStatus().equals("Selesai"))
                .mapToDouble(k -> k.getDurasi() / 60.0) // Convert menit ke jam
                .sum();
    }

    // Hitung total jam belajar minggu ini
    public double hitungJamBelajarMingguIni() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        return jadwalManager.getKegiatanBelajar().stream()
                .filter(k -> k.getStatus().equals("Selesai"))
                .filter(k -> {
                    LocalDate tgl = k.getTanggal();
                    return !tgl.isBefore(startOfWeek) && !tgl.isAfter(endOfWeek);
                })
                .mapToDouble(k -> k.getDurasi() / 60.0)
                .sum();
    }

    // Hitung total jam belajar bulan ini
    public double hitungJamBelajarBulanIni() {
        LocalDate today = LocalDate.now();
        int bulanIni = today.getMonthValue();
        int tahunIni = today.getYear();

        return jadwalManager.getKegiatanBelajar().stream()
                .filter(k -> k.getStatus().equals("Selesai"))
                .filter(k -> k.getTanggal().getMonthValue() == bulanIni &&
                        k.getTanggal().getYear() == tahunIni)
                .mapToDouble(k -> k.getDurasi() / 60.0)
                .sum();
    }

    // Hitung jam belajar per hari dalam seminggu
    public Map<String, Double> hitungJamBelajarPerHari() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);

        String[] namaHari = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"};
        Map<String, Double> jamPerHari = new LinkedHashMap<>();

        for (int i = 0; i < 7; i++) {
            LocalDate hari = startOfWeek.plusDays(i);
            double totalJam = jadwalManager.getKegiatanBelajar().stream()
                    .filter(k -> k.getStatus().equals("Selesai"))
                    .filter(k -> k.getTanggal().isEqual(hari))
                    .mapToDouble(k -> k.getDurasi() / 60.0)
                    .sum();
            jamPerHari.put(namaHari[i], totalJam);
        }

        return jamPerHari;
    }

    // ============ STATISTIK MATA KULIAH ============

    // Hitung mata kuliah yang paling banyak dipelajari
    public Map<String, Integer> getMataKuliahTerbanyak() {
        Map<String, Integer> counter = new HashMap<>();

        // Count dari KegiatanBelajar
        for (KegiatanBelajar kb : jadwalManager.getKegiatanBelajar()) {
            String mk = kb.getMataKuliah();
            counter.put(mk, counter.getOrDefault(mk, 0) + 1);
        }

        // Sort berdasarkan count (descending)
        return counter.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    // Hitung total jam belajar per mata kuliah
    public Map<String, Double> getJamBelajarPerMataKuliah() {
        Map<String, Double> jamPerMK = new HashMap<>();

        for (KegiatanBelajar kb : jadwalManager.getKegiatanBelajar()) {
            if (kb.getStatus().equals("Selesai")) {
                String mk = kb.getMataKuliah();
                double jam = kb.getDurasi() / 60.0;
                jamPerMK.put(mk, jamPerMK.getOrDefault(mk, 0.0) + jam);
            }
        }

        // Sort descending
        return jamPerMK.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    // ============ STATISTIK TUGAS ============

    // Hitung persentase penyelesaian tugas
    public double hitungPersentaseTugasSelesai() {
        List<KegiatanTugas> semuaTugas = jadwalManager.getKegiatanTugas();
        if (semuaTugas.isEmpty()) return 0;

        long tugasSelesai = semuaTugas.stream()
                .filter(t -> t.getProgress() == 100)
                .count();

        return (tugasSelesai * 100.0) / semuaTugas.size();
    }

    // Get jumlah tugas per status
    public Map<String, Integer> getStatusTugas() {
        Map<String, Integer> statusCount = new LinkedHashMap<>();
        statusCount.put("Selesai", 0);
        statusCount.put("Sedang Dikerjakan", 0);
        statusCount.put("Belum Mulai", 0);
        statusCount.put("Terlambat", 0);

        for (KegiatanTugas tugas : jadwalManager.getKegiatanTugas()) {
            if (tugas.getProgress() == 100) {
                statusCount.put("Selesai", statusCount.get("Selesai") + 1);
            } else if (tugas.isTerlambat()) {
                statusCount.put("Terlambat", statusCount.get("Terlambat") + 1);
            } else if (tugas.getProgress() > 0) {
                statusCount.put("Sedang Dikerjakan", statusCount.get("Sedang Dikerjakan") + 1);
            } else {
                statusCount.put("Belum Mulai", statusCount.get("Belum Mulai") + 1);
            }
        }

        return statusCount;
    }

    // Hitung rata-rata progress tugas
    public double hitungRataRataProgressTugas() {
        List<KegiatanTugas> tugas = jadwalManager.getKegiatanTugas();
        if (tugas.isEmpty()) return 0;

        return tugas.stream()
                .mapToInt(KegiatanTugas::getProgress)
                .average()
                .orElse(0);
    }

    // ============ ANALISIS PRODUKTIVITAS ============

    // Hitung skor produktivitas (0-100)
    public int analisisProduktivitas() {
        double jamBelajarMingguIni = hitungJamBelajarMingguIni();
        double persentaseTugasSelesai = hitungPersentaseTugasSelesai();
        int jumlahTerlambat = jadwalManager.getTugasTerlambat().size();

        // Formula sederhana:
        // - Jam belajar: max 40 poin (1 jam = 2 poin, max 20 jam)
        // - Tugas selesai: max 40 poin
        // - Penalty terlambat: -5 poin per tugas

        double skorJamBelajar = Math.min(jamBelajarMingguIni * 2, 40);
        double skorTugas = (persentaseTugasSelesai / 100.0) * 40;
        double penaltyTerlambat = jumlahTerlambat * 5;

        int totalSkor = (int) Math.max(0, Math.min(100, skorJamBelajar + skorTugas + 20 - penaltyTerlambat));

        return totalSkor;
    }

    // Get label produktivitas
    public String getLabelProduktivitas() {
        int skor = analisisProduktivitas();

        if (skor >= 90) return "Sangat Produktif 🏆";
        else if (skor >= 75) return "Produktif 🌟";
        else if (skor >= 60) return "Cukup Produktif ⭐";
        else if (skor >= 40) return "Kurang Produktif ⚠️";
        else return "Perlu Ditingkatkan 📉";
    }

    // ============ SUMMARY STATISTIK ============

    public String getSummaryStatistik() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== SUMMARY STATISTIK ===\n\n");

        summary.append(String.format("📚 Total Jam Belajar Minggu Ini: %.1f jam\n",
                hitungJamBelajarMingguIni()));

        summary.append(String.format("📝 Tingkat Penyelesaian Tugas: %.1f%%\n",
                hitungPersentaseTugasSelesai()));

        summary.append(String.format("🎯 Produktivitas: %d%% - %s\n",
                analisisProduktivitas(), getLabelProduktivitas()));

        summary.append(String.format("⚠️ Tugas Terlambat: %d\n",
                jadwalManager.getTugasTerlambat().size()));

        return summary.toString();
    }
}