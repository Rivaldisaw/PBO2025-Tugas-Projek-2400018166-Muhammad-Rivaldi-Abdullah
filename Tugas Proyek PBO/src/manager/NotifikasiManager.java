package manager;

import model.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * NotifikasiManager
 * Mengelola notifikasi dan reminder untuk kegiatan
 */
public class NotifikasiManager {
    private JadwalManager jadwalManager;
    private List<String> notifikasiAktif;

    // Constructor
    public NotifikasiManager(JadwalManager jadwalManager) {
        this.jadwalManager = jadwalManager;
        this.notifikasiAktif = new ArrayList<>();
    }

    // Cek semua deadline dan buat notifikasi
    public void cekDeadline() {
        notifikasiAktif.clear();
        LocalDate today = LocalDate.now();

        // Cek tugas yang mendekati deadline
        for (KegiatanTugas tugas : jadwalManager.getKegiatanTugas()) {
            if (tugas.getProgress() < 100) { // Hanya tugas yang belum selesai
                long sisaHari = ChronoUnit.DAYS.between(today, tugas.getDeadline());

                if (sisaHari < 0) {
                    // Sudah terlambat
                    notifikasiAktif.add(String.format(
                            "⚠️ TERLAMBAT: Tugas '%s' - Deadline: %s",
                            tugas.getJudul(), tugas.getDeadline()
                    ));
                } else if (sisaHari == 0) {
                    // Hari ini deadline
                    notifikasiAktif.add(String.format(
                            "🔴 HARI INI: Deadline tugas '%s' - %s",
                            tugas.getJudul(), tugas.getMataKuliah()
                    ));
                } else if (sisaHari == 1) {
                    // Besok deadline
                    notifikasiAktif.add(String.format(
                            "🟡 BESOK: Deadline tugas '%s' - %s",
                            tugas.getJudul(), tugas.getMataKuliah()
                    ));
                } else if (sisaHari <= 3) {
                    // 2-3 hari lagi
                    notifikasiAktif.add(String.format(
                            "🟢 %d HARI LAGI: Deadline tugas '%s' - %s",
                            sisaHari, tugas.getJudul(), tugas.getMataKuliah()
                    ));
                }
            }
        }

        // Cek ujian yang akan datang
        for (KegiatanUjian ujian : jadwalManager.getKegiatanUjian()) {
            long sisaHari = ChronoUnit.DAYS.between(today, ujian.getTanggal());

            if (sisaHari == 0) {
                notifikasiAktif.add(String.format(
                        "📝 UJIAN HARI INI: %s - %s (%s)",
                        ujian.getJenisUjian(), ujian.getMataKuliah(), ujian.getWaktuMulai()
                ));
            } else if (sisaHari == 1) {
                notifikasiAktif.add(String.format(
                        "📚 UJIAN BESOK: %s - %s",
                        ujian.getJenisUjian(), ujian.getMataKuliah()
                ));
            } else if (sisaHari <= 7) {
                notifikasiAktif.add(String.format(
                        "📖 UJIAN %d HARI LAGI: %s - %s",
                        sisaHari, ujian.getJenisUjian(), ujian.getMataKuliah()
                ));
            }
        }

        // Cek kegiatan belajar hari ini
        for (KegiatanBelajar belajar : jadwalManager.getKegiatanBelajar()) {
            if (belajar.getTanggal().isEqual(today) &&
                    belajar.getStatus().equals("Belum Mulai")) {
                notifikasiAktif.add(String.format(
                        "📖 JADWAL BELAJAR: %s - %s (%s)",
                        belajar.getMataKuliah(), belajar.getTopik(), belajar.getWaktuMulai()
                ));
            }
        }
    }

    // Kirim reminder (bisa dipanggil setiap hari)
    public List<String> kirimReminder() {
        cekDeadline();
        return new ArrayList<>(notifikasiAktif);
    }

    // Get notifikasi aktif
    public List<String> getNotifikasiAktif() {
        return new ArrayList<>(notifikasiAktif);
    }

    // Get jumlah notifikasi
    public int getJumlahNotifikasi() {
        return notifikasiAktif.size();
    }

    // Cek apakah ada notifikasi penting (deadline hari ini atau terlambat)
    public boolean adaNotifikasiPenting() {
        return notifikasiAktif.stream()
                .anyMatch(n -> n.contains("TERLAMBAT") || n.contains("HARI INI"));
    }

    // Get notifikasi penting saja
    public List<String> getNotifikasiPenting() {
        List<String> penting = new ArrayList<>();
        for (String notif : notifikasiAktif) {
            if (notif.contains("TERLAMBAT") || notif.contains("HARI INI")) {
                penting.add(notif);
            }
        }
        return penting;
    }

    // Get summary notifikasi
    public String getSummaryNotifikasi() {
        cekDeadline();

        if (notifikasiAktif.isEmpty()) {
            return "✅ Tidak ada notifikasi";
        }

        int tugasMenunggu = 0;
        int ujianMenunggu = 0;
        int terlambat = 0;

        for (String notif : notifikasiAktif) {
            if (notif.contains("TUGAS") || notif.contains("DEADLINE")) {
                tugasMenunggu++;
            }
            if (notif.contains("UJIAN")) {
                ujianMenunggu++;
            }
            if (notif.contains("TERLAMBAT")) {
                terlambat++;
            }
        }

        StringBuilder summary = new StringBuilder();
        if (terlambat > 0) {
            summary.append(String.format("⚠️ %d Tugas Terlambat | ", terlambat));
        }
        if (tugasMenunggu > 0) {
            summary.append(String.format("📝 %d Tugas Menunggu | ", tugasMenunggu));
        }
        if (ujianMenunggu > 0) {
            summary.append(String.format("📚 %d Ujian Akan Datang", ujianMenunggu));
        }

        return summary.toString();
    }

    // Clear notifikasi
    public void clearNotifikasi() {
        notifikasiAktif.clear();
    }
}